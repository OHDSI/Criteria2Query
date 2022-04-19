package edu.columbia.dbmi.ohdsims.service.impl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;


import edu.columbia.dbmi.ohdsims.pojo.*;
import edu.columbia.dbmi.ohdsims.service.IConceptMappingService;
import edu.columbia.dbmi.ohdsims.tool.*;
import edu.columbia.dbmi.ohdsims.util.FileUtil;
import net.sf.json.JSONArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xpath.operations.Bool;
import org.springframework.stereotype.Service;

import edu.columbia.dbmi.ohdsims.service.IInformationExtractionService;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;
import net.sf.json.JSONObject;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.swing.*;

import static edu.columbia.dbmi.ohdsims.tool.OHDSIApis.querybyconceptSetid;
import static java.lang.Math.abs;


@Service("ieService")
public class InformationExtractionServiceImpl implements IInformationExtractionService {

    private static Logger logger = LogManager.getLogger(InformationExtractionServiceImpl.class);

    @Resource
    private IConceptMappingService conceptMappingService;

    CoreNLP corenlp = new CoreNLP();
    NERTool nertool = new NERTool();
    NegReTool negtool = new NegReTool();
    LogicAnalysisTool logictool = new LogicAnalysisTool();
    RelExTool reltool = new RelExTool();
    ConceptMapping cptmap = new ConceptMapping();
    ReconTool recontool = new ReconTool();
    NegationDetection nd = new NegationDetection();

    @Override
    public Paragraph translateText(String freetext, boolean include) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Document runIE4Doc(Document doc) {
        // TODO Auto-generated method stub
        return null;
    }

    //Instantiate the Document class, and save the "initial_event", "inclusion_criteria", "exclusion_criteria"
    // after part 1 of the information extraction process, including segmentation and NER.
    @Override
    public Document translateByDoc(String initial_event, String inclusion_criteria, String exclusion_criteria) {
        Document doc = new Document();
        Map<String, Integer> distinctConceptSet = new HashMap<>();
        List<Paragraph> initialEventPas = translateByBlockSegNERConceptMapping(initial_event, distinctConceptSet);
        List<Paragraph> inclusionCriteriaPas = translateByBlockSegNERConceptMapping(inclusion_criteria, distinctConceptSet);
        List<Paragraph> exclusionCriteriaPas = translateByBlockSegNERConceptMapping(exclusion_criteria, distinctConceptSet);
        wholeTextsNegationDetection(initialEventPas, inclusionCriteriaPas, exclusionCriteriaPas);
        translateByBlockRelLogExtract(initialEventPas);
        translateByBlockRelLogExtract(inclusionCriteriaPas);
        translateByBlockRelLogExtract(exclusionCriteriaPas);
        doc.setInitial_event(initialEventPas);
        doc.setInclusion_criteria(inclusionCriteriaPas);
        doc.setExclusion_criteria(exclusionCriteriaPas);
        return doc;
    }

    public void wholeTextsNegationDetection(List<Paragraph> initialEvent, List<Paragraph> inclusionCriteria, List<Paragraph> exclusionCritiera) {
        List<List<Integer>> allNegateCues = new ArrayList<>();
        List<String> wholeTexts = new ArrayList<>();
        boolean flagNeg = false;
        int initialEventSentNum = 0, inclusionCriteriaSentNum = 0, exclusionCritieraSentNum = 0;
        for (Paragraph pa : initialEvent) {
            List<Sentence> sents = pa.getSents();
            for (Sentence s : sents) {
                List<Integer> cues = s.getNegateCues();
                allNegateCues.add(cues);
                wholeTexts.add("###"+s.getText().trim()+"###");
                if(cues.contains(1) || cues.contains(2)){
                    flagNeg = true;
                }
                initialEventSentNum++;
            }
        }
        for (Paragraph pa : inclusionCriteria) {
            List<Sentence> sents = pa.getSents();
            for (Sentence s : sents) {
                List<Integer> cues = s.getNegateCues();
                allNegateCues.add(cues);
                wholeTexts.add("###"+s.getText().trim()+"###");
                if(cues.contains(1) || cues.contains(2)){
                    flagNeg = true;
                }
                inclusionCriteriaSentNum ++;
            }
        }
        for (Paragraph pa : exclusionCritiera) {
            List<Sentence> sents = pa.getSents();
            for (Sentence s : sents) {
                List<Integer> cues = s.getNegateCues();
                allNegateCues.add(cues);
                wholeTexts.add("###"+s.getText().trim()+"###");
                if(cues.contains(1) || cues.contains(2)){
                    flagNeg = true;
                }
                exclusionCritieraSentNum++;
            }
        }
        if (flagNeg) {
            //If there is any negation cues in either initial event, inclusion criteria, or exclusion criteria
            List<List<Integer>> negateTags = nd.getNegateTag(allNegateCues, wholeTexts);


            assignNegTag(initialEvent, negateTags.subList(0, initialEventSentNum));
            int iniIncSentNum = initialEventSentNum+inclusionCriteriaSentNum;
            assignNegTag(inclusionCriteria, negateTags.subList(initialEventSentNum, iniIncSentNum));
            assignNegTag(exclusionCritiera, negateTags.subList(iniIncSentNum, iniIncSentNum+exclusionCritieraSentNum));
        }else{
            assignNegTag(initialEvent, null);
            assignNegTag(inclusionCriteria, null);
            assignNegTag(exclusionCritiera, null);
        }



    }


    public void assignNegTag(List<Paragraph> sec, List<List<Integer>> allNegateTags) {
        int i = 0;
        for (Paragraph pa : sec) {
            for (Sentence s : pa.getSents()) {
                List<Integer> negateTags = null;
                if (allNegateTags != null) {
                    negateTags = allNegateTags.get(i);
                    i++;
                }
                List<Term> primary_entities = new ArrayList<>();
                List<Term> attributes = new ArrayList<>();
                for (Term t : s.getTerms()) {
                    if (Arrays.asList(GlobalSetting.primaryEntities).contains(t.getCategorey())) { //If the term belongs to primary entities,
                        // Negation detection
                        boolean ntag = false;
                        if (negateTags != null) {
                            double neg_val = negateTags.subList(t.getIndex().get(0), t.getIndex().get(1)).
                                    stream().mapToDouble(val -> val).average().orElse(0.0);
                            ntag = neg_val>=0.5;
                        }
                        t.setNeg(ntag);
                        primary_entities.add(t);
                    } else if (Arrays.asList(GlobalSetting.atrributes).contains(t.getCategorey())) { //If the term belongs to attributes,
                        attributes.add(t);
                    }
                }
                List<Term> allterms = new ArrayList<Term>();
                allterms.addAll(primary_entities);
                allterms.addAll(attributes);
                s.setTerms(allterms);
                s.setPrimaryEntities(primary_entities);
                s.setAttributes(attributes);
            }

        }

    }


    public List<Paragraph> translateByBlockSegNERConceptMapping(String text, Map<String, Integer> conceptSet) {
        String[] pas = text.split("\n");
        List<Paragraph> spas = new ArrayList<Paragraph>();
        if (text.length() == 0) {
            return spas;
        }
        for (String p : pas) {
            if (p.trim().isEmpty()) {
                continue;
            }
            Paragraph pa = new Paragraph();
            List<String> block_text = corenlp.splitParagraph(p);//split p text into sentences.
            List<Sentence> sents = new ArrayList<Sentence>();
            // NER, relation, negation, logic are operated against sentence level
            for (String s : block_text) {
                // filter bracket
                s = s.replaceAll("-LRB-", "(");
                s = s.replaceAll("-RRB-", ")");
                s = s.replaceAll("-LSB-", "[");
                s = s.replaceAll("-RSB-", "]");
                s = s.replaceAll("-LCB-", "{");
                s = s.replaceAll("-RCB-", "}");
                Sentence sent = new Sentence(" " + s + " ");
                String crf_results = sent.getText();
                if (s.trim().split(" ").length < 3) {
                    //Recognize named entity by looking up the dictionary;
                    //If the text satisfies the specific condition, it will return <domain>input<domain/>.
                    //Otherwise, it will return the input;
                    crf_results = nertool.nerByDicLookUp(sent.getText().trim());
                }
                //If crf_results still has not been given a tag:
                if (crf_results.length() <= sent.getText().length()) {
                    //Implement NER methods based on a sequence labeling method, conditional random fields, in CoreNLP with an empirical feature set.
                    //It will return a string with annotated classification information.
                    //e.g. <Demographic>Male</Demographic> or <Demographic>female</Demographic> of <Condition>non-child bearing potential</Condition>
                    crf_results = nertool.nerByCrf(sent.getText());
                }
                Object[] items = nertool.formulateNerResult(sent.getText(), crf_results);//Extract entities, and save them as term objects.
                List<Term> terms = (List<Term>) items[0];
                List negateCues = (List) items[1];
                //boolean containNeg = (boolean) items[2];
                //Aho–Corasick for rule-based screening
                try {

                    terms = nertool.nerEnhancedByACAlgorithm(sent.getText(), terms);//Merge the terms extracted by using the Rule-based AC algorithm and those extracted by the machine learning.

                } catch (Exception e) {

                }
                //System.out.println("===> after enhanced ====>");

                //Patch the term with category “Temporal”
                terms = patchTermLevel(terms);

                String display = "";
                try {
                    display = nertool.trans4display(sent.getText(), terms, conceptSet);
                    //Concept mapping;
                    //Translate the sentence with the designated form of terms for display.
                } catch (Exception ex) {

                }
                //String display = nertool.trans2Html(crf_results);
                // displaying
                sent.setTerms(terms);
                sent.setDisplay(display);
                sent.setNegateCues(negateCues);
                sents.add(sent);
            }
            pa.setSents(sents);
            logger.info(JSONObject.fromObject(pa));
            spas.add(pa);
        }
        return spas;
    }

    public void translateByBlockRelLogExtract(List<Paragraph> sec) {
        for (Paragraph pa : sec) {
            for (Sentence sent : pa.getSents()) {
                List<Triple<Integer, Integer, String>> relations = new ArrayList<Triple<Integer, Integer, String>>();
                List<Term> primaryEntities = sent.getPrimaryEntities();
                List<Term> attributes = sent.getAttributes();
                for (Term t : primaryEntities) {
                    for (Term a : attributes){
                        String rel = "no_relation";
                        boolean relflag = false;
                        relflag = true;
                        if (relflag == true && a.getCategorey().equals("Value")) {
                            rel = "has_value";
                        }

                        //if (relflag == true && a.getCategorey().equals("Temporal")) {
                        if (a.getCategorey().equals("Temporal")) {
                            rel = "has_temporal";
                        }

                        Triple<Integer, Integer, String> triple = new Triple<Integer, Integer, String>(t.getTermId(),
                                a.getTermId(), rel);

                        if (triple.third().equals("no_relation") == false) {
                            relations.add(triple);
                        }

                    }
                }
                //relation revision
                relations = reltool.relsRevision(sent.getTerms(), relations, "has_temporal");
                relations = reltool.relsRevision(sent.getTerms(), relations, "has_value");
                //It revises the "has_temporal" relation.
                // Among all term2s that have this relation with the same term1,
                // it only saves the relation between term1 and term2 which have the shortest distance.
                sent.setRelations(relations);
                //Logistic Extraction
                List<LinkedHashSet<Integer>> logic_groups = logictool.ddep(sent.getText(), primaryEntities);
                //Detect logic "or" relation between entities, and save groups of entities which are in "or" relation into a list.
                sent.setLogic_groups(logic_groups);
            }
        }
    }

    @Override
    public List<DisplayCriterion> displayDoc(List<Paragraph> ps) {
        // TODO Auto-generated method stub
        List<DisplayCriterion> displaycriteria = new ArrayList<DisplayCriterion>();
        int i = 1;
        for (Paragraph p : ps) {
            boolean ehrstatus = false;
            DisplayCriterion d = new DisplayCriterion();
            StringBuffer sb = new StringBuffer();
            for (Sentence s : p.getSents()) {
                sb.append(s.getDisplay());
                for (Term t : s.getTerms()) {
                    if (Arrays.asList(GlobalSetting.primaryEntities).contains(t.getCategorey())) {
                        ehrstatus = true;
                    }
                }
            }
            d.setCriterion(sb.toString());
            d.setId(i++);
            d.setEhrstatus(ehrstatus);
            displaycriteria.add(d);
        }
        return displaycriteria;
    }

    @Override
    public Document patchIEResults(Document doc) {
        // TODO Auto-generated method stub
        //If initial_event exists,
        if (doc.getInitial_event() != null) {
            List<Paragraph> originalp = doc.getInitial_event();
            originalp = patchDocLevel(originalp);//Add "Demographic" term together with the "has_value" relation.
            doc.setInitial_event(originalp);
        }
        //If inclusion_criteria exists,
        if (doc.getInclusion_criteria() != null) {
            List<Paragraph> originalp = doc.getInclusion_criteria();
            originalp = patchDocLevel(originalp);//Add "Demographic" term together with the "has_value" relation.
            doc.setInclusion_criteria(originalp);
        }
        //If exclusion_criteria exists,
        if (doc.getExclusion_criteria() != null) {
            List<Paragraph> originalp = doc.getExclusion_criteria();
            originalp = patchDocLevel(originalp);//Add "Demographic" term together with the "has_value" relation.
            doc.setExclusion_criteria(originalp);
        }
        return doc;
    }


    @Override
    public Document reconIEResults(Document doc) {
        // TODO Auto-generated method stub
        if (doc.getInitial_event() != null) {
            List<Paragraph> originalp = doc.getInitial_event();
            originalp = reconOnDocLevel(originalp);
            doc.setInitial_event(originalp);
        }
        if (doc.getInclusion_criteria() != null) {
            List<Paragraph> originalp = doc.getInclusion_criteria();
            originalp = reconOnDocLevel(originalp);
            doc.setInclusion_criteria(originalp);
        }
        if (doc.getExclusion_criteria() != null) {
            List<Paragraph> originalp = doc.getExclusion_criteria();
            originalp = reconOnDocLevel(originalp);
            doc.setExclusion_criteria(originalp);
        }
        return doc;
    }

    // term-level calibration
    public List<Paragraph> reconOnDocLevel(List<Paragraph> originalp) {
        for (Paragraph p : originalp) {
            if (p.getSents() != null) {
                for (Sentence s : p.getSents()) {
                    if (s.getTerms() != null) {
                        for (int i = 0; i < s.getTerms().size(); i++) {
                            if (s.getTerms().get(i).getCategorey().equals("Condition")
                                    || s.getTerms().get(i).getCategorey().equals("Drug")
                                    || s.getTerms().get(i).getCategorey().equals("Measurement")
                                    || s.getTerms().get(i).getCategorey().equals("Procedure")
                                    || s.getTerms().get(i).getCategorey().equals("Observation")) {//If the term belongs to the concept set
                                String text = s.getTerms().get(i).getText();

                                if (recontool.isCEE(text)) {//If "and", ",", "or", or "/" exists in the text,

                                    Term t = s.getTerms().get(i);
                                    String category = t.getCategorey();
                                    String entity = t.getText();
                                    Integer start_index = t.getStart_index();
                                    Integer end_index = t.getEnd_index();
                                    List<String> concepts = recontool.resolve(t.getText());
                                    int count = 0;
                                    for (String c : concepts) {
                                        //System.out.println("=>"+c);
                                        Term ret = new Term();
                                        Integer newtId = t.getTermId() + 100 + count;
                                        ret.setTermId(newtId);
                                        ret.setText(c);
                                        ret.setNeg(t.isNeg());
                                        ret.setCategorey(t.getCategorey());
                                        ret.setStart_index(t.getStart_index());
                                        ret.setEnd_index(t.getEnd_index());
                                        s.getTerms().add(ret);
                                        count++;
                                    }


                                    s.getTerms().remove(i);
                                }
                            }

                        }
                    }
                }
            }
        }
        return originalp;
    }

    //Patch the term's category, especially for the category "Temporal".
    public List<Term> patchTermLevel(List<Term> terms) {
        for (int i = 0; i < terms.size(); i++) {
            List<String> lemmas = corenlp.getLemmasList(terms.get(i).getText());//Lemmatize the text of the term and turn it into a list of string.
            if ((lemmas.contains("day") || lemmas.contains("month") || lemmas.contains("year")) && (lemmas.contains("old") == false) && (lemmas.contains("/") == false)) {
                if (i > 0 && terms.get(i - 1).getCategorey().equals("Demographic") == false) {
                    terms.get(i).setCategorey("Temporal");
                }

            }
        }
        return terms;
    }


    // term-level calibration
    public List<Paragraph> patchDocLevel(List<Paragraph> originalp) {
        for (Paragraph p : originalp) {
            if (p.getSents() != null) {
                for (Sentence s : p.getSents()) {
                    if (s.getTerms() != null) {
                        for (int i = 0; i < s.getTerms().size(); i++) {
                            if (s.getTerms().get(i).getCategorey().equals("Value")) {
                                String text = s.getTerms().get(i).getText();
                                List<String> lemmas = corenlp.getLemmasList(text);
                                if (lemmas.contains("old") || lemmas.contains("young") || lemmas.contains("older")
                                        || lemmas.contains("younger")) {//If this "Value" term contains "old", "young", etc. after lemmatization.
                                    // if there is no age in this sentence.
                                    if (hasDemoAge(s.getTerms()) == false) {
                                        Term t = new Term();
                                        t.setCategorey("Demographic");
                                        t.setStart_index(-1);
                                        t.setEnd_index(-1);
                                        t.setNeg(false);
                                        t.setText("age");
                                        Integer assignId = s.getTerms().size();
                                        t.setTermId(assignId);
                                        s.getTerms().add(t);//Add a term "age" into the term list of this sentence.
                                        s.getRelations().add(new Triple<Integer, Integer, String>(assignId,
                                                s.getTerms().get(i).getTermId(), "has_value"));//Add a "has_value" relation between this "age" term and the "Value" term.
                                    }
                                }
                            }


                        }
                    }
                }
            }
        }
        return originalp;
    }

    //Check if "age" is in the terms after lemmatization.
    public boolean hasDemoAge(List<Term> terms) {
        for (Term t : terms) {
            List<String> lemmas = corenlp.getLemmasList(t.getText());
            if (lemmas.get(0).equals("age")) {
                return true;
            }
        }
        return false;
    }

    public Document abbrExtensionByDoc(Document doc) {
        // TODO Auto-generated method stub

        if (doc.getInitial_event() != null) {
            List<Paragraph> originalp = doc.getInitial_event();
            originalp = abbrExtension(originalp);
            doc.setInitial_event(originalp);
        }
        if (doc.getInclusion_criteria() != null) {
            List<Paragraph> originalp = doc.getInclusion_criteria();
            originalp = abbrExtension(originalp);
            doc.setInclusion_criteria(originalp);
        }
        if (doc.getExclusion_criteria() != null) {
            List<Paragraph> originalp = doc.getExclusion_criteria();
            originalp = abbrExtension(originalp);
            doc.setExclusion_criteria(originalp);
        }
        return doc;
    }

    public List<Paragraph> abbrExtension(List<Paragraph> originalp) {
        for (Paragraph p : originalp) {
            if (p.getSents() != null) {
                for (Sentence s : p.getSents()) {
                    if (s.getTerms() != null) {
                        for (int i = 0; i < s.getTerms().size(); i++) {
                            if (isAcronym(s.getTerms().get(i).getText())) {//If the term is an acronym
                                String extendphrase = cptmap.extendByUMLS(s.getTerms().get(i).getText());
                                s.getTerms().get(i).setText(extendphrase);
                            }
                        }
                    }
                }
            }
        }
        return originalp;
    }


    public boolean isAcronym(String word) {
        // if the word is less than three letters.
        if (word.length() < 3) {
            return true;
        } else {
            if (word.indexOf(" ") == -1) {
                for (int i = 0; i < word.length(); i++) {
                    if (Character.isDigit(word.charAt(i))) {
                        return true;
                    }
                }
            }
            // if all upper case
            if (Character.isUpperCase(word.charAt(1))) {
                return true;
            }
        }
        // if there is a number in the word

        return false;
    }

    @Override
    public List<String> getAllInitialEvents(Document doc) {
        List<String> initevent = new ArrayList<String>();
        List<Paragraph> initial_events = doc.getInitial_event();
        if (initial_events != null) {
            for (Paragraph p : initial_events) {
                List<Sentence> sents = p.getSents();
                if (sents != null) {
                    for (Sentence s : sents) {
                        List<Term> terms = s.getTerms();
                        if (terms != null) {
                            for (Term t : terms) {
                                if (Arrays.asList(GlobalSetting.conceptSetDomains).contains(t.getCategorey())) {
                                    initevent.add(t.getText());
                                }
                            }
                        }
                    }
                }
            }
        }
        return initevent;
    }

    public Document continueTranslateByDoc(Document doc, JSONArray iniResult, JSONArray incResult, JSONArray excResult) {
        Document newDoc = new Document();
        Map<String, Integer> distinctConceptSet = new HashMap<>();
        List<Paragraph> initialEventPas = translateByBlockNer(doc.getInitial_event(), iniResult, distinctConceptSet);
        List<Paragraph> inclusionCriteriaPas = translateByBlockNer(doc.getInclusion_criteria(), incResult, distinctConceptSet);
        List<Paragraph> exclusionCriteriaPas = translateByBlockNer(doc.getExclusion_criteria(), excResult, distinctConceptSet);
        wholeTextsNegationDetection(initialEventPas, inclusionCriteriaPas, exclusionCriteriaPas);
        translateByBlockRelLogExtract(initialEventPas);
        translateByBlockRelLogExtract(inclusionCriteriaPas);
        translateByBlockRelLogExtract(exclusionCriteriaPas);
        newDoc.setInitial_event(initialEventPas);
        newDoc.setInclusion_criteria(inclusionCriteriaPas);
        newDoc.setExclusion_criteria(exclusionCriteriaPas);
        return newDoc;
    }

    //It parses the text in a block with the process of recognizing the latest terms, detecting negations, and extracting relations.
    public List<Paragraph> translateByBlockNer(List<Paragraph> pas, JSONArray result, Map<String, Integer> conceptSetMap) {
        List<Paragraph> newPas = new ArrayList<>();
        if (result.size() == 0) {
            return newPas;
        }
        for (int j = 0; j < result.size(); j++) {
            JSONObject criterion = JSONObject.fromObject(result.get(j));
            String pasResult = criterion.getString("criterion");
            Integer id = criterion.getInt("id") - 1;//pas indexes from 0; id of criterion indexes from 1;
            List<String> sensResult = corenlp.splitParagraph(pasResult);//Split a paragraph into sentences.
            Paragraph pa = pas.get(id);
            List<Sentence> newSents = new ArrayList<>();
            for (int k = 0; k < sensResult.size(); k++) {
                String sResult = sensResult.get(k);
                sResult = sResult.replaceAll("-LRB-", "(");
                sResult = sResult.replaceAll("-RRB-", ")");
                sResult = sResult.replaceAll("-LSB-", "[");
                sResult = sResult.replaceAll("-RSB-", "]");
                sResult = sResult.replaceAll("-LCB-", "{");
                sResult = sResult.replaceAll("-RCB-", "}");

                Sentence sent = pa.getSents().get(k);
                String s = sent.getText();
                Object[] items = nertool.formulateTerms(s, sResult, conceptSetMap);//Get all terms in the sentence.
                List<Term> terms = (List<Term>) items[0];
                List negateCues = (List) items[1];

                sent.setTerms(terms);
                sent.setNegateCues(negateCues);
                newSents.add(sent);
            }
            pa.setSents(newSents);
            newPas.add(pa);
        }
        return newPas;
    }


    public Boolean compareTerms(List<Term> newTerms, List<Term> oldTerms, String userId, Long lastAccessedTime, String filePath) {
        Boolean match = true;
        int i = 0, j = 0;
        String record = "";
        if (newTerms.size() > 0 && oldTerms.size() > 0) {
            while (i + j < newTerms.size() + oldTerms.size() - 1) {
                if (newTerms.get(i).getStart_index().equals(oldTerms.get(j).getStart_index()) &&
                        newTerms.get(i).getEnd_index().equals(oldTerms.get(j).getEnd_index())) {
                    if (!newTerms.get(i).getCategorey().equals(oldTerms.get(j).getCategorey())) {
                        // System.out.println("update" + newTerms.get(i).getText());
                        record = record + userId + "," + lastAccessedTime.toString() + ",update," + oldTerms.get(j).getText() + "," +
                                GlobalSetting.domainAbbrMap.get(oldTerms.get(j).getCategorey()) + "," + oldTerms.get(j).getConceptName() + "," +
                                GlobalSetting.domainAbbrMap.get(newTerms.get(i).getCategorey()) + "," + newTerms.get(i).getConceptName() + "\n";
                        match = false;
                    } else {
                        if (Arrays.asList(GlobalSetting.conceptSetDomains).contains(newTerms.get(i).getCategorey())) {
                            if (!newTerms.get(i).getConceptId().equals(oldTerms.get(j).getConceptId())) {
                                //System.out.println("update" + newTerms.get(i).getText());
                                record = record + userId + "," + lastAccessedTime.toString() + ",update," + oldTerms.get(j).getText() + "," +
                                        GlobalSetting.domainAbbrMap.get(oldTerms.get(j).getCategorey()) + "," + oldTerms.get(j).getConceptName() + "," +
                                        GlobalSetting.domainAbbrMap.get(newTerms.get(i).getCategorey()) + "," + newTerms.get(i).getConceptName() + "\n";
                                match = false;
                            }
                        }
                    }
                    i++;
                    j++;
                } else {
                    match = false;
                    if (newTerms.get(i).getStart_index() < oldTerms.get(j).getStart_index() &&
                            newTerms.get(i).getEnd_index() < oldTerms.get(j).getStart_index()) {
                        // System.out.println("add" + newTerms.get(i).getText());
                        record = record + userId + "," + lastAccessedTime.toString() + ",add," + newTerms.get(i).getText() + "," +
                                "," + "," +
                                GlobalSetting.domainAbbrMap.get(newTerms.get(i).getCategorey()) + "," + newTerms.get(i).getConceptName() + "\n";
                        i++;
                    } else if (newTerms.get(i).getStart_index() < oldTerms.get(j).getStart_index() &&
                            newTerms.get(i).getEnd_index() >= oldTerms.get(j).getStart_index()) {
                        //System.out.println("delete" + oldTerms.get(j).getText());
                        record = record + userId + "," + lastAccessedTime.toString() + ",delete," + oldTerms.get(j).getText() + "," +
                                GlobalSetting.domainAbbrMap.get(oldTerms.get(j).getCategorey()) + "," + oldTerms.get(j).getConceptName() + "," +
                                "," + "\n";
                        // System.out.println("add" + newTerms.get(i).getText());
                        record = record + userId + "," + lastAccessedTime.toString() + ",add," + newTerms.get(i).getText() + "," +
                                "," + "," +
                                GlobalSetting.domainAbbrMap.get(newTerms.get(i).getCategorey()) + "," + newTerms.get(i).getConceptName() + "\n";
                        i++;
                        j++;
                    } else if (newTerms.get(i).getStart_index() >= oldTerms.get(j).getStart_index() &&
                            newTerms.get(i).getStart_index() <= oldTerms.get(j).getEnd_index()) {
                        // System.out.println("delete" + oldTerms.get(j).getText());
                        record = record + userId + "," + lastAccessedTime.toString() + ",delete," + oldTerms.get(j).getText() + "," +
                                GlobalSetting.domainAbbrMap.get(oldTerms.get(j).getCategorey()) + "," + oldTerms.get(j).getConceptName() + "," +
                                "," + "\n";
                        // System.out.println("add" + newTerms.get(i).getText());
                        record = record + userId + "," + lastAccessedTime.toString() + ",add," + newTerms.get(i).getText() + "," +
                                "," + "," +
                                GlobalSetting.domainAbbrMap.get(newTerms.get(i).getCategorey()) + "," + newTerms.get(i).getConceptName() + "\n";
                        i++;
                        j++;
                    } else if (newTerms.get(i).getStart_index() >= oldTerms.get(j).getEnd_index()) {
                        // System.out.println("delete" + oldTerms.get(j).getText());
                        record = record + userId + "," + lastAccessedTime.toString() + ",delete," + oldTerms.get(j).getText() + "," +
                                GlobalSetting.domainAbbrMap.get(oldTerms.get(j).getCategorey()) + "," + oldTerms.get(j).getConceptName() + "," +
                                "," + "\n";
                        j++;
                    }
                }
                if (i == newTerms.size() && j < oldTerms.size()) {
                    match = false;
                    for (int k = j; k < oldTerms.size(); k++) {
                        //System.out.println("delete" + oldTerms.get(k).getText());
                        record = record + userId + "," + lastAccessedTime.toString() + ",delete," + oldTerms.get(k).getText() + "," +
                                GlobalSetting.domainAbbrMap.get(oldTerms.get(k).getCategorey()) + "," + oldTerms.get(k).getConceptName() + "," +
                                "," + "\n";
                    }
                    break;
                } else if (j == oldTerms.size() && i < newTerms.size()) {
                    match = false;
                    for (int k = i; k < newTerms.size(); k++) {
                        //System.out.println("add" + newTerms.get(k).getText());
                        record = record + userId + "," + lastAccessedTime.toString() + ",add," + newTerms.get(k).getText() + "," +
                                "," + "," +
                                GlobalSetting.domainAbbrMap.get(newTerms.get(k).getCategorey()) + "," + newTerms.get(k).getConceptName() + "\n";
                    }
                    break;
                }
            }
        } else if (newTerms.size() > 0 && oldTerms.size() == 0) {
            match = false;
            for (int k = i; k < newTerms.size(); k++) {
                //System.out.println("add" + newTerms.get(k).getText());
                record = record + userId + "," + lastAccessedTime.toString() + ",add," + newTerms.get(i).getText() + "," +
                        "," + "," +
                        GlobalSetting.domainAbbrMap.get(newTerms.get(i).getCategorey()) + "," + newTerms.get(i).getConceptName() + "\n";
            }
        } else if (oldTerms.size() > 0 && newTerms.size() == 0) {
            match = false;
            for (int k = j; k < oldTerms.size(); k++) {
                //System.out.println("delete" + oldTerms.get(k).getText());
                record = record + userId + "," + lastAccessedTime.toString() + ",delete," + oldTerms.get(j).getText() + "," +
                        GlobalSetting.domainAbbrMap.get(oldTerms.get(j).getCategorey()) + "," + oldTerms.get(j).getConceptName() + "," +
                        "," + "\n";
            }
        }
        // System.out.println(record);
        FileUtil.add2File(filePath, record);
        return match;
    }


}
