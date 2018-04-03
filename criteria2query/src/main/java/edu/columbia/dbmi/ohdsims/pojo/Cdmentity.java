package edu.columbia.dbmi.ohdsims.pojo;

import java.util.HashMap;
import java.util.List;

public class Cdmentity {
	String index;
	String entityname;
	String cui;
	String domain;
	HashMap<String, String> relations;
	int startindex;
	int endindex;
	boolean neg;
	
	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	public boolean isNeg() {
		return neg;
	}

	public void setNeg(boolean neg) {
		this.neg = neg;
	}

	public int getEndindex() {
		return endindex;
	}

	public void setEndindex(int endindex) {
		this.endindex = endindex;
	}

	public int getStartindex() {
		return startindex;
	}

	public void setStartindex(int startindex) {
		this.startindex = startindex;
	}

	public HashMap<String, String> getRelations() {
		return relations;
	}

	public void setRelations(HashMap<String, String> relations) {
		this.relations = relations;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getEntityname() {
		return entityname;
	}

	public void setEntityname(String entityname) {
		this.entityname = entityname;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
