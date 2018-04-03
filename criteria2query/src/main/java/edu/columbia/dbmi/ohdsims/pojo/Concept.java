package edu.columbia.dbmi.ohdsims.pojo;

public class Concept {
 Integer CONCEPT_ID;
 String CONCEPT_NAME;
 String STANDARD_CONCEPT;
 String INVALID_REASON;
 String CONCEPT_CODE;
 String DOMAIN_ID;
 String VOCABULARY_ID;
 String CONCEPT_CLASS_ID;
 String STANDARD_CONCEPT_CAPTION;
 String INVALID_REASON_CAPTION;
//int RC;
//int DRC;
//public int getRC() {
//	return RC;
//}
//public void setRC(int rC) {
//	RC = rC;
//}
//public int getDRC() {
//	return DRC;
//}
//public void setDRC(int dRC) {
//	DRC = dRC;
//}
public Integer getCONCEPT_ID() {
	return CONCEPT_ID;
}
public void setCONCEPT_ID(Integer cONCEPT_ID) {
	CONCEPT_ID = cONCEPT_ID;
}
public String getCONCEPT_NAME() {
	return CONCEPT_NAME;
}
public void setCONCEPT_NAME(String cONCEPT_NAME) {
	CONCEPT_NAME = cONCEPT_NAME;
}
public String getSTANDARD_CONCEPT() {
	return STANDARD_CONCEPT;
}
public void setSTANDARD_CONCEPT(String sTANDARD_CONCEPT) {
	STANDARD_CONCEPT = sTANDARD_CONCEPT;
}
public String getINVALID_REASON() {
	return INVALID_REASON;
}
public void setINVALID_REASON(String iNVALID_REASON) {
	INVALID_REASON = iNVALID_REASON;
}
public String getCONCEPT_CODE() {
	return CONCEPT_CODE;
}
public void setCONCEPT_CODE(String cONCEPT_CODE) {
	CONCEPT_CODE = cONCEPT_CODE;
}
public String getDOMAIN_ID() {
	return DOMAIN_ID;
}
public void setDOMAIN_ID(String dOMAIN_ID) {
	DOMAIN_ID = dOMAIN_ID;
}
public String getVOCABULARY_ID() {
	return VOCABULARY_ID;
}
public void setVOCABULARY_ID(String vOCABULARY_ID) {
	VOCABULARY_ID = vOCABULARY_ID;
}
public String getCONCEPT_CLASS_ID() {
	return CONCEPT_CLASS_ID;
}
public void setCONCEPT_CLASS_ID(String cONCEPT_CLASS_ID) {
	CONCEPT_CLASS_ID = cONCEPT_CLASS_ID;
}
public String getSTANDARD_CONCEPT_CAPTION() {
	return STANDARD_CONCEPT_CAPTION;
}
public void setSTANDARD_CONCEPT_CAPTION(String sTANDARD_CONCEPT_CAPTION) {
	STANDARD_CONCEPT_CAPTION = sTANDARD_CONCEPT_CAPTION;
}
public String getINVALID_REASON_CAPTION() {
	return INVALID_REASON_CAPTION;
}
public void setINVALID_REASON_CAPTION(String iNVALID_REASON_CAPTION) {
	INVALID_REASON_CAPTION = iNVALID_REASON_CAPTION;
}

public void print(){
	System.out.println("CONCEPT_ID="+CONCEPT_ID );
	System.out.println("CONCEPT_NAME="+CONCEPT_NAME );
	System.out.println("STANDARD_CONCEPT="+STANDARD_CONCEPT );
	System.out.println("INVALID_REASON="+INVALID_REASON );
	System.out.println("CONCEPT_CODE="+CONCEPT_CODE);
	System.out.println("DOMAIN_ID="+DOMAIN_ID );
	System.out.println("VOCABULARY_ID="+VOCABULARY_ID);
	System.out.println("CONCEPT_CLASS_ID="+CONCEPT_CLASS_ID );
	System.out.println("STANDARD_CONCEPT_CAPTION="+STANDARD_CONCEPT_CAPTION );
	System.out.println("INVALID_REASON_CAPTION="+INVALID_REASON_CAPTION );
}
}
