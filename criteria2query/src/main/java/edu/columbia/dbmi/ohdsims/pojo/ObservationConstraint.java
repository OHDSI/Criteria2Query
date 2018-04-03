package edu.columbia.dbmi.ohdsims.pojo;

public class ObservationConstraint {
	private String startDate;
	private String endDate;
	private Integer daysBefore;
	private Integer daysAfter;
	private String limitTo;
	
	
	public String getLimitTo() {
		return limitTo;
	}
	public void setLimitTo(String limitTo) {
		this.limitTo = limitTo;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
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
	
	
	
}
