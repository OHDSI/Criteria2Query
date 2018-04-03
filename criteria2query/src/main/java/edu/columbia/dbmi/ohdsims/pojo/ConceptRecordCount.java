package edu.columbia.dbmi.ohdsims.pojo;

public class ConceptRecordCount {
	private Integer rc;
	private Integer drc;
	public ConceptRecordCount(){
		
	}
	public ConceptRecordCount(Integer rc,Integer drc){
		this.rc=rc;
		this.drc=drc;
	}
	public Integer getRc() {
		return rc;
	}
	public void setRc(Integer rc) {
		this.rc = rc;
	}
	public Integer getDrc() {
		return drc;
	}
	public void setDrc(Integer drc) {
		this.drc = drc;
	}
	
	
	
}
