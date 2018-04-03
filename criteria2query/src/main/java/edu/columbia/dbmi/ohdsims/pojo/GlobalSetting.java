package edu.columbia.dbmi.ohdsims.pojo;

public class GlobalSetting {
	public final static String c2qversion="criteria2query v0.8.2.1";
	public final static String ohdsi_api_base_url="http://api.ohdsi.org/WebAPI/";//http://api.ohdsi.org/WebAPI/
	public final static String crf_model="edu/columbia/dbmi/ohdsims/model/c2q_all_model_advanced.ser.gz";//all-c2q-model. //ec-ner-model.ser.gz
	public final static String relexmodel="edu/columbia/dbmi/ohdsims/model/re.model";//all-c2q-model. //ec-ner-model.ser.gz
	public final static String relExmodel="edu/columbia/dbmi/ohdsims/model/RelEx.model";
	public final static String negatemodel="edu/columbia/dbmi/ohdsims/model/negex_triggers.txt";//all-c2q-model. //ec-ner-model.ser.gz
	public final static String instancefile="edu/columbia/dbmi/ohdsims/model/100trialsrels4weka.arff";//all-c2q-model. //ec-ner-model.ser.gz
	//public final static String dependence_model="edu/columbia/dbmi/ohdsims/model/wsjPCFG.ser.gz";
	public final static String dependence_model="edu/columbia/dbmi/ohdsims/model/wsjPCFG.ser.gz";//edu/columbia/dbmi/ohdsims/model/wsjPCFG.ser.gz
	public final static String opennlp_model_dir="";
	public final static String[] alldomains={"Condition","Observation","Drug","Measurement","Demographic","Temporal","Value","Negation_cue","Procedure","Device"};
	public final static String[] conceptSetDomains={"Condition","Observation","Measurement","Drug","Procedure"};
	public final static String[] primaryEntities={"Condition","Observation","Measurement","Drug","Procedure","Demographic"};
	public final static String[] atrributes={"Temporal_measurement","Temporal","Value"};
	public final static String[] combo={"Measurement_Value","Drug_Temporal","Demographic_Value","Observation_Value","Condition_Value","Condition_Temporal","Procedure_Temporal","Drug_Value","Procedure_Value","Observation_Temporal","Measurement_Temporal","Demographic_Temporal"};
	public final static String[] relations={"no_relation","has_value","has_temporal"};
	public final static String negateTag="Negation_cue";
	public final static String concepthub="http://45.77.96.239:8080/concepthub";//http://45.77.96.239:8080/concepthub
}
