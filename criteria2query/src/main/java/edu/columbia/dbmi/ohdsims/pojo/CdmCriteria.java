package edu.columbia.dbmi.ohdsims.pojo;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * OHDSI Data Model
 *  
 **/
public class CdmCriteria {
	String text;
	String desc;
	boolean initialevent=false;
	boolean included;
	boolean neg;
	String type="ALL";
	String limitTo;
	Integer priorDays;
	Integer postDays;
	List<CdmCriterion> clist;
	private OccurrenceStart occurenceStart;
	private List<LinkedHashSet<Integer>> logic_groups;
	
	public CdmCriteria(){
		
	}
	
	public OccurrenceStart getOccurenceStart() {
		return occurenceStart;
	}

	public void setOccurenceStart(OccurrenceStart occurenceStart) {
		this.occurenceStart = occurenceStart;
	}

	public CdmCriteria(String text,String desc,List<CdmCriterion> clist){
		this.text=text;
		this.desc=desc;
		this.clist=clist;
	}
	
	
	public String getLimitTo() {
		return limitTo;
	}

	public void setLimitTo(String limitTo) {
		this.limitTo = limitTo;
	}

	public Integer getPriorDays() {
		return priorDays;
	}

	public void setPriorDays(Integer priorDays) {
		this.priorDays = priorDays;
	}

	public Integer getPostDays() {
		return postDays;
	}

	public void setPostDays(Integer postDays) {
		this.postDays = postDays;
	}

	public List<LinkedHashSet<Integer>> getLogic_groups() {
		return logic_groups;
	}

	public void setLogic_groups(List<LinkedHashSet<Integer>> logic_groups) {
		this.logic_groups = logic_groups;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isInitialevent() {
		return initialevent;
	}

	public void setInitialevent(boolean initialevent) {
		this.initialevent = initialevent;
	}

	public List<CdmCriterion> getClist() {
		return clist;
	}

	public void setClist(List<CdmCriterion> clist) {
		this.clist = clist;
	}
	public boolean isIncluded() {
		return included;
	}
	public void setIncluded(boolean included) {
		this.included = included;
	}
	public boolean isNeg() {
		return neg;
	}
	public void setNeg(boolean neg) {
		this.neg = neg;
	}
	
	
	
}
