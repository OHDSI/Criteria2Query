package edu.columbia.dbmi.ohdsims.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletContext;

import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import jnr.ffi.annotations.In;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;


import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.RuleBasedModels;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.tool.AhoCorasickDoubleArrayTrie.Hit;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import net.sf.json.JSONObject;
import weka.core.SerializationHelper;

import static edu.columbia.dbmi.ohdsims.tool.OHDSIApis.querybyconceptSetid;

public class NERTool {
    AbstractSequenceClassifier<CoreLabel> ner = CRFClassifier.getClassifierNoExceptions(GlobalSetting.crf_model);
    //AbstractSequenceClassifier class provides common functionality for (probabilistic) sequence models. It is a superclass of our CMM and CRF sequence classifiers.
    //CoreLabel class will hold information about an entity.
    //getClassifierNoExceptions will use the model specified by its argument.
    public static final String grammars = GlobalSetting.dependence_model;
    private final static String diclookup = GlobalSetting.concepthub + "/omop/searchOneEntityByTerm";
    private final static String rule_based_model = GlobalSetting.rule_base_model;
    public RuleBasedModels rbm = new RuleBasedModels();

    public NERTool() {
        try {
//	        URL realPath = Thread.currentThread().getContextClassLoader().getResource("");
//	        System.out.println("realPath:"+realPath);
//	        String decoded = URLDecoder.decode(realPath.getFile(), "UTF-8");
//	        File fileRource1 = new File(decoded, GlobalSetting.rule_base_acdat_model);
//			File fileRource2 = new File(decoded, GlobalSetting.rule_base_dict_model);
//			System.out.println("f1="+fileRource1.getAbsolutePath());
//			System.out.println("f2="+fileRource2.getAbsolutePath());
            Resource fileRource = new ClassPathResource(rule_based_model);
            this.rbm = (RuleBasedModels) SerializationHelper.read(new GZIPInputStream(fileRource.getInputStream()));//Deserialize from the given stream
            //Locate and open the resource

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        NERTool ner = new NERTool();
        String orignialstr = "Diagnosis of Type 2 Diabetes ( T2DM ) and is either drug naive or is being treated with metformin only";
        List<AhoCorasickDoubleArrayTrie.Hit<String>> wordList = ner.rbm.getAcdat().parseText(orignialstr.toLowerCase());
//		NERTool ner=new NERTool();
//		String text="Patients with pregnancy test positive .";
//		Sentence sent = new Sentence(text);
//		String crf_results=sent.getText();
//		crf_results = ner.nerByCrf(sent.getText());
//		System.out.println("crf_results="+crf_results);
//		List<Term> terms = ner.formulateNerResult(sent.getText(), crf_results);
//		ner.nerEnhancedByACAlgorithm(text, terms);
    }


    public static void train(String traindatapath, String targetpath) {
        long startTime = System.nanoTime();
        /* Step 1: learn the classifier from the training data */
        String trainFile = traindatapath;
        /* Learn the classifier from the training data */
        String serializeFileLoc = targetpath;
        // properties: https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/ie/NERFeatureFactory.html
        Properties props = new Properties();
        props.put("trainFile", trainFile); // To train with multiple files, a comma separated list
        props.put("map", "word=0,answer=1");
        props.put("useClassFeature", "true");
        props.put("useNGrams", "true");
        props.put("noMidNGrams", "true");
        props.put("maxNGramLeng", "6");
        props.put("useDisjunctive", "true");
        props.put("usePrev", "true");
        props.put("useNext", "true");
        props.put("useSequences", "true");
        props.put("usePrevSequences", "true");
        props.put("maxLeft", "1");
        props.put("useTypeSeqs", "true");
        props.put("useTypeSeqs2", "true");
        props.put("useTypeySequences", "true");
        props.put("wordShape", "chris2useLC");
        // props.put("printFeatures", "true");
        // This feature can be turned off in recent versions with the flag -useKnownLCWords false
        // https://nlp.stanford.edu/software/crf-faq.html question 13

        SeqClassifierFlags flags = new SeqClassifierFlags(props);
        CRFClassifier<CoreLabel> crf = new CRFClassifier<CoreLabel>(flags);
        crf.train();
        crf.serializeClassifier(serializeFileLoc);

    }


    /**
     * Word Dependency Author:chi Date:2017-3-22
     */
    public Collection<TypedDependency> outputDependency(Tree t) {
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        // tlp.setGenerateOriginalDependencies(true); Standford Dependency
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(t);

        Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed();

        int countforitem = 0;
        int source = 0;
        int target = 0;
        for (TypedDependency item : tdl) {
            System.out.println(item);
        }

        return tdl;

    }

    public Object[] formulateNerResult(String orignialstr, String result) {
        List<String[]> listmap = new ArrayList<String[]>();
        List<Term> terms = new ArrayList<Term>();
        //String pattern = "<(Value|Demographic|Condition|Qualifier|Measurement|Observation|Drug|Procedure|Temporal|Negation_cue)>([\\s\\S]*?)</(Value|Demographic|Condition|Qualifier|Measurement|Observation|Drug|Procedure|Temporal|Negation_cue)>";
        //String pattern = "<(Scope|Person|Condition|Drug|Observation|Measurement|Procedure|Device|Visit|Negation|Qualifier|Temporal|Value|Multiplier|Reference_point|Line|Mood)>([\\s\\S]*?)</(Scope|Person|Condition|Drug|Observation|Measurement|Procedure|Device|Visit|Negation|Qualifier|Temporal|Value|Multiplier|Reference_point|Line|Mood)>";
        String pattern = "<(Demographic|Condition|Drug|Observation|Measurement|Procedure|Device|Negation_cue|Temporal|Value)>([\\s\\S]*?)</(Demographic|Condition|Drug|Observation|Measurement|Procedure|Device|Negation_cue|Temporal|Value)>";
        String s = result;
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(s);
        int count = 0;
        while (mat.find()) { //Find the next subsequence of s that matches the pattern pat.
            count = count + 1;
            String[] arrs = new String[2];
            arrs[0] = mat.group(1);
            //Return the input subsequence captured by the first group (the part in the first parentheses in the pattern,
            // i.e. category of the term).
            arrs[1] = mat.group(2);
            //Return the input subsequence captured by the second group (the part in the second parentheses in the pattern,
            // i.e. text of the term).
            listmap.add(arrs);
        }
        int relativepos = 0;
        int termindex = 0;
        List<Integer> negateCues = new ArrayList<>();
        int length0 = 0;
        boolean containNeg = false;
        for (int i = 0; i < listmap.size(); i++) {
            List<Integer> termPosition = new ArrayList<>();//Start position and end position
            int start = orignialstr.indexOf(listmap.get(i)[1]);
            //the start position of the term in the substring of sentence where (i-1)th term and the part before it has been removed.
            int entitylength = listmap.get(i)[1].length();//length of the term
            Term term = new Term();
            term.setTermId(termindex);
            term.setCategorey(listmap.get(i)[0]);
            term.setText(listmap.get(i)[1]);
            term.setStart_index(relativepos + start);//relative start position of the term in the whole sentence
            term.setEnd_index(relativepos + start + entitylength);//relative end position of (the term+a space) in the whole sentence
            System.out.println(orignialstr.substring(0, start).trim());
            String subOriginalStr = orignialstr.substring(0, start).trim();
            if(!subOriginalStr.isEmpty()){
                length0 = subOriginalStr.split(" ").length;
            }else{
                length0 = 0;
            }
            //length0 = orignialstr.substring(0, start).trim().split(" ").length;
            negateCues.addAll(Collections.nCopies(length0, 3));
            int termLength = term.getText().trim().split(" ").length;
            if (term.getCategorey().equals("Negation_cue")) {
                containNeg = true;
                if (termLength > 1) {
                    termPosition.add(negateCues.size());
                    negateCues.addAll(Collections.nCopies(termLength, 2)); //used to be 2
                    termPosition.add(negateCues.size());
                    term.setIndex(termPosition);
                } else {
                    termPosition.add(negateCues.size());
                    negateCues.addAll(Collections.nCopies(termLength, 1));
                    termPosition.add(negateCues.size());
                    term.setIndex(termPosition);
                }
            } else {
                termPosition.add(negateCues.size());
                negateCues.addAll(Collections.nCopies(termLength, 3));
                termPosition.add(negateCues.size());
                term.setIndex(termPosition);
            }
            terms.add(term);
            termindex++;
            relativepos = relativepos + start + listmap.get(i)[1].length();
            orignialstr = orignialstr.substring(start + listmap.get(i)[1].length());//Remove (i-1)th term and the part of the sentence before it
        }
        String subOriginalStr = orignialstr.trim();
        if(!subOriginalStr.isEmpty()){
            length0 = subOriginalStr.split(" ").length;
        }else{
            length0 = 0;
        }
        negateCues.addAll(Collections.nCopies(length0, 3));
        Object[] items = new Object[]{terms, negateCues, containNeg};
        return items;
    }

    public List<Term> nerEnhancedByACAlgorithm(String orignialstr, List<Term> terms) throws Exception {

        List<AhoCorasickDoubleArrayTrie.Hit<String>> wordList = this.rbm.getAcdat().parseText(orignialstr.toLowerCase());//Get the word list by using the rule-based AC algorithm to parse the original sentence.
        //System.out.println(wordList);
        //System.out.println(orignialstr);
        Integer last_start = 0;
        Integer last_end = 0;
        List<AhoCorasickDoubleArrayTrie.Hit<String>> longest = new ArrayList<AhoCorasickDoubleArrayTrie.Hit<String>>();
        for (Hit<String> s : wordList) {
            if ((s.begin <= last_start) && (s.end >= last_end)) {//If the new word s covers the last word that we just added to the list, remove the last word in the list.
                if (longest.size() > 0) {
                    longest.remove(longest.size() - 1);
                }
            } else if (s.begin >= last_start && s.end <= last_end) {//Do not add the word s if it has been covered by the last word that we just added to the list.
                continue;
            }
            //System.out.println("s--"+s);
            longest.add(s);//Add the word to the list
            last_start = s.begin;
            last_end = s.end;
        }
        List<Term> enhancedNERResults = new ArrayList<Term>();

        List<Term> dicResults = new ArrayList<Term>();
        int temporalId = terms.size();//The length of the term
        //Convert the word list "longest" into the term list form.
        int position;
        for (Hit<String> s : longest) {
            List<Integer> termPosition = new ArrayList<>();
            Term t = new Term();
            t.setCategorey(this.rbm.getDir().get(s.value.toLowerCase().trim()));//look up dic for the Category
            if(!Arrays.asList(GlobalSetting.alldomains).contains(t.getCategorey())){
                continue;
            }
            t.setTermId(temporalId++);
            t.setText(orignialstr.substring(s.begin + 1, s.end - 1));//t.setText(s.value.trim())
            t.setStart_index(s.begin + 1);
            t.setEnd_index(s.end - 1);
            position = orignialstr.substring(0, s.begin).trim().split(" ").length;
            termPosition.add(position);
            position = orignialstr.substring(0, s.end - 1).trim().split(" ").length;
            termPosition.add(position);
            t.setIndex(termPosition);
            t.setNeg(false);
            dicResults.add(t);
            //System.out.println(s.value+"\t"+s.begin+","+s.end+"\t"+this.rbm.getDir().get(s.value.trim().toLowerCase()));
        }
        enhancedNERResults = mergeResultsfromRuleAndML(orignialstr, dicResults, terms);//Merge the term list we get from the Rule-based AC algorithm and machine learning
        return enhancedNERResults;
    }

    /**
     * Note: Author only implemented a over-simple method, it could be optimized by Segment tree
     */
    public List<Term> mergeResultsfromRuleAndML(String text, List<Term> ruleresults, List<Term> mlresults) {
        List<Term> termlist = new ArrayList<Term>();
        if (ruleresults != null) {
            termlist.addAll(ruleresults);
        }
        if (mlresults != null) {
            termlist.addAll(mlresults);
        }
        Collections.sort(termlist, new Comparator<Term>() {//Sort the term list based on the start index from small to large
            public int compare(Term t1, Term t2) {
                if (t1.getStart_index() < t2.getStart_index()) {
                    return -1;
                }
                if (t1.getStart_index() == t2.getStart_index()) {
                    return 0;
                }
                return 1;
            }
        });


        int temporalId = termlist.size();
        for (int k = 0; k < termlist.size(); k++) {
            if (termlist.get(k).getTermId() < mlresults.size()) { //If this term is extracted by using the machine learning method
                if ((k + 1) < termlist.size()) { //If this term is not the last one in the term list
                    if (termlist.get(k).getEnd_index() >= termlist.get(k + 1).getEnd_index()) { //If this term covers the term next to it, remove that term.
                        termlist.remove(k + 1);
                    } else {
                        if (termlist.get(k + 1).getStart_index() <= termlist.get(k).getEnd_index()) {
                            //If this term overlaps the term next to it, combine these two terms together,
                            //set the category of next term as the one of this new term, and remove these two terms, and
                            // insert the new term to the position k.
                            Term t = new Term();
                            t.setTermId(temporalId++);
                            t.setText(text.substring(termlist.get(k).getStart_index(), termlist.get(k + 1).getEnd_index()));
                            t.setStart_index(termlist.get(k).getStart_index());
                            t.setEnd_index(termlist.get(k + 1).getEnd_index());
                            t.setCategorey(termlist.get(k + 1).getCategorey());
                            t.setNeg(false);
                            List<Integer> termPosition = new ArrayList<>();
                            termPosition.add(termlist.get(k).getIndex().get(0));
                            termPosition.add(termlist.get(k + 1).getIndex().get(1));
                            t.setIndex(termPosition);
                            termlist.remove(k);
                            termlist.remove(k);
                            termlist.add(k, t);
                        } else {//If this term doesn't overlap the term next to it,
                            continue;
                        }
                    }
                }
            } else { //If this term is extracted by using the Rule-based AC algorithm,
                if ((k) < termlist.size() && (k - 1) >= 0) { //If this term is not the first one of the list,
                    if (termlist.get(k - 1).getEnd_index() >= termlist.get(k).getEnd_index()) { //If the previous term covers this term, remove this term from the list.
                        termlist.remove(k);
                    } else {
                        if (termlist.get(k).getStart_index() <= termlist.get(k - 1).getEnd_index()) { //If this term overlaps the previous one, remove this term from the list.
                            termlist.remove(k);
                        }
                    }
                }
                if ((k + 1) < termlist.size()) { //If this term is not the last one of the list,
                    if (termlist.get(k).getEnd_index() >= termlist.get(k + 1).getEnd_index()) { //If this term covers next term, remove next term from the list.
                        termlist.remove(k + 1);
                    } else {
                        if (termlist.get(k + 1).getStart_index() <= termlist.get(k).getEnd_index()) { //If this term overlaps next term, remove next term from the list.
                            termlist.remove(k + 1);
                        }
                    }
                }

            }
        }
        int tId = 0;
        for (Term t : termlist) {
            t.setTermId(tId++);//reset termID for all the terms in the list
        }
        List<Term> newtermlist = new ArrayList<Term>();

        int newtId = 0;
        if (termlist.size() > 0) {
            termlist.get(0).setTermId(newtId++);
            newtermlist.add(termlist.get(0));//Add the first term of termlist to the newtermlist.
        }
        for (int k = 1; k < termlist.size(); k++) {
            if ((termlist.get(k).getStart_index() >= termlist.get(k - 1).getStart_index())
                    && (termlist.get(k).getEnd_index() <= termlist.get(k - 1).getEnd_index())) {
                System.out.println("ktermlist " + termlist.get(k).getText());
                System.out.println("k-1termlist " + termlist.get(k - 1).getText());

                termlist.remove(k);
                k--;
                continue;

            }
            termlist.get(k).setTermId(newtId++);
            newtermlist.add(termlist.get(k));
        }
        return newtermlist;
    }

    public String nerByCrf(String str) {
        String results = ner.classifyWithInlineXML(str);
        //Implement NER methods based on a sequence labeling method, conditional random fields, in CoreNLP with an empirical feature set.
        //It will return a string with annotated classification information.
        //e.g. <Demographic>Male</Demographic> or <Demographic>female</Demographic> of <Condition>non-child bearing potential</Condition>
        //System.out.println(results);
        results = results.replace("<0>", "");
        results = results.replace("</0>", "");

        return results;
    }

    public String nerByDicLookUp(String str) {
        System.out.println("======nerByDicLookUp=====");
        String res = str;
        if (str.trim().toLowerCase().equals("male") || str.trim().toLowerCase().equals("female") || str.trim().toLowerCase().equals("women") || str.trim().toLowerCase().equals("men") || str.trim().toLowerCase().equals("man") || str.trim().toLowerCase().equals("woman")) {
            res = "<" + "Demographic" + ">" + str + "</" + "Demographic" + ">";
            return res;
        }

        JSONObject jo = new JSONObject();
        jo.accumulate("term", str);

        String result = HttpUtil.doPost(diclookup, jo.toString());
        //System.out.println("result="+result);//e.g result={"matchScore":0.65110296,"concept":{"conceptId":46235215,"conceptName":"Gender identity","domainId":"Measurement","vocabularyId":"LOINC","conceptClassId":"Clinical Observation","standardConcept":"S","conceptCode":"76691-5","validStartDate":"20150313","validEndDate":"20991231","invalidReason":null,"parentCount":2,"childCount":0,"additionalInformation":""},"term":"Woman"}
        //Creates a JSONObject.
        JSONObject bestconcept = JSONObject.fromObject(result);
        try {
            if (bestconcept.getDouble("matchScore") > 0.75) {
                JSONObject concept_jo = bestconcept.getJSONObject("concept");
                String domain = concept_jo.getString("domainId");
                res = "<" + domain + ">" + str + "</" + domain + ">";
            }

        } catch (Exception ex) {

        }
        //System.out.println("dic_result="+res);
        return res;
    }


    public String trans2Html(String result) {
        result = result.replace("<Condition>", "<mark data-entity=\"condition\">");
        result = result.replace("</Condition>", "</mark>");
        result = result.replace("<Drug>", "<mark data-entity=\"drug\">");
        result = result.replace("</Drug>", "</mark>");
        result = result.replace("<Procedure>", "<mark data-entity=\"procedure\">");
        result = result.replace("</Procedure>", "</mark>");
        result = result.replace("<Observation>", "<mark data-entity=\"observation\">");
        result = result.replace("</Observation>", "</mark>");
        result = result.replace("<Measurement>", "<mark data-entity=\"measurement\">");
        result = result.replace("</Measurement>", "</mark>");
        result = result.replace("<Temporal>", "<mark data-entity=\"temporal\">");
        result = result.replace("</Temporal>", "</mark>");
        result = result.replace("<Negation_cue>", "<mark data-entity=\"negation_cue\">");
        result = result.replace("</Negation_cue>", "</mark>");
        result = result.replace("<Demographic>", "<mark data-entity=\"demographic\">");
        result = result.replace("</Demographic>", "</mark>");
        result = result.replace("<Value>", "<mark data-entity=\"value\">");
        result = result.replace("</Value>", "</mark>");
        return result;
    }


    public String trans4display(String text, List<Term> terms, Map<String, Integer> conceptSet) {
        String sent = text;
        StringBuffer sb = new StringBuffer();
        ConceptMapping cm = new ConceptMapping();
        int endindex = 0;
        for (int i = 0; i < terms.size(); i++) {
            String conceptId = "", name = "";
            String termText = terms.get(i).getText();
            String termDomain = terms.get(i).getCategorey();
            if (Arrays.asList(GlobalSetting.conceptSetDomains).contains(termDomain)) {
                try {
                    String[] res = cm.getConceptByUsagi(termText, termDomain);//Map the term to a concept with highest matching score.
                    conceptId = res[0];
                    name = res[1];
                    terms.get(i).setConceptId(Integer.valueOf(conceptId));
                    terms.get(i).setConceptName(name);
                    if (!conceptSet.containsKey(termText + " " + conceptId)) {
                        Integer conceptSetId = conceptSet.size() + 1;
                        conceptSet.put(termText + " " + conceptId, conceptSetId);
                        terms.get(i).setVocabularyId(conceptSetId);
                    } else {
                        terms.get(i).setVocabularyId(conceptSet.get(termText + " " + conceptId));
                    }
                } catch (Exception e) {
                    System.out.println("--");
                    System.out.println(terms.get(i).getText());
                    System.out.println(e);
                }
            }
            //System.out.println("ConceptID:"+conceptId);
            String s2;
            if (conceptId != "") {//If the term can be mapped to a concept,
                s2 = "<mark data-entity=\"" + terms.get(i).getCategorey().toLowerCase() + "\" " +
                        "concept-id=\"" + conceptId + "\">" +
                        terms.get(i).getText() +
                        "<b><i>[" + name + "]</i></b>" + "</mark> ";
            } else {//If the term can not be mapped to a concept,
                s2 = "<mark data-entity=\"" + terms.get(i).getCategorey().toLowerCase() + "\">" + terms.get(i).getText() + "</mark> ";
            }
            //sent=sent.replace(.getText(), s2);
            if (i == 0) {
                sb.append(sent.substring(0, terms.get(i).getEnd_index()).replace(terms.get(i).getText(), s2));
                //Get a substring of the sentence which ends with the term and a space, replace the term with its new form, and add this substring to "sb".
                endindex = terms.get(i).getEnd_index();
            } else if (i > 0) {
                sb.append(sent.substring(terms.get(i - 1).getEnd_index() + 1, terms.get(i).getEnd_index()).replace(terms.get(i).getText(), s2));
                endindex = terms.get(i).getEnd_index();
            }
        }
        if (terms.size() == 0) {//If there doesn't exist any entity in the sentence,
            sb.append(text);
        } else if (endindex != text.length()) { //If there exists some words after the last term in the sentence,
            sb.append(sent.substring(endindex + 1));
        }
        //System.out.println("to be displayed =>"+sb.toString());
        return sb.toString();
    }


    public String nerByCrf4Dispaly(String str) {
        String results = ner.classifyWithInlineXML(str);
        String displaystr = trans2Html(results);
        return displaystr;
    }

    /**
     * parseSentence Author:chi Date:2017-3-22
     */
    public Tree parseSentence(String input) {
        LexicalizedParser lp = LexicalizedParser.loadModel(grammars);
        Tree tree = lp.parse(input);
        return tree;
    }

    public ArrayList<TaggedWord> tagWords(Tree t) {
        ArrayList<TaggedWord> twlist = t.taggedYield();
        return twlist;
    }

    //It creates a term list for each sentence with the latest parsing result after user's edition.
    public Object[] formulateTerms(String orignialstr, String result, Map<String, Integer> conceptSetMap) {
        List<String[]> listmap = new ArrayList<String[]>();
        List<Term> terms = new ArrayList<Term>();
        //Find the terms with matched concepts in the sentence.
        String pattern = "<mark data-entity=\"(value|demographic|condition|qualifier|measurement|observation|drug|procedure|temporal|negation_cue|device)\" concept-id=\"([0-9]*)\"> ([\\s\\S]*?) <b> <i> \\[ ([\\s\\S]*?) \\] </i> </b> </mark>";
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(result);
        while (mat.find()) {
            String[] arrs = new String[5];
            arrs[0] = String.valueOf(mat.start());//get the start position of term
            arrs[1] = mat.group(1);//get the term category.
            arrs[2] = mat.group(3).trim();//get the term text.
            arrs[3] = mat.group(2);//get the conceptId.
            arrs[4] = mat.group(4).trim();
            listmap.add(arrs);
        }
        //Find terms without matched concepts in the sentence.
        pattern = "<mark data-entity=\"(value|demographic|condition|qualifier|measurement|observation|drug|procedure|temporal|negation_cue|deivce)\"> ([\\s\\S]*?) </mark>";
        pat = Pattern.compile(pattern);
        mat = pat.matcher(result);
        while (mat.find()) {
            String[] arrs = new String[3];
            arrs[0] = String.valueOf(mat.start());//get the start position of term
            arrs[1] = mat.group(1);//get the term category.
            arrs[2] = mat.group(2).trim();//get the term text.
            listmap.add(arrs);
        }
        //Sort terms in the list with their start position
        Collections.sort(listmap, new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                return Integer.valueOf(o1[0]).compareTo(Integer.valueOf(o2[0]));
            }
        });

        int relativepos = 0;
        int termindex = 0;
        List<Integer> negateCues = new ArrayList<>();
        boolean containNeg = false;
        int length0 = 0;
        for (int i = 0; i < listmap.size(); i++) {
            int start = orignialstr.indexOf(listmap.get(i)[2]);
            //the start position of the term in the substring of sentence where (i-1)th term and the part before it has been removed.
            int entitylength = listmap.get(i)[2].length();//length of the term
            Term term = new Term();
            term.setTermId(termindex);
            char[] chars = listmap.get(i)[1].toCharArray();
            if (chars[0] >= 'a' && chars[0] <= 'z') {
                chars[0] = (char) (chars[0] - 32);
            }
            term.setCategorey(new String(chars));
            term.setText(listmap.get(i)[2]);
            term.setStart_index(relativepos + start);//relative start position of the term in the whole sentence
            term.setEnd_index(relativepos + start + entitylength);//relative end position of (the term+a space) in the whole sentenceterm.setVocabularyId();
            if (listmap.get(i).length > 3) {
                if (!conceptSetMap.containsKey(listmap.get(i)[2] + " " + listmap.get(i)[3])) {
                    Integer conceptSetId = conceptSetMap.size() + 1;
                    conceptSetMap.put(listmap.get(i)[2] + " " + listmap.get(i)[3], conceptSetId);
                    term.setVocabularyId(conceptSetId);
                } else {
                    term.setVocabularyId(conceptSetMap.get(listmap.get(i)[2] + " " + listmap.get(i)[3]));
                }
                //term.setVocabularyId(conceptSetMap.get(listmap.get(i)[2]+" "+listmap.get(i)[3]));
                term.setConceptId(Integer.valueOf(listmap.get(i)[3]));
                term.setConceptName(listmap.get(i)[4]);
            }
            List<Integer> termPosition = new ArrayList<>();
            String subOriginalStr = orignialstr.substring(0,start).trim();
            if(!subOriginalStr.isEmpty()){
                length0 = subOriginalStr.split(" ").length;
            }else{
                length0 = 0;
            }
            //length0 = orignialstr.substring(0, start).trim().split(" ").length;
            negateCues.addAll(Collections.nCopies(length0, 3));
            int termLength = term.getText().trim().split(" ").length;
            if (term.getCategorey().equals("Negation_cue")) {
                containNeg = true;
                if (termLength > 1) {
                    termPosition.add(negateCues.size());
                    negateCues.addAll(Collections.nCopies(termLength, 2)); //used to be 2
                    termPosition.add(negateCues.size());
                    term.setIndex(termPosition);
                } else {
                    termPosition.add(negateCues.size());
                    negateCues.addAll(Collections.nCopies(termLength, 1));
                    termPosition.add(negateCues.size());
                    term.setIndex(termPosition);
                }
            } else {
                termPosition.add(negateCues.size());
                negateCues.addAll(Collections.nCopies(termLength, 3));
                termPosition.add(negateCues.size());
                term.setIndex(termPosition);
            }
            terms.add(term);
            termindex++;
            relativepos = relativepos + start + listmap.get(i)[2].length();
            orignialstr = orignialstr.substring(start + entitylength);//Remove i th term and the part of the sentence before it
        }
        String subOriginalStr = orignialstr.trim();
        if(!subOriginalStr.isEmpty()){
            length0 = subOriginalStr.split(" ").length;
        }else{
            length0 = 0;
        }
        negateCues.addAll(Collections.nCopies(length0, 3));
        Object[] items = new Object[]{terms, negateCues, containNeg};
        return items;
    }


}
