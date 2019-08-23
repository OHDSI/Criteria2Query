package edu.columbia.dbmi.ohdsims.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.util.FileUtil;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

public class LogicAnalysisTool {
	static final CoreNLP snlp = new CoreNLP();
	AnnotationPipeline pipeline;

	public static void main(String[] args) {
		String teststr = "Unstable angina, myocardial infarction, or congestive heart failure Class II-IV (Attachment 3) within the preceding 12 months, cerebrovascular accident (CVA), transient ischemic attack (TIA) within the preceding 3 months, pulmonary embolism (PE) within the preceding 2 months.";
		String test2 = "Concurrent severe or uncontrolled medical disease (e.g., active systemic infection,diabetes, hypertension, coronary artery disease, congestive heart failure, active viral hepatitis or chronic liver disease)";
		String test3 = "have type 2 diabetes and HTN";
		String test4 = "Has an alanine aminotransferase, aspartate aminotransferase or total bilirubin level greater than 1.5 times the upper limits of normal.";
		Sentence p = new Sentence(test2);
		LogicAnalysisTool lat = new LogicAnalysisTool();
		lat.decompose(p);
		String test="Patients have type 2 diabetes within 12 months .";
		Term t1=new Term();
		t1.setStart_index(14);
		t1.setEnd_index(29);
		Term t2=new Term();
		t2.setStart_index(30);
		t2.setEnd_index(46);
		System.out.println(lat.isConnected(t1,t2,test));
	}

	public LogicAnalysisTool() {
		Properties properties = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma,depparse");
		// properties.setProperty("depparse.language", "English");
		pipeline = new StanfordCoreNLP(properties);

	}
	
	public boolean isConnected(Term t1, Term t2, String text){
		Set<IndexedWord> itemset = new HashSet<IndexedWord>();
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);
		List<SemanticGraphEdge> sges=new ArrayList<SemanticGraphEdge>();
		Set<Integer> term1_set=new HashSet<Integer>();
		Set<Integer> term2_set=new HashSet<Integer>();
		List<Integer[]> deprels=new ArrayList<Integer[]>();
		for (CoreMap sentence : annotation.get(SentencesAnnotation.class)) {
			sges = sentence.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class).edgeListSorted();
			for (SemanticGraphEdge sge : sges) {
				if(isInRange(sge.getGovernor().beginPosition(),sge.getGovernor().endPosition(),t1.getStart_index(),t1.getEnd_index())){
					term1_set.add(sge.getGovernor().index());
				}
				if(isInRange(sge.getDependent().beginPosition(),sge.getDependent().endPosition(),t1.getStart_index(),t1.getEnd_index())){
					term1_set.add(sge.getDependent().index());
				}
				if(isInRange(sge.getGovernor().beginPosition(),sge.getGovernor().endPosition(),t2.getStart_index(),t2.getEnd_index())){
					term2_set.add(sge.getGovernor().index());
				}
				if(isInRange(sge.getDependent().beginPosition(),sge.getDependent().endPosition(),t2.getStart_index(),t2.getEnd_index())){
					term2_set.add(sge.getDependent().index());
				}
				Integer[] deprel=new Integer[2];
				deprel[0]=sge.getGovernor().index();
				deprel[1]=sge.getDependent().index();
				deprels.add(deprel);
				System.out.println(sge.getGovernor().index()+" "+sge.getGovernor().word()+"\t"+sge.getRelation().getShortName()+"\t"+sge.getDependent().index()+" "+sge.getDependent().word());
				
			}
		}
		boolean connect=false;
		System.out.println("term 1=");
		for(Integer i:term1_set){
			System.out.println(i);
		}
		System.out.println("term 2=");
		for(Integer i:term2_set){
			System.out.println(i);
		}
		for (Integer[] rel : deprels) {
			if (term1_set.contains(rel[0])&&term2_set.contains(rel[1])) {
				System.out.println(rel[0]+"\t"+rel[1]);
				connect=true;
			}else if(term1_set.contains(rel[1])&&term2_set.contains(rel[0])){
				System.out.println(rel[0]+"\t"+rel[1]);
				connect=true;
			}
		}
		return connect;
	}
	/**
	 * determine if one word is in a index range or not
	 *
	 * */
	public boolean isInRange(int start,int end, int targetstart,int targetend){
		int midloc=(start+end)/2;
		if(midloc>=targetstart&&midloc<targetend){
			return true;
		}else{
			return false;
		}
		
	}

	public List<LinkedHashSet<Integer>> decompose(Sentence p) {
		Collection<TypedDependency> tdset = snlp.getDependency(p.getText());
		int entity1_median = 0;
		int entity2_median = 0;
		List<LinkedHashSet<Integer>> conj_or = new ArrayList<LinkedHashSet<Integer>>();
		for (TypedDependency item : tdset) {

			if (item.reln().toString().equals("conj:or")) {
				entity1_median = (item.dep().beginPosition() + item.dep().endPosition()) / 2;
				entity2_median = (item.gov().beginPosition() + item.gov().endPosition()) / 2;
				LinkedHashSet<Integer> conj_or_group_1 = searchGroup(conj_or, entity1_median);
				LinkedHashSet<Integer> conj_or_group_2 = searchGroup(conj_or, entity2_median);
				if (conj_or_group_1 == null && conj_or_group_2 == null) {
					LinkedHashSet<Integer> conj_or_group = new LinkedHashSet<Integer>();
					conj_or_group.add(entity1_median);
					conj_or_group.add(entity2_median);
					conj_or.add(conj_or_group);
				} else if (conj_or_group_1 != null && conj_or_group_2 == null) {
					conj_or.remove(conj_or_group_1);
					conj_or_group_1.add(entity2_median);
					conj_or.add(conj_or_group_1);
				} else if (conj_or_group_1 == null && conj_or_group_2 != null) {
					conj_or.remove(conj_or_group_2);
					conj_or_group_2.add(entity1_median);
					conj_or.add(conj_or_group_2);
				}
			}
		}
		// printoutGroups(conj_or);
		List<LinkedHashSet<Integer>> entity_group = new ArrayList<LinkedHashSet<Integer>>();

		for (int i = 0; i < conj_or.size(); i++) {
			LinkedHashSet<Integer> entities = new LinkedHashSet<Integer>();
			for (Integer b : conj_or.get(i)) {
				if (p.getTerms() != null) {
					for (Term t : p.getTerms()) {
						if (b >= t.getStart_index() && b <= t.getEnd_index()) {
							entities.add(t.getTermId());
						}
					}
				}
			}
			entity_group.add(entities);
		}

		return entity_group;
	}

	public List<LinkedHashSet<Integer>> decompose(String text, List<Term> terms) {
		Collection<TypedDependency> tdset = snlp.getDependency(text);
		int entity1_median = 0;
		int entity2_median = 0;
		List<LinkedHashSet<Integer>> conj_or = new ArrayList<LinkedHashSet<Integer>>();
		for (TypedDependency item : tdset) {

			if (item.reln().toString().equals("conj:or")) {
				entity1_median = (item.dep().beginPosition() + item.dep().endPosition()) / 2;
				entity2_median = (item.gov().beginPosition() + item.gov().endPosition()) / 2;
				LinkedHashSet<Integer> conj_or_group_1 = searchGroup(conj_or, entity1_median);
				LinkedHashSet<Integer> conj_or_group_2 = searchGroup(conj_or, entity2_median);
				if (conj_or_group_1 == null && conj_or_group_2 == null) {
					LinkedHashSet<Integer> conj_or_group = new LinkedHashSet<Integer>();
					conj_or_group.add(entity1_median);
					conj_or_group.add(entity2_median);
					conj_or.add(conj_or_group);
				} else if (conj_or_group_1 != null && conj_or_group_2 == null) {
					conj_or.remove(conj_or_group_1);
					conj_or_group_1.add(entity2_median);
					conj_or.add(conj_or_group_1);
				} else if (conj_or_group_1 == null && conj_or_group_2 != null) {
					conj_or.remove(conj_or_group_2);
					conj_or_group_2.add(entity1_median);
					conj_or.add(conj_or_group_2);
				}
			}
		}
		// printoutGroups(conj_or);
		List<LinkedHashSet<Integer>> entity_group = new ArrayList<LinkedHashSet<Integer>>();

		for (int i = 0; i < conj_or.size(); i++) {
			LinkedHashSet<Integer> entities = new LinkedHashSet<Integer>();
			for (Integer b : conj_or.get(i)) {
				for (Term t : terms) {
					if (b >= t.getStart_index() && b <= t.getEnd_index()) {
						entities.add(t.getTermId());
					}
				}
			}
			entity_group.add(entities);
		}

		return entity_group;
	}

	public LinkedHashSet<Integer> searchGroup(List<LinkedHashSet<Integer>> conj_or, int x) {
		LinkedHashSet<Integer> result = null;
		for (int i = 0; i < conj_or.size(); i++) {
			if (conj_or.get(i).contains(x)) {
				result = conj_or.get(i);
			}
		}
		return result;
	}

	public void printoutGroups(List<LinkedHashSet<Integer>> conj_or) {
		for (int i = 0; i < conj_or.size(); i++) {
			System.out.println("----------------------");
			for (Integer b : conj_or.get(i)) {
				System.out.println("->" + b);
			}
		}
	}

	public List<LinkedHashSet<Integer>> ddep(String text, List<Term> terms) {
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);
		List<LinkedHashSet<Integer>> conj_or = new ArrayList<LinkedHashSet<Integer>>();
		for (CoreMap sentence : annotation.get(SentencesAnnotation.class)) {
			List<SemanticGraphEdge> sges = sentence
					.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class).edgeListSorted();
			int entity1_median = 0;
			int entity2_median = 0;
			for (SemanticGraphEdge sge : sges) {
				//System.out.println(
				//		sge.getRelation().getSpecific() + "\t" + sge.getDependent() + "\t" + sge.getGovernor());
				if (sge.getRelation().getSpecific() != null && sge.getRelation().getSpecific().equals("or")) {
					entity1_median = (sge.getDependent().beginPosition() + sge.getDependent().endPosition()) / 2;
					entity2_median = (sge.getGovernor().beginPosition() + sge.getGovernor().endPosition()) / 2;
					LinkedHashSet<Integer> conj_or_group_1 = searchGroup(conj_or, entity1_median);
					LinkedHashSet<Integer> conj_or_group_2 = searchGroup(conj_or, entity2_median);
					if (conj_or_group_1 == null && conj_or_group_2 == null) {
						LinkedHashSet<Integer> conj_or_group = new LinkedHashSet<Integer>();
						conj_or_group.add(entity1_median);
						conj_or_group.add(entity2_median);
						conj_or.add(conj_or_group);
					} else if (conj_or_group_1 != null && conj_or_group_2 == null) {
						conj_or.remove(conj_or_group_1);
						conj_or_group_1.add(entity2_median);
						conj_or.add(conj_or_group_1);
					} else if (conj_or_group_1 == null && conj_or_group_2 != null) {
						conj_or.remove(conj_or_group_2);
						conj_or_group_2.add(entity1_median);
						conj_or.add(conj_or_group_2);
					}
				}
			}
			
		}
		List<LinkedHashSet<Integer>> entity_group = new ArrayList<LinkedHashSet<Integer>>();
		for (int i = 0; i < conj_or.size(); i++) {
			LinkedHashSet<Integer> entities = new LinkedHashSet<Integer>();
			for (Integer b : conj_or.get(i)) {
				for (Term t : terms) {
					if (b >= t.getStart_index() && b <= t.getEnd_index()) {
						entities.add(t.getTermId());
					}
				}
			}
			entity_group.add(entities);
		}

		return entity_group;
	}
}
