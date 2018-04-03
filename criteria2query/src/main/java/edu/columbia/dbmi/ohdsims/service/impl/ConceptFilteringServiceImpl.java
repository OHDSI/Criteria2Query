package edu.columbia.dbmi.ohdsims.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.Paragraph;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.service.IConceptFilteringService;
import edu.stanford.nlp.util.Triple;

@Service("conceptFilteringService")
public class ConceptFilteringServiceImpl implements IConceptFilteringService {

	@Override
	public Document removeRedundency(Document doc) {
		// TODO Auto-generated method stub
		Set<Term> termpool=new HashSet<Term>();
		if (doc.getInitial_event() != null) {
			List<Paragraph> originalp = doc.getInitial_event();
			originalp = filterOutTerms(originalp, termpool);
			doc.setInitial_event(originalp);
		}
		if (doc.getInclusion_criteria() != null) {
			List<Paragraph> originalp = doc.getInclusion_criteria();
			originalp = filterOutTerms(originalp, termpool);
			doc.setInclusion_criteria(originalp);
		}
		if (doc.getExclusion_criteria() != null) {
			List<Paragraph> originalp = doc.getExclusion_criteria();
			originalp = filterOutTerms(originalp, termpool);
			doc.setExclusion_criteria(originalp);
		}
		return doc;
	}

	// 
	public List<Paragraph> filterOutTerms(List<Paragraph> originalp, Set<Term> terms) {
		for (Paragraph p : originalp) {
			if (p.getSents() != null) {
				for (Sentence s : p.getSents()) {
					if (s.getTerms() != null) {
						for (int i = 0; i < s.getTerms().size(); i++) {
							for(Term t:terms){
								if(isEqual(t, s.getTerms().get(i))){
									s.getTerms().remove(i);
									System.err.println("remove");
								}else{
									terms.add(s.getTerms().get(i));
								}
							}
						}
					}
				}
			}
		}
		return originalp;
	}
	
	public boolean isEqual(Term t1,Term t2){
		if(t1.getText().equals(t2.getText())&&t1.getCategorey().equals(t2.getCategorey())){
			return true;
		}else{
			return false;
		}
	}

}
