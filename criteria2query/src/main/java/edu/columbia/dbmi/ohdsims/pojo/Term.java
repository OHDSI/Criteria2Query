package edu.columbia.dbmi.ohdsims.pojo;

import java.util.HashMap;

public class Term implements Cloneable{
	private Integer termId;
	private Integer vocabularyId;
	private String text;
	private boolean neg;
	private String categorey;
	private Integer start_index;
	private Integer end_index;
	private HashMap<Integer, String> relations;
	
	
	public Integer getVocabularyId() {
		return vocabularyId;
	}
	public void setVocabularyId(Integer vocabularyId) {
		this.vocabularyId = vocabularyId;
	}
	public Integer getTermId() {
		return termId;
	}
	public void setTermId(Integer termId) {
		this.termId = termId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCategorey() {
		return categorey;
	}
	public void setCategorey(String categorey) {
		this.categorey = categorey;
	}
	public Integer getStart_index() {
		return start_index;
	}
	public void setStart_index(Integer start_index) {
		this.start_index = start_index;
	}
	public Integer getEnd_index() {
		return end_index;
	}
	public void setEnd_index(Integer end_index) {
		this.end_index = end_index;
	}
	
	public String toString(){
		String tostr="<"+this.termId.toString()+">["+this.categorey+"]:"+this.text;
		return tostr;
	}
	public boolean isNeg() {
		return neg;
	}
	public void setNeg(boolean neg) {
		this.neg = neg;
	}
	public HashMap<Integer, String> getRelations() {
		return relations;
	}
	public void setRelations(HashMap<Integer, String> relations) {
		this.relations = relations;
	}
	
	public Object clone() throws CloneNotSupportedException {  
        return super.clone();  
    }  

}
