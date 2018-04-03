package edu.columbia.dbmi.ohdsims.pojo;

public class TemporalConstraint {
	Integer start_days;
	Integer start_offset;//-1: before ; 1 after
	Integer end_days;
	Integer end_offset;
	public Integer getStart_days() {
		return start_days;
	}
	public void setStart_days(Integer start_days) {
		this.start_days = start_days;
	}
	public Integer getStart_offset() {
		return start_offset;
	}
	public void setStart_offset(Integer start_offset) {
		this.start_offset = start_offset;
	}
	public Integer getEnd_days() {
		return end_days;
	}
	public void setEnd_days(Integer end_days) {
		this.end_days = end_days;
	}
	public Integer getEnd_offset() {
		return end_offset;
	}
	public void setEnd_offset(Integer end_offset) {
		this.end_offset = end_offset;
	}
	
	
}
