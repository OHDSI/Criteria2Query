package edu.columbia.dbmi.ohdsims.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.util.FileUtil;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;

public class RelExTool {
	static final CoreNLP snlp = new CoreNLP();
	Classifier classifier;
	
	public RelExTool(String modelpath) {
		this.classifier = readModel(modelpath);//GlobalSetting.relexmodel
	}
	public RelExTool() {
		this.classifier = readModel(GlobalSetting.relexmodel);//GlobalSetting.relexmodel
	}
	public static void main(String[] args) {
		//generateTrainingFile();
		RelExTool rt=new RelExTool();
		//rt.getRelsByTermPairs(null,"Patients receiving DFX at a dose > 40 mg/kg/day or DFP at a dose > 100 mg/kg/day at screening",snlp);
	}
	public static void generateTrainingFile() {
		// predictRel(text);
		String dir = "/Users/cy2465/Documents/nlptools/standoff2corenlp/data";
		String targetcvs="/Users/cy2465/Documents/c2q_rel_training_100.csv";
		List<Sentence> sents = convertStandoff(dir);
		List<Sentence> newsents =new ArrayList<Sentence>();
		for(Sentence s:sents){
			String string=s.getText();
			System.out.println(string);
			if(string.startsWith(">>")){
				continue;
			}else if(string.startsWith("\n- INCLU")){
				continue;
			}else if(string.startsWith("\n EXCLUS")){
				continue;
			}else if(string.equals("\n")){
				continue;
			}else if(string.startsWith("\n>>")){
				continue;
			}else if(string.startsWith("\nInclus")){
				continue;
			}else if(string.startsWith("\n Exclusion")){
				continue;
			}else if(string.startsWith("\n - ")){
				int sufix=4;
				String aftertext=string.substring(sufix);
				System.out.println(aftertext);
				List<Term> newtermlist=new ArrayList<Term>();
				for(Term t:s.getTerms()){
					System.out.println("Term:"+t.getTermId()+"\t"+t.getText()+"\t["+t.getCategorey()+"]"+"["+(t.getStart_index()-sufix+1)+","+(t.getEnd_index()-sufix+1)+"]"+aftertext.substring(t.getStart_index()-sufix+1,t.getEnd_index()-sufix+1)+"<<KKKK");
					t.setStart_index(t.getStart_index()-sufix+1);
					t.setEnd_index(t.getEnd_index()-sufix+1);
					newtermlist.add(t);
				}
				Sentence news=new Sentence(aftertext);
				news.setTerms(newtermlist);
				news.setRelations(s.getRelations());
				newsents.add(news);
				
			}else if(string.startsWith("\n ")){
				int sufix=2;
				String aftertext=string.substring(sufix);
				System.out.println(aftertext);
				List<Term> newtermlist=new ArrayList<Term>();
				for(Term t:s.getTerms()){
					System.out.println("Term:"+t.getTermId()+"\t"+t.getText()+"\t["+t.getCategorey()+"]"+"["+(t.getStart_index()-sufix+1)+","+(t.getEnd_index()-sufix+1)+"]");
					t.setStart_index(t.getStart_index()-sufix+1);
					t.setEnd_index(t.getEnd_index()-sufix+1);
					newtermlist.add(t);
				}
				Sentence news=new Sentence(aftertext);
				news.setTerms(newtermlist);
				news.setRelations(s.getRelations());
				newsents.add(news);
			}else if(string.startsWith("\n")){
				int sufix=1;
				String aftertext=string.substring(sufix);
				List<Term> newtermlist=new ArrayList<Term>();
				for(Term t:s.getTerms()){
					System.out.println("Term:"+t.getTermId()+"\t"+t.getText()+"\t["+t.getCategorey()+"]"+"["+(t.getStart_index()-sufix+1)+","+(t.getEnd_index()-sufix+1)+"]");
					t.setStart_index(t.getStart_index()-sufix+1);
					t.setEnd_index(t.getEnd_index()-sufix+1);
					newtermlist.add(t);
				}
				Sentence news=new Sentence(aftertext);
				news.setTerms(newtermlist);
				news.setRelations(s.getRelations());
				newsents.add(news);
			}
			else{
				System.out.println("exp:"+string);
				return;
			}
			
		}
		System.out.println("---END--");
		//generateTrainingFile(targetcvs, sents);
		//GlobalSetting.relexmodel
		int singlesent=0;
		int twosents=0;
		int threesents=0;
		List<Sentence> miniunits=new ArrayList<Sentence>();
		for(Sentence sss:newsents){
			System.out.println(sss.getText());
			List<Term> tlist=sss.getTerms();
			List<String> str=snlp.splitParagraph(sss.getText());
			if(str.size()==1){
				Sentence mu=new Sentence(sss.getText());
				mu.setTerms(tlist);
				mu.setRelations(sss.getRelations());
				mu.setStart_index(0);
				mu.setEnd_index(sss.getText().length());
				singlesent++;
				miniunits.add(mu);
			}else if(str.size()==2){
				String sufix=str.get(1).substring(0,5);
				System.out.println("sufix="+sufix);
				try{
				int splitindex=sss.getText().indexOf(sufix);
				System.out.println("splitindex="+splitindex);
				String text1=sss.getText().substring(0,splitindex-1);
				Sentence mu1=new Sentence(text1);
				String text2=sss.getText().substring(splitindex);
				mu1.setStart_index(0);
				mu1.setEnd_index(text2.length());
				Sentence mu2=new Sentence(text2);
				mu2.setStart_index(0);
				mu2.setEnd_index(text2.length());
				List<Term> tmu1=new ArrayList<Term>();
				List<Term> tmu2=new ArrayList<Term>();
				for(Term t:tlist){
					if(t.getStart_index()>=splitindex){
						System.out.println("next sent start index="+splitindex);
						t.setStart_index(t.getStart_index()-splitindex);
						t.setEnd_index(t.getEnd_index()-splitindex);
						tmu2.add(t);
					}else{
						tmu1.add(t);
					}
				}
				mu1.setTerms(tmu1);
				mu2.setTerms(tmu2);
				System.out.println("size="+sss.getRelations().size());
				
				mu1.setRelations(sss.getRelations());
				mu2.setRelations(sss.getRelations());
				miniunits.add(mu1);
				miniunits.add(mu2);
				twosents++;
				}catch(Exception ex){
					
				}
			}
		}
//		System.out.println(singlesent+"\t"+twosents+"\t"+threesents);
//		for(Sentence skk:miniunits){
//			if(skk.getText().startsWith(" -")){
//				System.out.println("-->"+skk.getText());
//				for(Term t:skk.getTerms()){
//					System.out.println(t.getTermId()+"\t"+t.getText()+"\t"+t.getStart_index()+","+t.getEnd_index());
//				}
//				for(Triple<Integer, Integer, String> r:skk.getRelations()){
//					System.out.println("r="+r);
//					if(r.third.contains("TempMea")){
//						return;
//					}
//				}
//			}
//		}
		RelExTool rex=new RelExTool();
		rex.generateTrainingFile(targetcvs,miniunits);
	}

	public static <T> T readModel(String modelName) {
		Classifier classifier = null;
		try {
			Resource fileRource = new ClassPathResource(modelName);
			classifier = (Classifier) SerializationHelper.read(fileRource.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (T) classifier;
	}
	
	public void trainClassifier(String trainfile,String modelpath) throws Exception{
		Classifier m_classifier = new RandomForest();
		File inputFile = new File(trainfile);
		ArffLoader atf = new ArffLoader(); 
		atf.setFile(inputFile);
		Instances instancesTrain = atf.getDataSet(); 
		instancesTrain.setClassIndex(6);
        m_classifier.buildClassifier(instancesTrain); 
        saveModel(m_classifier, modelpath);
	}
	
	public static void saveModel(Classifier classifier, String modelName) {
	    try {
	        SerializationHelper.write(modelName, classifier);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public String predict(String en1, String en2, Double e1e, Double e2s, Double dis, Double shortestdeppath)
			throws Exception {
		List entity1_type = Arrays.asList(GlobalSetting.primaryEntities);
		List entity2_type = Arrays.asList(GlobalSetting.atrributes);
		List rel = Arrays.asList(GlobalSetting.relations);
		Attribute entity1_end_index = new Attribute("entity1_end_index");
		Attribute entity2_start_index = new Attribute("entity2_start_index");
		Attribute distance = new Attribute("distance");
		Attribute shortestdep = new Attribute("shortestdep");
		Attribute entity1_type_attr = new Attribute("entity1_type", entity1_type);
		Attribute entity2_type_attr = new Attribute("entity2_type", entity2_type);
		Attribute rel_attr = new Attribute("rel", rel);

		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		atts.add(entity1_type_attr);
		atts.add(entity2_type_attr);
		atts.add(entity1_end_index);
		atts.add(entity2_start_index);
		atts.add(distance);
		atts.add(shortestdep);
		atts.add(rel_attr);
		Instances adataset = new Instances("TestDataSet", atts, 1);
		Instance inst = new DenseInstance(7);
		inst.setValue(entity1_type_attr, en1);
		inst.setValue(entity2_type_attr, en2);
		inst.setValue(entity2_start_index, e2s);
		inst.setValue(entity1_end_index, e1e);
		inst.setValue(distance, dis);
		inst.setValue(shortestdep, shortestdeppath);
		// inst.setValue(rel_attr, "has-relation");
		inst.setDataset(adataset);
		adataset.setClassIndex(6);
		Double d = classifier.classifyInstance(inst);
		// System.out.println("?="+d);
		return (String) rel.get(d.intValue());
	}
	
	public String predict(String en1, String en2,String cb, Double e1e, Double e2s, Double dis, Double shortestdeppath)
			throws Exception {
		List entity1_type = Arrays.asList(GlobalSetting.primaryEntities);
		List entity2_type = Arrays.asList(GlobalSetting.atrributes);
		List combo = Arrays.asList(GlobalSetting.combo);
		List rel = Arrays.asList(GlobalSetting.relations);
		Attribute entity1_end_index = new Attribute("entity1_end_index");
		Attribute entity2_start_index = new Attribute("entity2_start_index");
		Attribute distance = new Attribute("distance");
		Attribute shortestdep = new Attribute("shortestdep");
		Attribute entity1_type_attr = new Attribute("entity1_type", entity1_type);
		Attribute entity2_type_attr = new Attribute("entity2_type", entity2_type);
		Attribute combo_attr = new Attribute("combo", combo);
		Attribute rel_attr = new Attribute("rel", rel);

		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		atts.add(entity1_type_attr);
		atts.add(entity2_type_attr);
		atts.add(combo_attr);
		atts.add(entity1_end_index);
		atts.add(entity2_start_index);
		atts.add(distance);
		atts.add(shortestdep);
		atts.add(rel_attr);
		Instances adataset = new Instances("TestDataSet", atts, 1);
		Instance inst = new DenseInstance(8);
		inst.setValue(entity1_type_attr, en1);
		inst.setValue(entity2_type_attr, en2);
		inst.setValue(combo_attr, cb);
		inst.setValue(entity2_start_index, e2s);
		inst.setValue(entity1_end_index, e1e);
		inst.setValue(distance, dis);
		inst.setValue(shortestdep, shortestdeppath);
		// inst.setValue(rel_attr, "has-relation");
		inst.setDataset(adataset);
		adataset.setClassIndex(7);
		Double d = classifier.classifyInstance(inst);
		// System.out.println("?="+d);
		return (String) rel.get(d.intValue());
	}

	
	public void generateTrainingFile(String targetcvs, List<Sentence> sents) {
		StringBuffer sb = new StringBuffer();
		for (Sentence sent : sents) {
			if(sent.getText().startsWith(" -")){
				continue;
			}
			System.out.println("start=>"+sent.getText() + "[" + sent.getStart_index() + "," + sent.getEnd_index() + "]");
			List<Term> terms = sent.getTerms();
			List<Term> pes = new ArrayList<Term>();
			List<Term> atts = new ArrayList<Term>();
			for (Term t : terms) {
				System.out.println(t.getTermId() + "\t" + t.getText() + "\t" + t.getCategorey() + " from "
						+ t.getStart_index() + " to " + t.getEnd_index());
				if (Arrays.asList(GlobalSetting.primaryEntities).contains(t.getCategorey())) {
					pes.add(t);
				} else if (Arrays.asList(GlobalSetting.atrributes).contains(t.getCategorey())) {
					atts.add(t);
				}
			}
			// for(Triple<Integer, Integer, String> r:sent.getRelations()){
			// System.out.println(r.toString());
			//
			// }
			System.out.println("________Relation____________");
			System.out.println("S\t"+sent.getText());
			sb.append("S\t"+sent.getText()+"\n");
			for (Term e : pes) {
				for (Term a : atts) {
					//Procedure
					if((e.getCategorey().equals("Condition")||e.getCategorey().equals("Observation")||e.getCategorey().equals("Procedure"))&&a.getCategorey().equals("Value")){
						continue;
					}
					String featurestr = getEntityFeaturesInStr(e, a, sent);
					//Integer sh = getshortestDepPath(e, a, sent.getText());
					String rowtext = featurestr ;
					String truth = queryRelTruth(e.getTermId(), a.getTermId(), sent.getRelations());
					System.out.println("R=\t"+e.getText()+"\t"+a.getText()+"\t"+rowtext + "," + truth );
					sb.append("R\t"+e.getText()+"\t"+a.getText()+"\t"+rowtext + "," + truth + "\n");
				}
			}
			System.out.println("____________________");
		}

		FileUtil.write2File(targetcvs, sb.toString());
	}

	public static String queryRelTruth(Integer a, Integer b, List<Triple<Integer, Integer, String>> relations) {
		for (Triple<Integer, Integer, String> r : relations) {
			if (r.first == a && r.second == b) {
				return r.third;
			}
		}
		return "no_relation";
	}

	public void prepareFeatures(StringBuffer relwriter, List<Sentence> plist) {
		for (Sentence pa : plist) {
			System.out.println("sent:" + pa.getText());
			// 全排列所有term和attributes的组合
			List<Term> termlist = pa.getTerms();
			List<Term> a = new ArrayList<Term>();
			List<Term> b = new ArrayList<Term>();
			if (termlist != null) {
				// System.out.println("list size="+termlist.size());
				for (Term t : termlist) {
					// System.out.println("->"+t.getText());
					if (isMainEntity(t.getCategorey())) {
						a.add(t);
						// System.out.println(t.getText()+";"+t.getCategorey()+"YES!");
					} else if (isAttribute(t.getCategorey())) {
						b.add(t);
						// System.out.println(t.getText()+";"+t.getCategorey()+"NO!");
					}
				}
				// System.out.println("==all candidates==");
				for (Term m : a) {
					for (Term n : b) {
						if (m.getRelations() != null && m.getRelations().containsKey(n.getTermId())) {
							// entity features
							String featurestr = getEntityFeaturesInStr(m, n, pa);
							System.out.println(">>>>>>>>>>>>>>>>>>>>>");
							Integer sh = getshortestDepPath(m, n, pa.getText(),snlp);
							System.out.println("-shortest path-->" + sh);
							System.out.println("<<<<<<<<<<<<<<<<<<<<<<");
							// word features
							// getWordFeatures(m, n, pa);
							System.out.println("has-relation:" + " " + featurestr);
							relwriter.append(featurestr + "," + sh + ",has-relation" + "\n");
							System.out.println(featurestr + "," + sh + ", rel:" + m.getRelations().get(n.getTermId()));
						} else if (m.getRelations() != null) {
							// entity features
							String featurestr = getEntityFeaturesInStr(m, n, pa);
							// word features
							// getWordFeatures(m, n, pa);
							// dependency features
							Integer sh = getshortestDepPath(m, n, pa.getText(),snlp);
							System.out.println("-shortest path-->" + sh);
							System.out.println("no-relation:" + " " + featurestr);
							relwriter.append(featurestr + "," + sh + ",no-relation" + "\n");
							System.out.println(featurestr + "," + sh + ", rel:no_relation");
						}
					}
				}
			}
		}
	}

	public static List<Sentence> convertStandoff(String base_dir) {
		File f = new File(base_dir);
		File[] flist = f.listFiles();
		Set<String> fset = new HashSet<String>();
		for (File file : flist) {
			if (file.getAbsolutePath().endsWith("ann")) {
				String filename = file.getAbsolutePath().split("\\.")[0];
				fset.add(filename);
			}
		}
		System.out.println("size=" + fset.size());
		List<Sentence> plist = new ArrayList<Sentence>();
		for (String base_filename : fset) {
			String annotation = FileUtil.Readfile(base_filename + ".ann");
			String orignial = FileUtil.Readfile(base_filename + ".txt");
			String[] rows = annotation.split("\n");
			Map<String, Term> termmap = new HashMap<String, Term>();
			Map<String, List<Integer>> relrow = new HashMap<String, List<Integer>>();
			List<Integer> allsentenceend = new ArrayList<Integer>();
			int linenum = 1;
			for (int i = -1; i <= orignial.lastIndexOf("\n"); ++i) {
				i = orignial.indexOf("\n", i);
				allsentenceend.add(i);
			}
			// this part is not high efficient. need to be refactory;
			for (String row : rows) {
				String[] att = row.split("\t");
				if (att[0].startsWith("T")) {
					Term t = new Term();
					String[] entity = att[1].split(" ");
					Integer s = Integer.valueOf(entity[1]);
					Integer e = Integer.valueOf(entity[2]);
					t.setTermId(Integer.valueOf(att[0].substring(1)));
					t.setText(att[2]);
					t.setStart_index(s);
					t.setEnd_index(e);
					t.setCategorey(entity[0]);
					termmap.put(att[0], t);
				}
			}
			List<String[]> rels = new ArrayList<String[]>();

			for (String row : rows) {
				String[] att = row.split("\t");
				if (att[0].startsWith("R")) {
					System.out.println(row);
					String[] rel = att[1].split(" ");
					String t1 = rel[1].split(":")[1];
					String t2 = rel[2].split(":")[1];
					// System.out.println(rel[0]+":"+"t1="+t1+";"+"t2="+t2);
					String[] relation = new String[3];
					relation[0] = rel[0];
					relation[1] = t1;
					relation[2] = t2;
					rels.add(relation);
					System.out.println(t1 + "---" + t2);

				}
			}

			int lastend = 0;
			for (Integer s : allsentenceend) {
				String ptext = orignial.substring(lastend, s);
				Sentence p = new Sentence(ptext);
				p.setStart_index(lastend);
				p.setEnd_index(s);
				List<Term> tlist = new ArrayList<Term>();
				List<Triple<Integer, Integer, String>> relations = new ArrayList<Triple<Integer, Integer, String>>();

				for (Map.Entry<String, Term> entry : termmap.entrySet()) {
					Term t = entry.getValue();
					if ((t.getStart_index() >= lastend) && (t.getEnd_index() <= s)) {
						for (String[] r : rels) {
							if (r[1].equals(new String("T" + t.getTermId()))) {
								Triple<Integer, Integer, String> runit = new Triple<Integer, Integer, String>(
										Integer.valueOf(r[1].substring(1)), Integer.valueOf(r[2].substring(1)), r[0]);
								relations.add(runit);
							}
						}
						// HashMap<Integer, String> term_relations =
						// t.getRelations();
						// if (term_relations != null) {
						// term_relations.put(Integer.valueOf(r[2].substring(1)),
						// r[0]);
						// } else {
						// term_relations = new HashMap<Integer, String>();
						// term_relations.put(Integer.valueOf(r[2].substring(1)),
						// r[0]);
						// }
						// t.setRelations(term_relations);
						// }
						// }
						int tempstart = t.getStart_index();
						int tempend = t.getEnd_index();
						int pstart = p.getStart_index();
						t.setStart_index(tempstart - pstart - 1);
						t.setEnd_index(tempend - pstart - 1);
						tlist.add(t);
					}
				}
				p.setTerms(tlist);
				p.setRelations(relations);
				lastend = s;
				plist.add(p);
			}
		}
		return plist;
	}
	
	
	
	public  List<Triple<Integer, Integer, String>> relsRevision(List<Term> terms, List<Triple<Integer, Integer, String>> rels) {
		HashMap<Integer,HashSet<Integer>> relarr=new HashMap<Integer,HashSet<Integer>>();
		List<Triple<Integer, Integer, String>> allrels=new ArrayList<Triple<Integer, Integer, String>>();
		for(Triple<Integer, Integer, String> rel:rels){
			if(rel.third.equals("has_temporal")){
				if(relarr.containsKey(rel.first)){
					HashSet<Integer> temporalset=relarr.get(rel.first);
					temporalset.add(rel.second);
					relarr.put(rel.first, temporalset);
				}else{
					HashSet<Integer> temporalset=new HashSet<Integer>();
					temporalset.add(rel.second);
					relarr.put(rel.first, temporalset);
				}
			}else{
				allrels.add(rel);
			}
		}
		
		for (Entry<Integer, HashSet<Integer>> entry : relarr.entrySet()) {
			if(entry.getValue().size()>1){
				HashSet<Integer> set=entry.getValue();
				int minidistance=1000000;
				int candidate=-1;
				for(Integer i:set){
					Term t1=findTermById(terms,entry.getKey());
					Term t2=findTermById(terms,i);
					Integer distance=(t2.getStart_index()-t1.getEnd_index())*(t2.getStart_index()-t1.getEnd_index());
					if(distance<minidistance){
						candidate=i;
						minidistance=distance;
					}
				}
				Triple<Integer, Integer, String> afterrel=new Triple<Integer, Integer, String>(entry.getKey(),candidate,"has_temporal");
				allrels.add(afterrel);
			}else{
				HashSet<Integer> set=entry.getValue();
				for(Integer a:set){
					Triple<Integer, Integer, String> afterrel=new Triple<Integer, Integer, String>(entry.getKey(),a,"has_temporal");
					allrels.add(afterrel);
				}
			}
		}
		return allrels;
	}
	public Term findTermById(List<Term> terms,Integer termId){
		for(Term t:terms){
			if(t.getTermId()==termId){
				return t;
			}
		}
		return null;
	}
	

	public Integer getshortestDepPath(Term t1, Term t2, String sentence, CoreNLP cnlp) {
		Tree tree = cnlp.parseSentence(sentence);
		Collection<TypedDependency> tdl = cnlp.outputDependency(tree);
		// System.out.println("[" + sentence + "]");
		// System.out.println("[---" + t1.getText() + "," + t2.getText() +
		// "----]");
		// System.out.println(snlp.extractTree(tree));
		DirectedGraph<String, DefaultEdge> g2 = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		Set<IndexedWord> itemset = new HashSet<IndexedWord>();
		for (TypedDependency item : tdl) {
			itemset.add(item.dep());
			itemset.add(item.gov());
		}
		for (IndexedWord iw : itemset) {
			String str = String.valueOf(iw.index());
			g2.addVertex(str);
		}
		for (TypedDependency item : tdl) {
			g2.addEdge(String.valueOf(item.gov().index()), String.valueOf(item.dep().index()));
		}
		
//		ConnectivityInspector ci=new ConnectivityInspector(g2);
//		Set<String> allvertexes=ci.connectedSetOf("1");
//		for(String v:allvertexes){
//			System.out.println("v="+v);
//		}
		List<String> t1list = new ArrayList<String>();
		List<String> t2list = new ArrayList<String>();
		for (IndexedWord iw : itemset) {
			if (iw.beginPosition() >= t1.getStart_index() && iw.endPosition() <= t1.getEnd_index()) {
//				System.out.println(iw.word() + "\t" + iw.beginPosition() + "," + iw.endPosition() + " <"
//						+ t1.getStart_index() + "," + t1.getEnd_index() + ">" + "T1");
				t1list.add(String.valueOf(iw.index()));
			}
			if (iw.beginPosition() >= t2.getStart_index() && iw.endPosition() <= t2.getEnd_index()) {
//				System.out.println(iw.word() + "\t" + iw.beginPosition() + "," + iw.endPosition() + " <"
//						+ t2.getStart_index() + "," + t2.getEnd_index() + ">" + "T2");
				t2list.add(String.valueOf(iw.index()));
			}
		}
		DijkstraShortestPath dijk = new DijkstraShortestPath(g2);
		List<Integer> paths = new ArrayList<Integer>();
		if (t1list.size() != 0 && t2list.size() != 0) {
			for (int a = 0; a < t1list.size(); a++) {
				for (int b = 0; b < t2list.size(); b++) {
					GraphPath<Integer, DefaultWeightedEdge> shortestPath1 = dijk.getPath(t1list.get(a), t2list.get(b));

					if (shortestPath1 != null) {
						paths.add(shortestPath1.getLength());
					} else {
						paths.add(1000);
					}
					GraphPath<Integer, DefaultWeightedEdge> shortestPath2 = dijk.getPath(t2list.get(b), t1list.get(a));
					if (shortestPath2 != null) {
						paths.add(shortestPath2.getLength());
					} else {
						paths.add(1000);
					}
				}
			}
		} else {
			paths.add(1000);
		}
		Collections.sort(paths);
		return paths.get(0);
		// GraphPath<Integer, DefaultWeightedEdge> shortestPath1 =
		// dijk.getPath("2","13");
		// if(shortestPath1==null){
		// return 500;
		// }else{
		// return shortestPath1.getLength();
		// }
	}

	

	public static boolean isMainEntity(String category) {
		if (category.equals("Condition") || category.equals("Observation") || category.equals("Drug")
				|| category.equals("Measurement") || category.equals("Demographic") || category.equals("Procedure")
				|| category.equals("Device")) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isAttribute(String category) {
		if (category.equals("Value") || category.equals("Temporal")) {
			return true;
		} else {
			return false;
		}
	}

	public static String transFeatures2Str(List<Integer> features) {
		int x = 1;
		StringBuffer sb = new StringBuffer();
		for (Integer i : features) {
			sb.append(x + ":" + i + " ");
			x++;
		}
		String result = sb.toString().trim();
		return result;
	}

	public static String getEntityFeaturesInStr(Term t1, Term t2, Sentence p) {
		/**
		 * 
		 * Relation Extraction Features category of entity 1 , start_index of
		 * entity 1, category of entity 2, start_index of entity 2, distance
		 * (number of words separating the two entities),
		 * 
		 **/
		double plength = (p.getEnd_index() - p.getStart_index());
		// String s = String.format("%.2f", i);
		// sb.append(featureindex + ":" + s + " ");
		String f = t1.getCategorey() + "," + t2.getCategorey() + "," 
				+ String.format("%.2f", (double) (t1.getEnd_index() / plength)) + ","
				+ String.format("%.2f", (double) (t2.getStart_index() / plength)) + ","
				+ String.format("%.2f", (double) ((t2.getStart_index() - t1.getEnd_index()) / plength));

		return f;
	}

	public static List<Double> getEntityFeatures(Term t1, Term t2, Sentence p) {
		/**
		 * 
		 * Relation Extraction Features category of entity 1 , start_index of
		 * entity 1, category of entity 2, start_index of entity 2, distance
		 * (number of words separating the two entities),
		 * 
		 **/
		double plength = (p.getEnd_index() - p.getStart_index());

		List<Double> entity1_categorey_featuers = encodeCategory(t1.getCategorey(), GlobalSetting.alldomains);
		List<Double> entity2_categorey_featuers = encodeCategory(t2.getCategorey(), GlobalSetting.alldomains);
		List<Double> features = new ArrayList<Double>();
		features.addAll(entity1_categorey_featuers);
		features.addAll(entity2_categorey_featuers);
		features.add((double) (t1.getEnd_index() / plength));
		features.add((double) t2.getStart_index() / plength);
		features.add((double) (t2.getStart_index() - t1.getEnd_index()) / plength);
		features.add((double) (t1.getEnd_index()));
		features.add((double) t2.getStart_index());
		features.add((double) (t2.getStart_index() - t1.getEnd_index()));
		return features;
	}

	public static void getWordFeatures(Term t1, Term t2, Sentence p) {
		/**
		 * Word features headwords entity2-
		 * 
		 */
		String[] arr1 = t1.getText().split(" ");
		int entity1_headindex = 0;
		int entity1_tailindex = arr1.length - 1;
		System.out.print("|headword of entity 1=" + arr1[entity1_headindex]);
		System.out.println("|tail of entity 1=" + arr1[entity1_tailindex]);

		String[] arr2 = t2.getText().split(" ");
		int entity2_headindex = 0;
		int entity2_tailindex = arr2.length - 1;
		System.out.print("|headword of entity 2=" + arr2[entity2_headindex]);
		System.out.print("|tail of entity 2=" + arr1[entity2_headindex]);
		System.out.print("\n");
	}

	public static void getDependencyFeatures(Term t1, Term t2, Sentence p) {
		String str = p.getText();

	}

	public static List<Double> encodeCategory(String str, String[] allstr) {
		List<Double> features = new ArrayList<Double>();
		for (String s : allstr) {
			if (str.equals(s)) {
				features.add(1.0);
			} else {
				features.add(0.0);
			}
		}
		return features;
	}

	public static String printoutFeatures(List<Double> features) {
		StringBuffer sb = new StringBuffer();
		int featureindex = 1;
		for (Double i : features) {
			String s = String.format("%.2f", i);
			sb.append(featureindex + ":" + s + " ");
			featureindex++;
		}
		return sb.toString().trim();

	}

}
