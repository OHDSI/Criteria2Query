package edu.columbia.dbmi.ohdsims.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;

import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.Paragraph;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.service.IConceptMappingService;
import edu.columbia.dbmi.ohdsims.tool.ConceptMapping;
import edu.columbia.dbmi.ohdsims.tool.OHDSIApis;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import edu.stanford.nlp.util.Triple;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("conceptMappingService")
public class ConceptMappingServiceImpl implements IConceptMappingService{
	private static Logger logger = LogManager.getLogger(ConceptMappingServiceImpl.class);
	ConceptMapping cptmap=new ConceptMapping();
	
	@Override
	public String extendAbbr(String abbr) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Term> getDistinctTerm(Document doc) {
		List<Term> allterms=new ArrayList<Term>();
		LinkedHashSet<String> termtexts=new LinkedHashSet<String>();
		for(Term t:getAllTermsByDoc(doc)){
			if(Arrays.asList(GlobalSetting.conceptSetDomains).contains(t.getCategorey())){
				if(termtexts.contains(t.getText())==false){
					allterms.add(t);
					termtexts.add(t.getText());
				}
			}
		}
		return allterms;
	}
	
	public List<Term> getAllTermsByDoc(Document doc) {
		List<Term> allterms = new ArrayList<Term>();
		if (doc.getInitial_event()!= null) {
			for (Paragraph p : doc.getInitial_event()) {
				if (p.getSents() != null) {
					for (Sentence s : p.getSents()) {
						allterms.addAll(s.getTerms());
					}
				}
			}
		}
		if (doc.getInclusion_criteria() != null) {
			for (Paragraph p : doc.getInclusion_criteria()) {
				if (p.getSents() != null) {
					for (Sentence s : p.getSents()) {
						allterms.addAll(s.getTerms());
					}
				}
			}
		}
		if (doc.getExclusion_criteria() != null) {
			for (Paragraph p : doc.getExclusion_criteria()) {
				if (p.getSents() != null) {
					for (Sentence s : p.getSents()) {
						allterms.addAll(s.getTerms());
					}
				}
			}
		}
		return allterms;
	}

	@Override
	public Map<String,Integer> createConceptsByTerms(List<ConceptSet> cslist, List<Term> terms) {
		Map<String,Integer> conceptsets=new HashMap<String,Integer>();
		for(Term t:terms){
			Integer conceptSetId=cptmap.createConceptSetByUsagi(cslist, t.getText(), t.getCategorey());
			conceptsets.put(t.getText(),conceptSetId);
		}
		return conceptsets;
	}
	
	public List<String[]> evaluateConceptMapping(List<Term> terms){
		List<String[]> mapresults=new ArrayList<String[]>();
		for(Term t:terms){
			String[] mapresult=cptmap.getCloestConceptByUsagi(t.getText(), t.getCategorey());
			mapresults.add(mapresult);
		}
		return mapresults;
	}

	@Override
	public Document linkConceptSetsToTerms(Document doc,Map<String,Integer> conceptSetIds) {
		if (doc.getInitial_event() != null) {
			List<Paragraph> plist=doc.getInitial_event();
			plist=addConceptSetIDtoTerm(conceptSetIds, plist);
			doc.setInitial_event(plist);
		}
		if (doc.getInclusion_criteria() != null) {
			List<Paragraph> plist=doc.getInclusion_criteria();
			plist=addConceptSetIDtoTerm(conceptSetIds, plist);
			doc.setInclusion_criteria(plist);
		}
		if (doc.getExclusion_criteria() != null) {
			List<Paragraph> plist=doc.getExclusion_criteria();
			plist=addConceptSetIDtoTerm(conceptSetIds, plist);
			doc.setExclusion_criteria(plist);
		}	
		return doc;
	}
	public List<Paragraph> addConceptSetIDtoTerm(Map<String, Integer> conceptSetIds, List<Paragraph> plist) {
		for (Paragraph p : plist) {
			if (p.getSents() != null) {
				for (Sentence s : p.getSents()) {
					if (s.getTerms() != null) {
						for (int i = 0; i < s.getTerms().size(); i++) {
							Integer conceptSetId=conceptSetIds.get(s.getTerms().get(i).getText());
							
							System.out.println(s.getTerms().get(i).getText()+" linked?=>"+conceptSetId);
							s.getTerms().get(i).setVocabularyId(conceptSetId);
						}
					}
				}
			}
		}
		return plist;
	}

	@Override
	public List<ConceptSet> mapAndSortConceptSetByEntityName(String entityname) {
		List<ConceptSet> lscst=new ArrayList<ConceptSet>();
		LinkedHashMap<ConceptSet, Integer> hcs = cptmap.mapConceptSetByEntity(entityname);
		Set<String> conceptSetlist=new HashSet<String>();
		
		//filter out
		for (Map.Entry<ConceptSet, Integer> entry : hcs.entrySet()) {
			//if(conceptSetlist.contains(entry.getKey().getName())==false){
				lscst.add(entry.getKey());
				//conceptSetlist.add(entry.getKey().getName());
			//}
		}
		return lscst;
	}
		
	@Override
	public List<ConceptSet> getAllConceptSets(){
		List<ConceptSet> cslist = cptmap.getallConceptSet();
		return cslist;
	}
	@Override
	public List<ConceptSet> mapAndSortConceptSetByEntityNameFromALlConceptSets(List<ConceptSet> conceptsets, String entityname) {
		List<ConceptSet> lscst=new ArrayList<ConceptSet>();
		LinkedHashMap<ConceptSet, Integer> hcs = cptmap.mapConceptSetByEntityFromAllConceptSets(conceptsets,entityname);
		//filter out
		for (Map.Entry<ConceptSet, Integer> entry : hcs.entrySet()) {
			//if(conceptSetlist.contains(entry.getKey().getName())==false){
				lscst.add(entry.getKey());
				//conceptSetlist.add(entry.getKey().getName());
			//}
		}
		return lscst;
	}
		
	
	public boolean conceptSetisEqual(Integer conceptSet1, Integer conceptSet2){
		Set<Integer> set1=getAllConceptIds(conceptSet1);
		Set<Integer> set2=getAllConceptIds(conceptSet2);
		if (set1 == null && set2 == null) {    
			   return true; // Both are null    
			  }        
			  if (set1 == null || set2 == null || set1.size() != set2.size()    
			    || set1.size() == 0 || set2.size() == 0) {    
			   return false;    
			  }    
			      
			  Iterator ite1 = set1.iterator();    
			  Iterator ite2 = set2.iterator();    
			      
			  boolean isFullEqual = true;    
			      
			 while (ite2.hasNext()) {    
			   if (!set1.contains(ite2.next())) {    
			    isFullEqual = false;    
			   }    
			  }  	      
		return isFullEqual;    
		
	}
	public Set<Integer> getAllConceptIds(Integer conceptSetId) {
		JSONObject set1=OHDSIApis.querybyconceptSetid(conceptSetId);
		Set<Integer> conceptIds=new HashSet<Integer>();
		JSONObject jo=(JSONObject) set1.get("expression");
		JSONArray ja=(JSONArray) jo.get("items");
		if(ja.size()>0){
			for(int i=0;i<ja.size();i++){
				JSONObject conceptunit=(JSONObject) ja.get(i);
				JSONObject concept=(JSONObject) conceptunit.get("concept");
				conceptIds.add(concept.getInt("CONCEPT_ID"));
			}
		}
		return conceptIds;
	}
	
	
	@Override
	public Document ignoreTermByEntityText(Document doc, String termtext) {
		if (doc.getInitial_event() != null) {
			List<Paragraph> orignialp=doc.getInitial_event(); 
			orignialp=removeTerm(orignialp,termtext);
			doc.setInitial_event(orignialp);
		}
		if (doc.getInclusion_criteria() != null) {
			List<Paragraph> orignialp=doc.getInclusion_criteria(); 
			orignialp=removeTerm(orignialp,termtext);
			doc.setInclusion_criteria(orignialp);
		}
		if (doc.getExclusion_criteria() != null) {
			List<Paragraph> orignialp=doc.getExclusion_criteria();
			orignialp=removeTerm(orignialp,termtext);
			doc.setExclusion_criteria(orignialp);
		}
		return doc;
	}
	
	public List<Paragraph> removeTerm(List<Paragraph> originalp,String termtext){
		for (Paragraph p : originalp) {
			if (p.getSents() != null) {
				for (Sentence s : p.getSents()) {
					if (s.getTerms() != null) {
						for (int i = 0; i < s.getTerms().size(); i++) {
							if(s.getTerms().get(i).getText().equals(termtext)){
								s.getTerms().remove(i);
							}
						}
					}				
				}	
			}
		}
		return originalp;
	}
	@Override
	public List<Term> getAllTerms(Document doc) {
		// TODO Auto-generated method stub
		List<Term> allterms=new ArrayList<Term>();
		LinkedHashSet<String> termtexts=new LinkedHashSet<String>();
		for(Term t:getAllTermsByDoc(doc)){
			allterms.add(t);
		}
		return allterms;
	}
	@Override
	public List<Triple<Integer, Integer, String>> getAllRelsByDoc(Document doc) {
		// TODO Auto-generated method stub
		List<Triple<Integer, Integer, String>> allrels = new ArrayList<Triple<Integer, Integer, String>>();
		if (doc.getInitial_event()!= null) {
			for (Paragraph p : doc.getInitial_event()) {
				if (p.getSents() != null) {
					for (Sentence s : p.getSents()) {
						allrels.addAll(s.getRelations());
					}
				}
			}
		}
		if (doc.getInclusion_criteria() != null) {
			for (Paragraph p : doc.getInclusion_criteria()) {
				if (p.getSents() != null) {
					for (Sentence s : p.getSents()) {
						allrels.addAll(s.getRelations());
					}
				}
			}
		}
		if (doc.getExclusion_criteria() != null) {
			for (Paragraph p : doc.getExclusion_criteria()) {
				if (p.getSents() != null) {
					for (Sentence s : p.getSents()) {
						allrels.addAll(s.getRelations());
					}
				}
			}
		}
		return allrels;
		
	}
	
	
}
