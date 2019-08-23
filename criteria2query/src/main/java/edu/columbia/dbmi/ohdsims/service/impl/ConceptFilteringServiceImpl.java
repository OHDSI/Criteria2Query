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
import edu.columbia.dbmi.ohdsims.util.FileUtil;
import edu.stanford.nlp.util.Triple;

@Service("conceptFilteringService")
public class ConceptFilteringServiceImpl implements IConceptFilteringService {

	@Override
	public Document removeRedundency(Document doc) {
		// TODO Auto-generated method stub
		Set<String> termpool=new HashSet<String>();
		if (doc.getInitial_event() != null) {
			System.out.println("Initial event");
			List<Paragraph> originalp = doc.getInitial_event();
			originalp = filterOutTerms(originalp, termpool);
			termpool = updateTermSet(originalp, termpool);
			doc.setInitial_event(originalp);
		}
		if (doc.getInclusion_criteria() != null) {
			System.out.println("Inclusion Criteria");
			List<Paragraph> originalp = doc.getInclusion_criteria();
			originalp = filterOutTerms(originalp, termpool);
			termpool = updateTermSet(originalp, termpool);
			doc.setInclusion_criteria(originalp);
		}
		if (doc.getExclusion_criteria() != null) {
			System.out.println("Exclusion Criteria");
			List<Paragraph> originalp = doc.getExclusion_criteria();
			originalp = filterOutTerms(originalp, termpool);
			doc.setExclusion_criteria(originalp);
		}
		return doc;
	}

	// 
	public List<Paragraph> filterOutTerms(List<Paragraph> originalp, Set<String> terms) {
		for (Paragraph p : originalp) {
			if (p.getSents() != null) {
				for (Sentence s : p.getSents()) {
					Set<String> tset=new HashSet<String>();
					if (s.getTerms() != null) {
						for (int i = 0; i < s.getTerms().size(); i++) {
								for(String a:tset){
									System.out.println("~>"+a);
								}
								if(tset.contains(s.getTerms().get(i).getText())){
									System.err.println("remove "+s.getTerms().get(i).getText());
									s.getTerms().remove(i);
									break;
								}else{
									tset.add(s.getTerms().get(i).getText());
								}
							
								}
							}
						}
					}
				}
		return originalp;
	}
	
	public Set<String> updateTermSet(List<Paragraph> originalp, Set<String> terms) {
		for (Paragraph p : originalp) {
			if (p.getSents() != null) {
				for (Sentence s : p.getSents()) {
					if (s.getTerms() != null) {
						for (int i = 0; i < s.getTerms().size(); i++) {
							terms.add(s.getTerms().get(i).getText());
			}
		}
				}
			}
		}
		return terms;
	}
	
	public void printout(Set<String> terms){
		for(String t:terms){
			System.out.println("print out:"+t);
		}
	}
	
	public boolean isEqual(Term t1,Term t2){
		if(t1.getText().equals(t2.getText())&&t1.getCategorey().equals(t2.getCategorey())){
			return true;
		}else{
			return false;
		}
	}

}
