package edu.columbia.dbmi.ohdsims.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;

import edu.columbia.dbmi.ohdsims.pojo.Concept;
import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import edu.columbia.dbmi.ohdsims.pojo.Cdmentity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONUtil {
	public static void main(String[] args) {
//		JSONArray criterialist = new JSONArray();
//
//		JSONObject criteriaunit = new JSONObject();
//		JSONObject occurrence = new JSONObject();
//		JSONObject startwindow = new JSONObject();
//		JSONObject criteria = new JSONObject();
//		criteria = setCriteria("Condition", 2);
//		boolean inc = true;
//		boolean exc = false;
//		if (inc == true) {
//			occurrence = setOccurrence(2, 1);
//			startwindow = setTemporalWindow(1);
//		}
//		if (exc == true) {
//			occurrence = setOccurrence(0, 0);
//		}
//		criteriaunit.accumulate("Criteria", criteria);
//		criteriaunit.accumulate("StartWindow", startwindow);
//		criteriaunit.accumulate("Occurrence", occurrence);
//
//		System.out.println(criteriaunit);
		System.out.println(anyConditionforInitialEvent());
		
	}
	public static JSONObject anyConditionforInitialEvent(){
		JSONObject anycondition=new JSONObject();
		JSONArray criteriaList=new JSONArray();
		JSONObject conditionOccurrence=new JSONObject();
		JSONObject jnull=new JSONObject();
		conditionOccurrence.accumulate("ConditionOccurrence", jnull);
		criteriaList.add(conditionOccurrence);
		anycondition.accumulate("CriteriaList", criteriaList);
		JSONObject observationWindow=new JSONObject();
		observationWindow.accumulate("PriorDays", 0);
		observationWindow.accumulate("PostDays", 0);
		anycondition.accumulate("ObservationWindow", observationWindow);
		JSONObject primaryCriteriaLimit=new JSONObject();
		primaryCriteriaLimit.accumulate("Type", "First");
		anycondition.accumulate("PrimaryCriteriaLimit", primaryCriteriaLimit);
		return anycondition;
	}
	

	public static JSONObject setOccurrence(int type, int count) {
		JSONObject jsonob = new JSONObject();
		jsonob.accumulate("Type", type);
		jsonob.accumulate("Count", count);
		return jsonob;
	}

	/**
	 * index 1: has history of 
	 * index 2: has 
	 * */
	public static JSONObject setTemporalWindow(int index) {
		JSONObject window = new JSONObject();
		JSONObject start = new JSONObject();
		JSONObject end = new JSONObject();
		if(index ==1){
			start.accumulate("Coeff", -1);
			// start.accumulate("Count", 0);
			end.accumulate("Coeff", "1");
			// end.accumulate("Count", 0);
			//end.accumulate("Days", "0");	
		}
		window.accumulate("Start", start);
		window.accumulate("End", end);
		return window;
	}

	public static JSONObject setCriteria(String type, int conceptsetid) {
		JSONObject criteriatype = new JSONObject();
		JSONObject codesetId = new JSONObject();
		codesetId.accumulate("CodesetId", conceptsetid);
		if (type.equals("Condition")) {
			criteriatype.accumulate("ConditionOccurrence", codesetId);
		} else if (type.equals("Drug")) {
			criteriatype.accumulate("DrugExposure", codesetId);
		} else if (type.equals("Observation")) {
			criteriatype.accumulate("Observation", codesetId);
		} else if (type.equals("Procedure_Device")) {
			criteriatype.accumulate("DeviceExposure", codesetId);
		}
		return criteriatype;
	}

	
	/**
	 * inclusion :flag = true ; exclusion : flag=false;
	 * neg
	 * temporal 1 has history of 
	 * 
	 * */
	
	public static JSONObject setCriteriaUnit(int index,boolean neg,boolean flag,String type,int temporal){
		JSONObject criteriaunit = new JSONObject();
		JSONObject occurrence = new JSONObject();
		JSONObject startwindow = new JSONObject();
		JSONObject criteria = new JSONObject();
		criteria = setCriteria(type, index);
		if (neg^flag == true) {
			occurrence = setOccurrence(2, 1);	
		}else {
			occurrence = setOccurrence(0, 0);
		}
		startwindow = setTemporalWindow(temporal);
		criteriaunit.accumulate("Criteria", criteria);
		criteriaunit.accumulate("StartWindow", startwindow);
		criteriaunit.accumulate("Occurrence", occurrence);
		return criteriaunit;
	}
	
	
}
