package edu.columbia.dbmi.ohdsims.service;

import java.util.List;
import java.util.Map;

import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.stanford.nlp.util.Triple;


public interface IConceptMappingService {
	public String extendAbbr(String abbr);
	public Map<String,Integer> createConceptsByTerms(List<ConceptSet> cslist, List<Term> terms);
	public List<Term> getDistinctTerm(Document doc);
	public List<Term> getAllTerms(Document doc);
	public List<String[]> evaluateConceptMapping(List<Term> terms);
	public List<Triple<Integer,Integer,String>> getAllRelsByDoc(Document doc);
	public Document linkConceptSetsToTerms(Document doc,Map<String,Integer> conceptSetIds);
	public List<ConceptSet> mapAndSortConceptSetByEntityName(String entityname);
	public Document ignoreTermByEntityText(Document doc,String termtext);
	public List<ConceptSet> mapAndSortConceptSetByEntityNameFromALlConceptSets(List<ConceptSet> conceptsets,
			String entityname);
	public List<ConceptSet> getAllConceptSets();
}
