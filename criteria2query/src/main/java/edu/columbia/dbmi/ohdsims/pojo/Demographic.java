package edu.columbia.dbmi.ohdsims.pojo;

public class Demographic {
	private Integer age_low;
	private Integer age_high;
	private String age_relation;
	private String gender;
	private String race;
	private String ethnicity;
	
	
	public Integer getAge_low() {
		return age_low;
	}
	public void setAge_low(Integer age_low) {
		this.age_low = age_low;
	}
	public Integer getAge_high() {
		return age_high;
	}
	public void setAge_high(Integer age_high) {
		this.age_high = age_high;
	}
	
	public String getAge_relation() {
		return age_relation;
	}
	public void setAge_relation(String age_relation) {
		this.age_relation = age_relation;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public String getEthnicity() {
		return ethnicity;
	}
	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}
	
	
	
}
