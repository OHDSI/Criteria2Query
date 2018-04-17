package edu.columbia.dbmi.ohdsims.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.time.SUTime.Temporal;
import edu.stanford.nlp.util.CoreMap;

public class TemporalNormalize {

	public static void main(String[] args) {
		String text = "at least 2 weeks";															// surgery
		TemporalNormalize sd = new TemporalNormalize();
		//sd.temporalNormalize(text);
		System.out.println(sd.temporalNormalizeforNumberUnit(text));
	}

	AnnotationPipeline pipeline;

	public TemporalNormalize() {
		pipeline = new AnnotationPipeline();
		//pipeline.addAnnotator(new PTBTokenizerAnnotator(false));
		pipeline.addAnnotator(new TokenizerAnnotator(false));
		pipeline.addAnnotator(new WordsToSentencesAnnotator(false));	
		String sutimeRules="edu/columbia/dbmi/ohdsims/model/defs.sutime.txt,"+"edu/columbia/dbmi/ohdsims/model/english.holidays.sutime.txt,"+"edu/columbia/dbmi/ohdsims/model/english.sutime.txt";
		Properties props = new Properties();
		props.setProperty("sutime.rules", sutimeRules);
		props.setProperty("sutime.binders", "0");
		pipeline.addAnnotator(new TimeAnnotator("sutime", props));

	}

	public Integer temporalNormalizeforNumberUnit(String text) {
		Annotation annotation = new Annotation(text);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, SUTime.getCurrentTime().toString());
		pipeline.annotate(annotation);

		System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));

		List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
		Integer days=0;
		for (CoreMap cm : timexAnnsAll) {

			List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
			 System.out.println(cm + " [from char offset " + tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) + " to "
			 + tokens.get(tokens.size() -1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']'
			 + " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());
			 System.out.println("!!!!-->" +cm.get(TimeExpression.Annotation.class).getValue());
			 System.out.println("---final result---");
			String tstr = cm.get(TimeExpression.Annotation.class).getTemporal().toString();
			System.out.println("tstr"+tstr);
			int k=tstr.indexOf("P");
			if(tstr.indexOf(")")!=-1){
				tstr=tstr.substring(k, tstr.length()-1);
			}
			double total = TemporalConvert.convertTodayUnit(tstr);
			double number = TemporalConvert.recognizeNumbersFormSUTime(tstr);
			System.out.println("t="+total);
			System.out.println("n="+number);
			// System.out.println("unit=" + total);
			if(total==30 && number==12){
				days=365;
				
			}else{
				days=(int) (total * number);
			}
		}
		return days;
		
	}
	


}
