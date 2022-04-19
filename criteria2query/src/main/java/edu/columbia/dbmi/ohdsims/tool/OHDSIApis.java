package edu.columbia.dbmi.ohdsims.tool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import edu.columbia.dbmi.ohdsims.pojo.Concept;
import edu.columbia.dbmi.ohdsims.pojo.ConceptRecordCount;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

public class OHDSIApis {
	private final static String vocubularyurl = GlobalSetting.ohdsi_api_base_url+"vocabulary/1PCT/search";
	private final static String recordcounturl = GlobalSetting.ohdsi_api_base_url+ "cdmresults/1PCT/conceptRecordCount";
	private final static String conceptseturl = GlobalSetting.ohdsi_api_base_url+ "conceptset/";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//searchConcept("type 2 diabetes", "Condition");
			Integer i=createConceptSetByConceptName("yctestT2DM212","type 2 diabetes", "Condition");
			System.out.println("i="+i);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Concept> searchConceptByNameAndDomain(String entity, String domainid)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		JSONObject queryjson = new JSONObject();
		queryjson.accumulate("QUERY", entity);
		queryjson.accumulate("DOMAIN_ID", "['" + domainid + "']");
		queryjson.accumulate("STANDARD_CONCEPT", "S");// only standard concept
		System.out.println("queryjson:" + queryjson);
		String vocabularyresult = getConcept(queryjson);
		System.out.println("vocabularyresult  length=" + vocabularyresult.length());
		Gson gson = new Gson();
		JsonArray ja = new JsonParser().parse(vocabularyresult).getAsJsonArray();
		if (ja.size() == 0) {
			return null;
		}
		List<Concept> list = gson.fromJson(ja, new TypeToken<List<Concept>>() {
		}.getType());
		return list;
	}

	
	public static String getConcept(JSONObject concept)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		String result=HttpUtil.doPost(vocubularyurl, concept.toString());
		return result;
	}

	public static Map<Concept, ConceptRecordCount> searchConcept(String entity, String domainid)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		Map<Integer, ConceptRecordCount> concept_crc = new HashMap<Integer, ConceptRecordCount>();
		List<Concept> list = searchConceptByNameAndDomain(entity, domainid);
		Map<Concept, ConceptRecordCount> results = new HashMap<Concept, ConceptRecordCount>();
		// extract concept id
		List<Integer> conceptidlist = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			conceptidlist.add(list.get(i).getCONCEPT_ID());
		}
		JsonArray conceptarr = (JsonArray) new Gson().toJsonTree(conceptidlist, new TypeToken<List<Integer>>() {
		}.getType());		
		// get record count by concept id array
		if (conceptarr.size() > 0) {
			String co = getRecordCount(conceptarr);
			JSONArray conceptrc = JSONArray.fromObject(co);
			System.out.println("size=" + conceptrc.size());
			for (int a = 0; a < conceptrc.size(); a++) {
				JSONObject conceptunit = JSONObject.fromObject(conceptrc.get(a));
				Set<String> setIds = conceptunit.keySet();
				for(String i:setIds){
					Integer concept_int=Integer.valueOf(i);
					JSONArray rcdrc= JSONArray.fromObject(conceptunit.get(i));
					if(rcdrc.size()==1){
						ConceptRecordCount concept_record_count=new ConceptRecordCount(-1,-1);
						concept_crc.put(concept_int, concept_record_count);
					}else{
						ConceptRecordCount concept_record_count=new ConceptRecordCount(rcdrc.getInt(0),rcdrc.getInt(1));
						concept_crc.put(concept_int, concept_record_count);
					}
				}
				for(Concept cpt: list){
					results.put(cpt, concept_crc.get(cpt.getCONCEPT_ID()));
				}
			}			
			return results;
		} else {
			return null;
		}
	}

	public static String getRecordCount(JsonArray array)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		String strResult=HttpUtil.doPost(recordcounturl, array.toString());
		return strResult;
	}
	
	public static Integer createConceptSetByConceptName(String conceptsetname,String word,String domain) throws UnsupportedEncodingException, ClientProtocolException, IOException{			
		//the most related one		
		List<Concept> econceptlist = searchConceptByNameAndDomain(word, domain);				
		String expression=generateConceptSetByConcepts(econceptlist);
		JSONObject jo=new JSONObject();
		jo.accumulate("name", conceptsetname);
		jo.accumulate("id", 1);
		String result=HttpUtil.doPost(conceptseturl, jo.toString());
		JSONObject rejo=JSONObject.fromObject(result);
		HttpUtil.doPut(conceptseturl+rejo.getString("id")+"/items",expression);		
		return Integer.valueOf(rejo.getString("id"));
	}
	
	
	
	public static String generateConceptSetByConcepts(List<Concept> concepts){
		JSONArray conceptSet=new JSONArray();
		for(Concept c:concepts){
			JSONObject jo=formatOneitem(c.getCONCEPT_ID());
			conceptSet.add(jo);		
		}
		return conceptSet.toString();
	}
	
	
	
	public static JSONObject formatOneitem(Integer conceptId){
		JSONObject jo=new JSONObject();
		try {
			jo.put("conceptId", conceptId);
			jo.put("isExcluded", 0);
			jo.put("includeDescendants", 1);//
			jo.put("includeMapped", 0);//
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jo;
	}

	//Make two Get requests to the conceptseturl and save the responses in a JSONObject with keys createdBy, modifiedBy, createdDate, modifiedDate, id, name, expression.
	public static JSONObject querybyconceptSetid(int conceptid){
		//System.out.println("===>querybyconceptSetid");
		JSONObject jot=new JSONObject();
    	String re2=HttpUtil.doGet(conceptseturl+conceptid);
    	JSONObject jore2=JSONObject.fromObject(re2);
    	//System.out.println("jore2="+jore2);
    	jot.accumulateAll(jore2);
    	String re3=HttpUtil.doGet(conceptseturl+conceptid+"/expression");
    	
    	JSONObject expression=JSONObject.fromObject(re3);
    	jot.accumulate("expression",expression);
    	
    	return jot;
	}
}
