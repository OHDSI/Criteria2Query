package edu.columbia.dbmi.ohdsims.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.columbia.dbmi.ohdsims.pojo.Cdmentity;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import edu.columbia.dbmi.ohdsims.util.StringUtil;
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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class NERTool {
	AbstractSequenceClassifier<CoreLabel> ner=CRFClassifier.getClassifierNoExceptions(GlobalSetting.crf_model);
	public static final String grammars = GlobalSetting.dependence_model;
	private final static String diclookup = GlobalSetting.concepthub+"/omop/searchOneEntityByTerm";

	public static void train(String traindatapath,String targetpath){
		long startTime = System.nanoTime();

        /* Step 1: learn the classifier from the training data */
        String trainFile = traindatapath; 
        /* Learn the classifier from the training data */
        String serializeFileLoc =targetpath;
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
	 * 
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

	public List<Term> formulateNerResult(String orignialstr, String result) {
		List<String[]> listmap = new ArrayList<String[]>();
		List<Term> terms=new ArrayList<Term>();	
		String pattern = "<(Value|Demographic|Condition|Qualifier|Measurement|Observation|Drug|Procedure|Temporal|Negation_cue)>([\\s\\S]*?)</(Value|Demographic|Condition|Qualifier|Measurement|Observation|Drug|Procedure|Temporal|Negation_cue)>";
		String s = result;
		Pattern pat = Pattern.compile(pattern);
		Matcher mat = pat.matcher(s);
		int count = 0;
		while (mat.find()) {
			count = count + 1;
			String[] arrs = new String[2];
			arrs[0] = mat.group(1);
			arrs[1] = mat.group(2);
			listmap.add(arrs);
		}
		int relativepos=0;
		int termindex=0;
		for(int i=0;i<listmap.size();i++){
			int start=orignialstr.indexOf(listmap.get(i)[1]);			
			int entitylength=listmap.get(i)[1].length();				
			Term term=new Term();
			term.setTermId(termindex);
			term.setCategorey(listmap.get(i)[0]);
			term.setText(listmap.get(i)[1]);
			term.setStart_index(relativepos+start);
			term.setEnd_index(relativepos+start+entitylength);
			terms.add(term);
			termindex++;
			relativepos=relativepos+start+listmap.get(i)[1].length();
			orignialstr=orignialstr.substring(start+listmap.get(i)[1].length());		
		}
		return terms;
	}
	
	
	public String nerByCrf(String str) {
		String results= ner.classifyWithInlineXML(str);
		results=results.replace("<0>", "");
		results=results.replace("</0>", "");
		return results;
	}
	
	public String nerByDic(String str){
		String res=str;
		JSONObject jo=new JSONObject();
		jo.accumulate("term", str);
		
		String result=HttpUtil.doPost(diclookup, jo.toString());
		System.out.println("result="+result);
		JSONObject bestconcept=JSONObject.fromObject(result);
		try{
			System.out.println("matchScore="+bestconcept.getDouble("matchScore"));
			if(bestconcept.getDouble("matchScore")>0.75)
			{
				JSONObject concept_jo=bestconcept.getJSONObject("concept");
				String domain=concept_jo.getString("domainId");
				res="<"+domain+">"+str+"</"+domain+">";
			}
				
		}catch(Exception ex){
			
		}
		System.out.println("dic_result="+res);
		return res;
	}
	
	
	
	public String trans2Html(String result){
		result=result.replace("<Condition>", "<mark data-entity=\"condition\">");
		result=result.replace("</Condition>", "</mark>");
		result=result.replace("<Drug>", "<mark data-entity=\"drug\">");
		result=result.replace("</Drug>", "</mark>");
		result=result.replace("<Procedure>", "<mark data-entity=\"procedure\">");
		result=result.replace("</Procedure>", "</mark>");
		result=result.replace("<Observation>", "<mark data-entity=\"observation\">");
		result=result.replace("</Observation>", "</mark>");
		result=result.replace("<Measurement>", "<mark data-entity=\"measurement\">");
		result=result.replace("</Measurement>", "</mark>");
		result=result.replace("<Temporal>", "<mark data-entity=\"temporal\">");
		result=result.replace("</Temporal>", "</mark>");
		result=result.replace("<Negation_cue>", "<mark data-entity=\"negation_cue\">");
		result=result.replace("</Negation_cue>", "</mark>");
		result=result.replace("<Demographic>", "<mark data-entity=\"demographic\">");
		result=result.replace("</Demographic>", "</mark>");
		result=result.replace("<Value>", "<mark data-entity=\"value\">");
		result=result.replace("</Value>", "</mark>");	
		return result;
	}
	
	public String trans4display(String text,List<Term> terms){
		String sent=text;
		StringBuffer sb=new StringBuffer();
		int endindex=0;
		for(int i=0;i<terms.size();i++){
			String s2="<mark data-entity=\""+terms.get(i).getCategorey().toLowerCase()+"\">"+terms.get(i).getText()+"</mark>";
			//sent=sent.replace(.getText(), s2);
			if(i==0){
				sb.append(sent.substring(0,terms.get(i).getEnd_index()).replace(terms.get(i).getText(), s2));
				endindex =terms.get(i).getEnd_index();
			}else if(i>0){
				sb.append(sent.substring(terms.get(i-1).getEnd_index()+1,terms.get(i).getEnd_index()).replace(terms.get(i).getText(), s2));
				endindex =terms.get(i).getEnd_index();
			}
		}
		if(terms.size()==0){
			sb.append(text);
		}else if(endindex!=text.length()){
			sb.append(sent.substring(endindex+1));
		}
		return sb.toString();
	}
	
	
	public String nerByCrf4Dispaly(String str) {
		String results= ner.classifyWithInlineXML(str);
		String displaystr=trans2Html(results);
		return displaystr;
	}
	
	/**
	 * parseSentence Author:chi Date:2017-3-22
	 * 
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
}
