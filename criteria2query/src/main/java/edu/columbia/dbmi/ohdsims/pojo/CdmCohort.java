package edu.columbia.dbmi.ohdsims.pojo;

import java.util.List;

public class CdmCohort {
	private String cohorttitle;
	private String creator;
	private Integer daysBefore;
	private Integer daysAfter;
	private String limitTo;
	private List<CdmCriteria> initial_event;
	private List<CdmCriteria> additional_criteria;
	private OccurrenceStart occurrenceStart;
	
	
	
	public OccurrenceStart getOccurrenceStart() {
		return occurrenceStart;
	}
	public void setOccurrenceStart(OccurrenceStart occurrenceStart) {
		this.occurrenceStart = occurrenceStart;
	}
	public String getLimitTo() {
		return limitTo;
	}
	public void setLimitTo(String limitTo) {
		this.limitTo = limitTo;
	}
	
	public Integer getDaysBefore() {
		return daysBefore;
	}
	public void setDaysBefore(Integer daysBefore) {
		this.daysBefore = daysBefore;
	}
	public Integer getDaysAfter() {
		return daysAfter;
	}
	public void setDaysAfter(Integer daysAfter) {
		this.daysAfter = daysAfter;
	}
	public String getCohorttitle() {
		return cohorttitle;
	}
	public void setCohorttitle(String cohorttitle) {
		this.cohorttitle = cohorttitle;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public List<CdmCriteria> getInitial_event() {
		return initial_event;
	}
	public void setInitial_event(List<CdmCriteria> initial_event) {
		this.initial_event = initial_event;
	}
	public List<CdmCriteria> getAdditional_criteria() {
		return additional_criteria;
	}
	public void setAdditional_criteria(List<CdmCriteria> additional_criteria) {
		this.additional_criteria = additional_criteria;
	}	
	
}
