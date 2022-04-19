package edu.columbia.dbmi.ohdsims.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ReconTool {
	Properties properties = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma,depparse");
	AnnotationPipeline pipeline = new StanfordCoreNLP(properties);
	private final static String reconurl = "http://35.225.138.31:8080/concepthub/recon/complexentity";

	public boolean isCEE(String text){
		text = text.replace("/", " / ");
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		boolean flag=false;
		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);//token.get(LemmaAnnotation.class);//TextAnnotation.class
				//Get the text of the token.
				String pos = token.get(PartOfSpeechAnnotation.class);
				//Get the pos tag of the token.
				//String lemma = token.get(LemmaAnnotation.class);
				boolean f = false;
				if ((word.equals("and") || word.equals(",") || word.equals("/") || word.equals("or"))) {
					flag = true;
					break;
				}
				
			}
		}
		
		return flag;
	}
	
	public List<String> resolve(String text){
		JSONObject jo=new JSONObject();
		jo.accumulate("entity", text);
		String result=HttpUtil.doPost(reconurl, jo.toString());
		JSONArray array = JSONArray.fromObject(result);
		List<String> list=new ArrayList<String>();
		for(Object obj :array){
			list.add((String) obj);
		}
		return list;
	}
}
