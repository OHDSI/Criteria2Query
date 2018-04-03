package edu.columbia.dbmi.ohdsims.pojo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Document implements Cloneable{
	String title;
	String timestamp;
	String text;
	List<Paragraph> initial_event;
	List<Paragraph> inclusion_criteria;
	List<Paragraph> exclusion_criteria;
	ObservationConstraint initial_event_constraint;
	

	public ObservationConstraint getInitial_event_constraint() {
		return initial_event_constraint;
	}

	public void setInitial_event_constraint(ObservationConstraint initial_event_constraint) {
		this.initial_event_constraint = initial_event_constraint;
	}

	public void setInitial_event(List<Paragraph> initial_event) {
		this.initial_event = initial_event;
	}

	public List<Paragraph> getInitial_event() {
		return initial_event;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public List<Paragraph> getInclusion_criteria() {
		return inclusion_criteria;
	}

	public void setInclusion_criteria(List<Paragraph> inclusion_criteria) {
		this.inclusion_criteria = inclusion_criteria;
	}

	public List<Paragraph> getExclusion_criteria() {
		return exclusion_criteria;
	}

	public void setExclusion_criteria(List<Paragraph> exclusion_criteria) {
		this.exclusion_criteria = exclusion_criteria;
	}

	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		if (initial_event != null) {
			for (Paragraph p : initial_event) {
				if (p.getSents() != null) {
					for (Sentence s : p.getSents()) {
						if (s.getTerms() != null) {
							for (int i = 0; i < s.getTerms().size(); i++) {
								sb.append(s.getTerms().get(i).getTermId()+","+s.getTerms().get(i).getText()+","+s.getTerms().get(i).getVocabularyId()+","+s.getTerms().get(i).getCategorey()+"\n");
							}
						}
					}
				}
			}
		}
		if (inclusion_criteria != null) {
			for (Paragraph p : inclusion_criteria) {
				if (p.getSents() != null) {
					for (Sentence s : p.getSents()) {
						if (s.getTerms() != null) {
							for (int i = 0; i < s.getTerms().size(); i++) {
								sb.append(s.getTerms().get(i).getTermId()+","+s.getTerms().get(i).getText()+","+s.getTerms().get(i).getVocabularyId()+","+s.getTerms().get(i).getCategorey()+"\n");
							}
						}
					}
				}
			}
		}
		if (exclusion_criteria != null) {
			for (Paragraph p : exclusion_criteria) {
				if (p.getSents() != null) {
					for (Sentence s : p.getSents()) {
						if (s.getTerms() != null) {
							for (int i = 0; i < s.getTerms().size(); i++) {
								sb.append(s.getTerms().get(i).getTermId()+","+s.getTerms().get(i).getText()+","+s.getTerms().get(i).getVocabularyId()+","+s.getTerms().get(i).getCategorey()+"\n");
							}
						}
					}
				}
			}
		}	
		return sb.toString();
	}
	
	public Object clone() throws CloneNotSupportedException {  
        return super.clone();  
    }  

}
