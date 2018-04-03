package edu.columbia.dbmi.ohdsims.pojo;

public class DisplayCriterion {
	int id;
	String criterion;
	String database;
	Integer patient;
	boolean ehrstatus;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isEhrstatus() {
		return ehrstatus;
	}

	public void setEhrstatus(boolean ehrstatus) {
		this.ehrstatus = ehrstatus;
	}

	public String getCriterion() {
		return criterion;
	}

	public void setCriterion(String criteria) {
		this.criterion = criteria;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public Integer getPatient() {
		return patient;
	}

	public void setPatient(Integer patient) {
		this.patient = patient;
	}

}
