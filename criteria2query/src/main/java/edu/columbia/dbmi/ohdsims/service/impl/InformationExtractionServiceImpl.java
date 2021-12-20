package edu.columbia.dbmi.ohdsims.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;

import edu.columbia.dbmi.ohdsims.pojo.DisplayCriterion;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.Paragraph;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.service.IInformationExtractionService;
import edu.columbia.dbmi.ohdsims.tool.ConceptMapping;
import edu.columbia.dbmi.ohdsims.tool.CoreNLP;
import edu.columbia.dbmi.ohdsims.tool.FeedBackTool;
import edu.columbia.dbmi.ohdsims.tool.LogicAnalysisTool;
import edu.columbia.dbmi.ohdsims.tool.NERTool;
import edu.columbia.dbmi.ohdsims.tool.NegReTool;
import edu.columbia.dbmi.ohdsims.tool.ReconTool;
import edu.columbia.dbmi.ohdsims.tool.RelExTool;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;
import net.sf.json.JSONObject;


@Service("ieService")
public class InformationExtractionServiceImpl implements IInformationExtractionService {
	
	private static Logger logger = LogManager.getLogger(InformationExtractionServiceImpl.class);
	
	CoreNLP corenlp = new CoreNLP();
	NERTool nertool = new NERTool();
	NegReTool negtool = new NegReTool();
	LogicAnalysisTool logictool = new LogicAnalysisTool();
	RelExTool reltool = new RelExTool();
	ConceptMapping cptmap = new ConceptMapping();
	ReconTool recontool=new ReconTool();
	
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

	@Override
	public Document translateByDoc(String initial_event, String inclusion_criteria, String exclusion_criteria) {
		Document doc = new Document();
		doc.setInitial_event(translateByBlock(initial_event));
		logger.info("initial_event="+initial_event);
		doc.setInclusion_criteria(translateByBlock(inclusion_criteria));
		logger.info("inclusion_criteria="+initial_event);
		doc.setExclusion_criteria(translateByBlock(exclusion_criteria));
		logger.info("exclusion_criteria="+initial_event);
		return doc;
	}

	@Override
	public List<Paragraph> translateByBlock(String text) {
		String[] pas = text.split("\n");
		List<Paragraph> spas = new ArrayList<Paragraph>();
		if (text.length() == 0) {
			return spas;
		}
		int pairs=0;
		for (String p : pas) {
			Paragraph pa = new Paragraph();
			List<String> block_text = corenlp.splitParagraph(p);
			List<Sentence> sents = new ArrayList<Sentence>();
			// NER, relation, negation, logic are operated against sentence
			// level
			for (String s : block_text) {
				// filter bracket
				s = s.replaceAll("-LRB-", "(");
				s = s.replaceAll("-RRB-", ")");
				s = s.replaceAll("-LSB-", "[");
				s = s.replaceAll("-RSB-", "]");
				s = s.replaceAll("-LCB-", "{");
				s = s.replaceAll("-RCB-", "}");
				Sentence sent = new Sentence(" "+s+" ");
				String crf_results=sent.getText();
				if(s.trim().split(" ").length<3){
					crf_results=nertool.nerByDicLookUp(sent.getText().trim());
				}
				
				if(crf_results.length()<=sent.getText().length()){
					crf_results = nertool.nerByCrf(sent.getText());
				}
				//System.out.println("crf_results="+crf_results);
				List<Term> terms = nertool.formulateNerResult(sent.getText(), crf_results);
				
				//Aho–Corasick for rule-based screening
				try{
				
				terms=nertool.nerEnhancedByACAlgorithm(sent.getText(),terms);
				
				}catch(Exception e){
					
				}
				//System.out.println("===> after enhanced ====>");
				
				
				//
				terms=patchTermLevel(terms);

				String display="";
				try{
					display = nertool.trans4display(sent.getText(),terms);
				}catch(Exception ex){
					
				}
				//String display = nertool.trans2Html(crf_results);			
				// displaying
				sent.setTerms(terms);
				sent.setDisplay(display);
				List<Term> primary_entities = new ArrayList<Term>();
				List<Term> attributes = new ArrayList<Term>();
				// Separate primary terms and attributes
				for (Term t : terms) {
					if (Arrays.asList(GlobalSetting.primaryEntities).contains(t.getCategorey())) {
						// Negation detection
						boolean ntag = negtool.negReg(sent.getText(), t.getText(), terms);
						t.setNeg(ntag);
						primary_entities.add(t);

					} else if (Arrays.asList(GlobalSetting.atrributes).contains(t.getCategorey())) {
						attributes.add(t);
					}
				}
				//added complex entity method
//				
//				for(int a=0;a<primary_entities.size();a++){
//					System.out.println("=>"+primary_entities.get(a).getText());
//					if(recontool.isCEE(primary_entities.get(a).getText())){
//						Term t=primary_entities.get(a);
////						String category=t.getCategorey();
////						String entity=t.getText();
////						Integer start_index=t.getStart_index();
////						Integer end_index=t.getEnd_index();
//						List<String> concepts=recontool.resolve(t.getText());
//						for(String c:concepts){
//							//System.out.println("=>"+c);
//							Term ret=new Term();
//							ret.setText(c);
//							ret.setNeg(t.isNeg());
//							ret.setCategorey(t.getCategorey());
//							ret.setStart_index(t.getStart_index());
//							ret.setEnd_index(t.getEnd_index());
//							primary_entities.add(ret);
//						}
//						primary_entities.remove(a);
//					}
//				}
				//end complex entity handling
				
				List<Term> allterms = new ArrayList<Term>();
				allterms.addAll(primary_entities);
				allterms.addAll(attributes);
				sent.setTerms(allterms);
				List<Triple<Integer, Integer, String>> relations = new ArrayList<Triple<Integer, Integer, String>>();
				for (Term t : primary_entities) {
					for (Term a : attributes) {
						// relation extraction
						// It is good to reuse by "String" rather than relation
						// id or something
						pairs++;
						String rel = "no_relation";
						boolean relflag = false;
						//reltool.getshortestDepPath(t, a, sent.getText(), corenlp) < 1000
						if (logictool.isConnected(t, a, sent.getText())) {
							relflag = true;
						}
						relflag = true;
						if (relflag == true && a.getCategorey().equals("Value")) {
							rel = "has_value";
						}
						if (relflag == true && a.getCategorey().equals("Temporal")) {
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
				relations=reltool.relsRevision(allterms,relations);
				sent.setRelations(relations);
				
				long startTime = System.currentTimeMillis();
				List<LinkedHashSet<Integer>> logic_groups =logictool.ddep(sent.getText(), primary_entities);
				long endTime = System.currentTimeMillis();
				sent.setLogic_groups(logic_groups);
				sents.add(sent);
			}
			pa.setSents(sents);
			logger.info(JSONObject.fromObject(pa));
			spas.add(pa);
		}
		return spas;
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
		if (doc.getInitial_event() != null) {
			List<Paragraph> originalp = doc.getInitial_event();
			originalp = patchDocLevel(originalp);
			doc.setInitial_event(originalp);
		}
		if (doc.getInclusion_criteria() != null) {
			List<Paragraph> originalp = doc.getInclusion_criteria();
			originalp = patchDocLevel(originalp);
			doc.setInclusion_criteria(originalp);
		}
		if (doc.getExclusion_criteria() != null) {
			List<Paragraph> originalp = doc.getExclusion_criteria();
			originalp = patchDocLevel(originalp);
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
										||s.getTerms().get(i).getCategorey().equals("Drug")
										||s.getTerms().get(i).getCategorey().equals("Measurement")
										||s.getTerms().get(i).getCategorey().equals("Procedure")
										||s.getTerms().get(i).getCategorey().equals("Observation")) {
									String text = s.getTerms().get(i).getText();
									
									if(recontool.isCEE(text)){
										
										Term t=s.getTerms().get(i);
										String category=t.getCategorey();
										String entity=t.getText();
										Integer start_index=t.getStart_index();
										Integer end_index=t.getEnd_index();
										List<String> concepts=recontool.resolve(t.getText());
										int count=0;
										for(String c:concepts){
											//System.out.println("=>"+c);
											Term ret=new Term();
											Integer newtId=t.getTermId()+100+count;
											ret.setTermId(newtId);
											ret.setText(c);
											ret.setNeg(t.isNeg());
											ret.setCategorey(t.getCategorey());
											ret.setStart_index(t.getStart_index());
											ret.setEnd_index(t.getEnd_index());
											s.getTerms().add(ret);
//											List<Triple<Integer, Integer, String>> itsrelations=s.getRelations();
//											for(Triple<Integer, Integer, String> rel:itsrelations){
//												if(rel.first==t.getTermId()){
//													Triple<Integer, Integer, String> newrel=new Triple<Integer, Integer, String>(newtId, rel.second, rel.third);
//													s.getRelations().add(newrel);
//												}
//											}
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
	
	public List<Term> patchTermLevel(List<Term> terms){
		for(int i=0;i<terms.size();i++){
			List<String> lemmas = corenlp.getLemmasList(terms.get(i).getText());
			if ((lemmas.contains("day") || lemmas.contains("month") || lemmas.contains("year"))&&(lemmas.contains("old")==false)&&(lemmas.contains("/")==false)) {
				if(i>0 && terms.get(i-1).getCategorey().equals("Demographic")==false){
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
										|| lemmas.contains("younger")) {
									// if there is no age in this sentence.
									if (hasDemoAge(s.getTerms())==false) {
										Term t = new Term();
										t.setCategorey("Demographic");
										t.setStart_index(-1);
										t.setEnd_index(-1);
										t.setNeg(false);
										t.setText("age");
										Integer assignId = s.getTerms().size();
										t.setTermId(assignId);
										s.getTerms().add(t);
										s.getRelations().add(new Triple<Integer, Integer, String>(assignId,
												s.getTerms().get(i).getTermId(), "has_value"));
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
							if (isAcronym(s.getTerms().get(i).getText())) {
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
		// if one is less than three letters.
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
		List<String> initevent=new ArrayList<String>();
		List<Paragraph> initial_events=doc.getInitial_event();
		if(initial_events!=null){
			for(Paragraph p:initial_events){
				List<Sentence> sents=p.getSents();
				if(sents!=null){
					for(Sentence s:sents){
						List<Term> terms=s.getTerms();
						if(terms!=null){
							for(Term t:terms){
								if(Arrays.asList(GlobalSetting.conceptSetDomains).contains(t.getCategorey())){
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

}
