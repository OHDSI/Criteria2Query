package edu.columbia.dbmi.ohdsims.pojo;

import java.util.HashMap;
import java.util.Map;

public class GlobalSetting {
    public final static String c2qversion = "criteria2query v0.8.6.0";
    public final static String ohdsi_api_base_url = "http://api.ohdsi.org/WebAPI/";//http://api.ohdsi.org/WebAPI/ http://atlas-tutorial.ohdsi.org/WebAPI/
    public final static String crf_model = "edu/columbia/dbmi/ohdsims/model/c2q_all_model_advanced.ser.gz";//all-c2q-model. //ec-ner-model.ser.gz
    public final static String relexmodel = "edu/columbia/dbmi/ohdsims/model/re.model";//all-c2q-model. //ec-ner-model.ser.gz
    public final static String relExmodel = "edu/columbia/dbmi/ohdsims/model/RelEx.model";
    public final static String negatemodel = "edu/columbia/dbmi/ohdsims/model/negex_triggers.txt";//all-c2q-model. //ec-ner-model.ser.gz
    public final static String instancefile = "edu/columbia/dbmi/ohdsims/model/100trialsrels4weka.arff";//all-c2q-model. //ec-ner-model.ser.gz
    //public final static String dependence_model="edu/columbia/dbmi/ohdsims/model/wsjPCFG.ser.gz";
    public final static String rule_base_model = "edu/columbia/dbmi/ohdsims/model/rule_based_model.ser.gz";
    public final static String dependence_model = "edu/columbia/dbmi/ohdsims/model/wsjPCFG.ser.gz";//edu/columbia/dbmi/ohdsims/model/wsjPCFG.ser.gz
    public final static String opennlp_model_dir = "";
    public final static String[] alldomains = {"Condition", "Observation", "Drug", "Measurement", "Demographic", "Temporal", "Value", "Negation_cue", "Procedure", "Device"};
    public final static String[] conceptSetDomains = {"Condition", "Observation", "Measurement", "Drug", "Procedure", "Device"};
    public final static String[] primaryEntities = {"Condition", "Observation", "Measurement", "Drug", "Procedure", "Demographic", "Device"};
    public final static String[] atrributes = {"Temporal_measurement", "Temporal", "Value"};
    public final static String[] combo = {"Measurement_Value", "Drug_Temporal", "Demographic_Value", "Observation_Value",
            "Condition_Value", "Condition_Temporal", "Procedure_Temporal", "Drug_Value", "Procedure_Value", "Observation_Temporal",
            "Measurement_Temporal", "Demographic_Temporal", "Device_Temporal", "Device_Value"};
    public final static String[] relations = {"no_relation", "has_value", "has_temporal"};
    public final static String negateTag = "Negation_cue";
    public final static Map<String, String> domainAbbrMap = new HashMap<String, String>() {
        {
            put("Condition", "Con");
            put("Drug", "Dru");
            put("Observation", "Obs");
            put("Measurement", "Mea");
            put("Procedure", "Pro");
            put("Device", "Dev");
            put("Temporal", "Tem");
            put("Value", "Val");
            put("Negation_cue", "Neg");
            put("Demographic", "Dem");
        }
    };
    public final static String valueTransDict = "edu/columbia/dbmi/ohdsims/model/value_comparison_phrase_normalization_dict.txt";
    public final static String temporalTransDict = "edu/columbia/dbmi/ohdsims/model/temporal_comparison_phrase_normalization_dict.txt";

    public final static String concepthub = "http://35.202.46.162:8080/concepthub";


    //Connect to the databases
    public final static String databaseURL1K = "jdbc:postgresql://localhost/synpuf1k";
    public final static String databaseURL5pct = "jdbc:postgresql://localhost/synpuf5pct";
    public final static String databaseUser = "Please connect to a database.";
    public final static String databasePassword = "*****";

    //Change the directories
    public final static String negateDetectionFolder = "/opt/tomcat/NegationDetection";
    public final static String virtualEnvFolder = "/opt/tomcat/python_virtualenvs/C2Q_NEGATION/bin"; //"D:\\C2Q\\python_virtualenvs\\C2Q_NEGATION\\Scripts";

}
