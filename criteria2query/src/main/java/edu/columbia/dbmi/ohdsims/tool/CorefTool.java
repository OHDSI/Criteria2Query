package edu.columbia.dbmi.ohdsims.tool;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;


public class CorefTool {

	public static void main(String[] args) {
		//extractCoref();
		CorefTool cft=new CorefTool();
		//cft.ddep();
	}

	public void extractCoref() {
		String s="Subjects with hypothyroidism who are on stable treatment for 3 months prior to screening are required to have TSH and free thyroxine (FT4) obtained. If the TSH value is out of range, but FT4 is normal, such cases should be discussed directly with the JRD responsible safety physician before the subject is enrolled. If the FT4 value is out of range, the subject is not eligible.";
		 Annotation document = new Annotation(s);
		    Properties props = new Properties();
		    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
		    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		    pipeline.annotate(document);
		    System.out.println("---");
		    System.out.println("coref chains");
		    for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
		      System.out.println("\t" + cc);
		    }
		    for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
		      System.out.println("---");
		      System.out.println("mentions");
		      for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
		        System.out.println("\t" + m);
		       }
		    }
	}
	
	

}
