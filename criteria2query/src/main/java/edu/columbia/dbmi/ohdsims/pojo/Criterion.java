package edu.columbia.dbmi.ohdsims.pojo;

public class Criterion {
	private Integer conceptSetId;
	private String conceptSetName;
	private String domain;
	private boolean neg;
	private Integer beforeDays;
	private Integer afterDays;
	private Integer measurement_type;
	private Double measurement_low;
	private Double measurement_high;
	private boolean isInitialEvent;
	private boolean isInclusionCriterion;
	private Integer temporaltype;
	private EventDate starting;
	private EventDate ending;
	
	public Integer getTemporaltype() {
		return temporaltype;
	}
	public void setTemporaltype(Integer temporaltype) {
		this.temporaltype = temporaltype;
	}
	public Double getMeasurement_low() {
		return measurement_low;
	}
	public void setMeasurement_low(Double measurement_low) {
		this.measurement_low = measurement_low;
	}
	public Double getMeasurement_high() {
		return measurement_high;
	}
	public void setMeasurement_high(Double measurement_high) {
		this.measurement_high = measurement_high;
	}
	public boolean isInclusionCriterion() {
		return isInclusionCriterion;
	}
	public void setInclusionCriterion(boolean isInclusionCriterion) {
		this.isInclusionCriterion = isInclusionCriterion;
	}
	public Integer getConceptSetId() {
		return conceptSetId;
	}
	public void setConceptSetId(Integer conceptSetId) {
		this.conceptSetId = conceptSetId;
	}
	public String getConceptSetName() {
		return conceptSetName;
	}
	public void setConceptSetName(String conceptSetName) {
		this.conceptSetName = conceptSetName;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public boolean isNeg() {
		return neg;
	}
	public void setNeg(boolean neg) {
		this.neg = neg;
	}
	public Integer getBeforeDays() {
		return beforeDays;
	}
	public void setBeforeDays(Integer beforeDays) {
		this.beforeDays = beforeDays;
	}
	public Integer getAfterDays() {
		return afterDays;
	}
	public void setAfterDays(Integer afterDays) {
		this.afterDays = afterDays;
	}
	public Integer getMeasurement_type() {
		return measurement_type;
	}
	public void setMeasurement_type(Integer measurement_type) {
		this.measurement_type = measurement_type;
	}
	public boolean isInitialEvent() {
		return isInitialEvent;
	}
	public void setInitialEvent(boolean isInitialEvent) {
		this.isInitialEvent = isInitialEvent;
	}
	
	
}
