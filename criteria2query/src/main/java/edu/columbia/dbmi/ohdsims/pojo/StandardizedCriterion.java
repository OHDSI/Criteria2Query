package edu.columbia.dbmi.ohdsims.pojo;


/**
 * Basic criterion class
 * 
 * **/
public class StandardizedCriterion {
	private Integer conceptSetId;
	private String domain;
	private EventDate starting;
	private EventDate ending;
	
	public StandardizedCriterion(Integer conceptSetId,String domain,EventDate starting, EventDate ending){
		this.conceptSetId=conceptSetId;
		this.domain=domain;
		this.starting=starting;
		this.ending=ending;
	}
	public Integer getConceptSetId() {
		return conceptSetId;
	}
	public void setConceptSetId(Integer conceptSetId) {
		this.conceptSetId = conceptSetId;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public EventDate getStarting() {
		return starting;
	}
	public void setStarting(EventDate starting) {
		this.starting = starting;
	}
	public EventDate getEnding() {
		return ending;
	}
	public void setEnding(EventDate ending) {
		this.ending = ending;
	}
	
	
}
