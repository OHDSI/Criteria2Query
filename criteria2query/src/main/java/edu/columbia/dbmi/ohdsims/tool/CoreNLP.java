package edu.columbia.dbmi.ohdsims.tool;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class CoreNLP {
	public static final String grammars = GlobalSetting.dependence_model;
	public static LexicalizedParser lp;

	public static void main(String[] args) {
		String sentence="≥18 and ≤60 years old";
		CoreNLP snlp=new CoreNLP();
		Tree tree = snlp.parseSentence(sentence);
		snlp.extractTree(tree);
		snlp.tagWords(tree);
		List<String> llist=snlp.getLemmasList(sentence);
		for(String lem:llist){
			System.out.println("->"+lem);
		}
		List<String> sss=snlp.splitParagraph("Patients have a history of type 2 diabetes within 10 years. Patients have no hypertension.");
		for(String s:sss){
			System.out.println(s);
		}
		//output tree structure
		System.out.println("----word pos-------");
		snlp.outputTreeStruture(tree);
		System.out.println("---dependency--------");
		Collection<TypedDependency> tdl=snlp.outputDependency(tree);
		System.out.println(tdl);
		System.out.println("---dependency-end--------");
		//System.out.println(snlp.extractTree(tree));
//		DirectedGraph<String, DefaultEdge> g2 = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
//		Set<IndexedWord> itemset=new HashSet<IndexedWord>();
//		for (TypedDependency item : tdl) {
//			itemset.add(item.dep());
//			itemset.add(item.gov());
//			//g2.addEdge(String.valueOf(item.gov().index()), String.valueOf(item.dep().index()));
//			System.out.println("dep->"+item.dep().before()+"\t"+item.dep().after()+"\t"+item.dep().index());
//		}
//		for(IndexedWord iw:itemset){
//			String str=String.valueOf(iw.index());
//			 g2.addVertex(str);
//		}
//		for (TypedDependency item : tdl) {
//			g2.addEdge(String.valueOf(item.gov().index()), String.valueOf(item.dep().index()));
//			System.out.println("==|+++->"+item.dep()+"\t"+item.reln()+"\t"+item.gov());
//		}
//		DijkstraShortestPath dijk = new DijkstraShortestPath(g2);
//	    //GraphPath<Integer, DefaultWeightedEdge> shortestPath1 = dijk.getPath("2","13");
//	    System.out.println("shortestPath1="+shortestPath1.getLength());
	}
	public CoreNLP() {
		this.lp = LexicalizedParser.loadModel(grammars);
	}
	
	/*
	 * Split sentence by CoreNLP
	 * 
	 * */
	public List<String> splitParagraph(String paragraph){
		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();
		for (List<HasWord> sentence : dp) {
			String sentenceString = SentenceUtils.listToString(sentence);
			sentenceList.add(sentenceString);
		}
		return sentenceList;
	}
	
	
	public String getChunksbySentenceStanford(String sentence){
		Tree tree = parseSentence(sentence);
		String result=outputTreeStruture(tree);
		return result;
	}
	
	public List<String[]> getAnswerChunksbySentenceStanford(String sentence){
		Tree tree = parseSentence(sentence);
		String result=outputTreeStruture(tree);
		String[] resset=result.split("\n");
		List<String[]> rlist=new ArrayList<String[]>();
		for(String s:resset){
			String[] eachrow=s.split("\t");
			rlist.add(eachrow);
		}
		return rlist;
	}
	
	
	//with improve better 
	public List<String[]> getAnswerChunksbySentenceStanford2(String sentence){
		Tree tree = parseSentence(sentence);
		String result=extractTree(tree);
		String[] resset=result.split("\n");
		List<String[]> rlist=new ArrayList<String[]>();
		for(String s:resset){
			String[] eachrow=s.split("\t");
			rlist.add(eachrow);
		}
		return rlist;
	}
	

	public ArrayList<TaggedWord> tagWords(Tree t) {
		ArrayList<TaggedWord> twlist = t.taggedYield();
		for (int x = 0; x < twlist.size(); x++) {
			TaggedWord tw = twlist.get(x);
			System.out.println("[" + (x) + "]:" + tw.tag() + "--" + tw.word() + " (" + tw.value() + ")" + "--" + tw.beginPosition() + "--" + tw.endPosition()+"-");
			//System.out.print(tw.word() + "/" + tw.tag() + " ");	
		}
		return twlist;
	}

	/**
	 * parseSentence 
	 * Author:chi Date:2017-3-22
	 * 
	 */
	public Tree parseSentence(String input) {
		Tree tree = lp.parse(input);
		return tree;
	}

	public List<Tree> GetNounPhrases(Tree parse) {

		List<Tree> phraseList = new ArrayList<Tree>();
		for (Tree subtree : parse) {
			if (subtree.label().value().equals("NP")) {
				phraseList.add(subtree);
				System.out.println(subtree);
			}
		}
		return phraseList;

	}

	//without improve
	public String outputTreeStruture(Tree tree){
		StringBuffer sb=new StringBuffer();
		List<Tree> tree_list = tree.getLeaves();
		for (Tree treeunit : tree_list) {
			if(treeunit.numChildren()==0){
				System.out.println(treeunit+"\t"+treeunit.parent(tree).label().toString()+"\t"+treeunit.parent(tree).parent(tree).label().toString());
				//sb.append(treeunit+"\t"+treeunit.parent(tree).label().toString()+"\t"+treeunit.parent(tree).parent(tree).label().toString()+"\n");
			}
		}
		return sb.toString();
	}
	
	/**
	 * with improve for ADJP and QP
	 * 
	 * */
	public String extractTree(Tree tree){
		StringBuffer sb=new StringBuffer();
		List<Tree> tree_list = tree.getLeaves();
		for (Tree treeunit : tree_list) {
			if(treeunit.numChildren()==0){
				//System.out.println(treeunit+"\t"+treeunit.parent(tree).label().toString()+"\t"+treeunit.parent(tree).parent(tree).label().toString());
				//System.out.println(treeunit.parent(tree).parent(tree).label().toString());
				if(treeunit.parent(tree).parent(tree).parent(tree).label().toString().equals("NP")&&(treeunit.parent(tree).parent(tree).label().toString().equals("ADJP")||treeunit.parent(tree).parent(tree).label().toString().equals("PRN"))){
					sb.append(treeunit+"\t"+treeunit.parent(tree).label().toString()+"\t"+"NP"+"\n");
				}else{
					sb.append(treeunit+"\t"+treeunit.parent(tree).label().toString()+"\t"+treeunit.parent(tree).parent(tree).label().toString()+"\n");
				}
			}
		}
		return sb.toString();
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
		return tdl;

	}
	
	public Collection<TypedDependency> getDependency(String sentence){
		Tree tree = parseSentence(sentence);
		Collection<TypedDependency> tdl=outputDependency(tree);
		return tdl;
	}

	//Lemmatize the input text and return a list of string
	public List<String> getLemmasList(String text) {
		  Sentence sentence = new Sentence(text);
		  return sentence.lemmas();
		}

	
}
