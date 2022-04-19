import sys
import time

import torch
from transformers import  AutoTokenizer, AutoModel

from torch.utils.data import TensorDataset, DataLoader
from keras.preprocessing.sequence import pad_sequences

import numpy as np
import pytorch_lightning as pl
from torch import nn

MAX_LEN = 512
bs = 8
EPOCHS = 60
SCOPE_METHOD = 'augment'
F1_METHOD = 'average'


class CustomData:
    def __init__(self):
        self.SCOPE_METHOD = SCOPE_METHOD
        self.MAX_LEN = MAX_LEN
        self.bs = bs
        self.model = torch.load("biomedBERT")


    def predict_neg(self, sentences, cues=None):
        self.sentences = sentences
        if cues != None:
            self.cues = cues
        if self.cues == None:
            raise ValueError("Need Cues Data to Generate the Scope Dataloader")
        method = SCOPE_METHOD
        do_lower_case = False
        tokenizer = AutoTokenizer.from_pretrained("microsoft/BiomedNLP-PubMedBERT-base-uncased-abstract-fulltext",
                                                  cache_dir='BiomedNLP-PubMedBERT_tokenizer', local_files_only=False)
        #tokenizer = BertTokenizer.from_pretrained('bert-base-uncased',cache_dir='bert_base_uncased_tokenizer')
        #tokenizer = AutoTokenizer.from_pretrained("emilyalsentzer/Bio_ClinicalBERT",
        #                                          cache_dir='Bio_ClinicalBERT_tokenizer')

        dl_sents = self.sentences
        dl_cues = self.cues

        sentences = dl_sents
        mytexts = []
        mycues = []
        mymasks = []
        if do_lower_case == True:
            sentences_clean = [sent.lower() for sent in sentences]
        else:
            sentences_clean = sentences

        for sent, cues in zip(sentences_clean, dl_cues):
            new_text = []
            new_cues = []
            new_masks = []
            for word, cue in zip(sent.split(), cues):
                sub_words = tokenizer.tokenize(word)
                for count, sub_word in enumerate(sub_words):
                    mask = 1
                    if count > 0:
                        mask = 0
                    new_masks.append(mask)
                    new_cues.append(cue)
                    new_text.append(sub_word)
            mymasks.append(new_masks)
            mytexts.append(new_text)
            mycues.append(new_cues)
        final_sentences = []
        final_masks = []
        if method == 'replace':
            for sent, cues in zip(mytexts, mycues):
                temp_sent = []
                for token, cue in zip(sent, cues):
                    if cue == 3:
                        temp_sent.append(token)
                    else:
                        temp_sent.append(f'[unused{cue + 1}]')
                final_sentences.append(temp_sent)
            final_masks = mymasks
        elif method == 'augment':
            for sent, cues, masks in zip(mytexts, mycues, mymasks):
                temp_sent = []
                temp_masks = []
                for token, cue, mask in zip(sent, cues, masks):
                    if cue != 3:
                        if mask == 1:
                            temp_sent.append(f'[unused{cue + 1}]')
                            temp_masks.append(1)
                            temp_sent.append(token)
                            temp_masks.append(0)

                        else:
                            temp_sent.append(f'[unused{cue + 1}]')
                            temp_masks.append(0)
                            temp_sent.append(token)
                            temp_masks.append(0)

                    else:
                        temp_masks.append(mask)
                        temp_sent.append(token)
                final_sentences.append(temp_sent)
                final_masks.append(temp_masks)
        else:
            raise ValueError("Supported methods for scope detection are:\nreplace\naugment")

        input_ids = pad_sequences([tokenizer.convert_tokens_to_ids(txt) for txt in final_sentences],
                                  maxlen=MAX_LEN, dtype="long", truncating="post", padding="post")

        final_masks = pad_sequences(final_masks,
                                    maxlen=MAX_LEN, value=0, padding="post",
                                    dtype="long", truncating="post").tolist()

        attention_masks = [[float(i > 0) for i in ii] for ii in input_ids]

        inputs = torch.LongTensor(input_ids)
        masks = torch.LongTensor(attention_masks)
        final_masks = torch.LongTensor(final_masks)

        data = TensorDataset(inputs, masks, final_masks)
        dataloader = DataLoader(data, batch_size=bs)
        # print(final_sentences, mycues)

        all_results = []
        for bacth_nb, batch in enumerate(dataloader):
            result = self.model.predict(batch, bacth_nb, 0)
            all_results.append(result)
        return all_results




class BioClinicalBertForNegationScopeDetection(pl.LightningModule):

    def __init__(self):
        super(BioClinicalBertForNegationScopeDetection, self).__init__()
        self.bert = AutoModel.from_pretrained("microsoft/BiomedNLP-PubMedBERT-base-uncased-abstract-fulltext", output_attentions=True)
        #self.bert = AutoModel.from_pretrained("emilyalsentzer/Bio_ClinicalBERT", output_attentions=True)
        #self.bert = BertModel.from_pretrained('bert-base-uncased',output_attentions =True)

        self.W = nn.Linear(self.bert.config.hidden_size, 2)
        self.num_labels = 2
        self.dropout = nn.Dropout(0.1)

    def forward(self, input_ids, attention_mask, token_type_ids):
        h = self.bert(input_ids=input_ids,
                      attention_mask=attention_mask,
                      token_type_ids=token_type_ids)[0]
        h = self.dropout(h)
        logits = self.W(h)
        return logits

    def predict(self, batch, batch_idx, dataloader_idx):
        b_input_ids, b_input_mask, b_mymasks = batch
        with torch.no_grad():
            logits = self.forward(b_input_ids, b_input_mask, token_type_ids=None)

        logits = logits.detach().cpu().numpy()
        mymasks = b_mymasks.to('cpu').numpy()

        if F1_METHOD == 'first_token':

            logits = [list(p) for p in np.argmax(logits, axis=2)]
            actual_logits = []
            for l, m in zip(logits, mymasks):
                actual_logits.append([i for i, j in zip(l, m) if j == 1])

            logits = actual_logits
            # label_ids = actual_label_ids
            # true_labels.append(label_ids)

        elif F1_METHOD == 'average':

            logits = [list(p) for p in logits]

            actual_logits = []

            for l, m in zip(logits, mymasks):

                my_logits = []
                curr_preds = []
                in_split = 0
                for i, j in zip(l, m):

                    if j == 1:
                        if in_split == 1:
                            if len(my_logits) > 0:
                                curr_preds.append(my_logits[-1])
                            mode_pred = np.argmax(np.average(np.array(curr_preds), axis=0), axis=0)
                            if len(my_logits) > 0:
                                my_logits[-1] = mode_pred
                            else:
                                my_logits.append(mode_pred)
                            curr_preds = []
                            in_split = 0
                        my_logits.append(np.argmax(i))
                    if j == 0:
                        curr_preds.append(i)
                        in_split = 1
                if in_split == 1:
                    if len(my_logits) > 0:
                        curr_preds.append(my_logits[-1])
                    mode_pred = np.argmax(np.average(np.array(curr_preds), axis=0), axis=0)
                    if len(my_logits) > 0:
                        my_logits[-1] = mode_pred
                    else:
                        my_logits.append(mode_pred)
                actual_logits.append(my_logits)
            return actual_logits
bert_finetuner = BioClinicalBertForNegationScopeDetection()

if __name__ == '__main__':

    inputFileName = "java_python_data_transfer/" + sys.argv[1] + ".txt"
    with open(inputFileName, "r") as reader:
        input = reader.read()
    inputs = input.split("\t")
    sents = inputs[0].split("###, ###")
    cues_str_list = inputs[1].split("], [")
    cues = []
    for cs_str in cues_str_list:
        cs_list = cs_str.split(", ")
        cues_per_sent = [int(c) for c in cs_list]
        cues.append(cues_per_sent)

    cd = CustomData()
    result = cd.predict_neg(sents, cues)

    with open(inputFileName, "w+") as writer:
        writer.write(str(result))
    #print(all_results)

