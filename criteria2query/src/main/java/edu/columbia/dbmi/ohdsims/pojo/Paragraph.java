package edu.columbia.dbmi.ohdsims.pojo;

import java.util.List;

/**
 * paragraph
 * */
public class Paragraph implements Cloneable{
	int pid;
	List<Sentence> sents;
	boolean include;
	int pattern;// 
	
	public List<Sentence> getSents() {
		return sents;
	}
	public void setSents(List<Sentence> sents) {
		this.sents = sents;
	}
	public boolean isInclude() {
		return include;
	}
	public void setInclude(boolean include) {
		this.include = include;
	}
	public int getPattern() {
		return pattern;
	}
	public void setPattern(int pattern) {
		this.pattern = pattern;
	}
	public Object clone() throws CloneNotSupportedException {  
        return super.clone();  
    }
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	
}
