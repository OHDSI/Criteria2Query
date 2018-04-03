package edu.columbia.dbmi.ohdsims.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.Paragraph;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.service.IConceptMappingService;
import edu.columbia.dbmi.ohdsims.tool.ConceptMapping;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import edu.stanford.nlp.util.Triple;
import net.sf.json.JSONArray;

@Service("conceptMappingService")
public class ConceptMappingServiceImpl implements IConceptMappingService{
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
	public Map<String,Integer> createConceptsByTerms(List<Term> terms) {
		Map<String,Integer> conceptsets=new HashMap<String,Integer>();
		for(Term t:terms){
			Integer conceptSetId=cptmap.createConceptSetByUsagi(t.getText(), t.getCategorey());
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
		for (Map.Entry<ConceptSet, Integer> entry : hcs.entrySet()) {
			lscst.add(entry.getKey());
		}
		return lscst;
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
