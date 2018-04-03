package edu.columbia.dbmi.ohdsims.pojo;

import java.util.Map;

/**
 * Basic criterion class
 * 
 * **/
public class CdmCriterion {
	private Integer criterionId;
	private String orginialtext;
	private Integer conceptsetId;
	private boolean neg;
	private String domain;
	private Map<String,String> attributes;
	private TemporalConstraint[] temporalwindow;
	private OccurrenceStart occurenceStart;
	
	
	
	public OccurrenceStart getOccurenceStart() {
		return occurenceStart;
	}
	public void setOccurenceStart(OccurrenceStart occurenceStart) {
		this.occurenceStart = occurenceStart;
	}
	public Integer getCriterionId() {
		return criterionId;
	}
	public void setCriterionId(Integer criterionId) {
		this.criterionId = criterionId;
	}
	public String getOrginialtext() {
		return orginialtext;
	}
	public void setOrginialtext(String orginialtext) {
		this.orginialtext = orginialtext;
	}
	public Integer getConceptsetId() {
		return conceptsetId;
	}
	public void setConceptsetId(Integer conceptsetId) {
		this.conceptsetId = conceptsetId;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	public TemporalConstraint[] getTemporalwindow() {
		return temporalwindow;
	}
	public void setTemporalwindow(TemporalConstraint[] temporalwindow) {
		this.temporalwindow = temporalwindow;
	}
	public boolean isNeg() {
		return neg;
	}
	public void setNeg(boolean neg) {
		this.neg = neg;
	}
	
	
}
