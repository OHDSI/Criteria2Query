package edu.columbia.dbmi.ohdsims.tool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;

import edu.columbia.dbmi.ohdsims.pojo.Concept;
import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.util.ATLASUtil;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import edu.columbia.dbmi.ohdsims.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class ConceptMapping {

	private final static String conceptseturl = GlobalSetting.ohdsi_api_base_url+"conceptset/";
	private final static String usagi = GlobalSetting.concepthub;//+"/omop/searchOneEntityByTermAndDomain";
	private final static String umlsurl = GlobalSetting.concepthub+"/umls/searchUMLS";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConceptMapping cm = new ConceptMapping();
		//cm.mapConceptSetByEntity("type 2 diabetes");
		cm.getConceptListByUsagi("Body Mass Index", "Observation");
		//LinkedHashMap<ConceptSet, Integer> hcs = cm.mapConceptSetByEntity("female");
		//for (Map.Entry<ConceptSet, Integer> entry : hcs.entrySet()) {
		//	String url=conceptseturl+entry.getKey().getId();
		//	System.out.println(url);
		//	HttpUtil.doDelete(url, "");
		//}
		//System.out.println("size="+hcs.size());
		//String s = "";
		//String results=HttpUtil.doPost(GlobalSetting.ohdsi_api_base_url+"cohortdefinition/sql", s);
		//System.out.println(results);

	}

	public JSONArray mapConceptSet(List<Sentence> plist) {
		JSONArray conceptsetarr = new JSONArray();
		for (Sentence pgph : plist) {
			List<Term> terms = pgph.getTerms();
			for (Term t : terms) {
				mapConceptSetByEntity(t.getText());
			}
		}
		return conceptsetarr;
	}
	
	public Map<String, Object> map2ConceptSet(List<Term> tlist,boolean ignore) {
		Map<String, Object> map = new HashMap<String, Object>();
		Set<String> filterset=new HashSet<String>();
		int index = 0;
		List<ConceptSet> cpslist=getallConceptSet();
		try {
			for (int j = 0; j < tlist.size(); j++) {
				if(filterset.contains(tlist.get(j).getText())){
					
				}else{
					System.out.println("1");
					String conceptsetname = tlist.get(j).getText();
					String domain = tlist.get(j).getCategorey();
					if(ignore==false){
						createConceptSetByUsagi(cpslist, conceptsetname,domain);
					}
					filterset.add(conceptsetname);
					System.out.println("2");
					LinkedHashMap<ConceptSet, Integer> hcs = mapConceptSetByEntityFromAllConceptSets(cpslist,conceptsetname);
					System.out.println("3");
					List<ConceptSet> lscst = new ArrayList<ConceptSet>();
					for (Map.Entry<ConceptSet, Integer> entry : hcs.entrySet()) {
						lscst.add(entry.getKey());
					}
					map.put("conceptset" + index, lscst);
					map.put("cstname" + index, conceptsetname);
					map.put("domain" + index, domain);
					index++;
				}
			}
		} catch (Exception ex) {
			System.out.println("--ConceptSet ERROR-->" + ex.getMessage());
			return null;
		}
		return map;
	}

	//Get the ID of the concept set whose name is exactly the same as the entity name. If there is no matched concept in the cslist,
	// it constructs a new concept set.
	public Integer createConceptSetByUsagi(List<ConceptSet> cslist,String term,String domain){
		//term: text of the term; domain: category of the term
		Integer conceptId=0;
		//the most related one
		try{
			int conceptsetId=reuseConceptSetByStringMatching(cslist,"[C2Q]"+term);//Get the ID of the concept whose name is exactly the same as the entity name.
			System.out.println(conceptsetId);
			if(conceptsetId==0){//If the entity doesn't match any one of the concept in the list, construct a new concept and return the new concept ID.
				System.out.println("NEW CREATED->");
				String[] res=getConceptListByUsagi(term,domain);//Get the concept(conceptID and other information, matchscore) mapped by Usagi. The length of the String array is 2.
				long t1=System.currentTimeMillis();
				JSONObject jo=new JSONObject();
				SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
				jo.accumulate("name", "[C2Q]"+term);
				//jo.accumulate("name", "[C2Q]"+term+"_[cs_"+res[1]+"]");
				jo.accumulate("id", 23333);
				jo.accumulate("createdBy", GlobalSetting.c2qversion);
				String result=HttpUtil.doPost(conceptseturl, jo.toString());//e.g. {"createdBy":"","modifiedBy":"","createdDate":1592705482029,"modifiedDate":1592705482029,"id":1864340,"name":"[C2Q]term"}
				System.out.println("jo->"+jo.toString());
				System.out.println("conceptset->"+result);
				JSONObject rejo=JSONObject.fromObject(result);
				HttpUtil.doPut(conceptseturl+rejo.getString("id")+"/items",res[0]);
						System.out.println("-return id-->"+rejo.getString("id"));
				return Integer.valueOf(rejo.getString("id"));
			}else{
				System.out.println("REUSE:"+conceptsetId);
				return conceptsetId;
			}
		}catch (Exception ex){
			return 0;
		}
		
		
	}

	//Get the ID of the concept set whose name is exactly the same as the entity name.
	public Integer reuseConceptSetByStringMatching(List<ConceptSet> cslist,String str){
		ConceptMapping cm = new ConceptMapping();
		LinkedHashMap<ConceptSet, Integer> hcs = cm.strictlyMapConceptSetByEntityFromAllConceptSets(cslist, str);
		//Get the concept sets in the cslist whose name is exactly the same as the entity name.
		//Actually there is only one element in the hcs.
		for (Map.Entry<ConceptSet, Integer> entry : hcs.entrySet()) {
			/*
			String url=conceptseturl+entry.getKey().getId();
			System.out.println(url);
			HttpUtil.doDelete(url, "");
			*/
			return entry.getKey().getId();
		}
		return 0;
		
	}
	
	
	public void deleteConceptSetBySubString(List<ConceptSet> cslist, String str){
		int count=0;
		ConceptMapping cm = new ConceptMapping();
		LinkedHashMap<ConceptSet, Integer> hcs = cm.mapConceptSetByEntityFromAllConceptSets(cslist,str);
		for (Map.Entry<ConceptSet, Integer> entry : hcs.entrySet()) {
			String url=conceptseturl+entry.getKey().getId();
			System.out.println(url);
			HttpUtil.doDelete(url, "");
			count++;
		}
		System.out.println("count="+count);
	}
	
	public String extendByUMLS(String term){
		Integer conceptId=0;
		JSONObject jo=new JSONObject();
		jo.accumulate("term", term);
		String result=HttpUtil.doPost(umlsurl, jo.toString());
		JSONObject rejo=JSONObject.fromObject(result);
		if(rejo!=null){
			String aftermap=rejo.getString("term");
		if(aftermap.equals("unmapped")==false){
			return aftermap;
		}else{
			return term;
		}
		}else{
			return term;
		}
		
	}

	//Query entity name and its domain to map the term to OMOP standard concepts, and keep only the highest scored concept mapped by Usagi.
	//It returns a String array, where the first element is information about the concept ID, a JSONArray in String format
	// ("[{"conceptId":123,"isExcluded":0,"includeDescendants":1,"includeMapped":0}]"), the second element is the match score.
	public String[] getConceptListByUsagi(String term,String domain){
		//term: the name of the entity; domain: the category of the entity
		String[] res=new String[2];
		JSONObject jo=new JSONObject();
		jo.accumulate("term", term);
		jo.accumulate("domain", domain);
		String result=HttpUtil.doPost(usagi, jo.toString());//concepthub+"/omop/searchOneEntityByTermAndDomain"
		System.out.println("result="+result);
		JSONObject bestconcept=JSONObject.fromObject(result);
		try{
		System.out.println("matchScore="+bestconcept.getDouble("matchScore"));
		String matchs=String.format("%.2f", bestconcept.getDouble("matchScore")*100);
		
		JSONObject concept_jo=bestconcept.getJSONObject("concept");
		Integer cId=concept_jo.getInt("conceptId");
		System.out.println("cid="+cId);
		JSONObject conceptunit=formatOneitem(cId);//Construct a JSONObject with conceptID, and set isExcluded = 0, includedDescendants = 1, includeMapped = 0
		JSONArray conceptSet=new JSONArray();
		conceptSet.add(conceptunit);
		res[0]=conceptSet.toString();//[{"conceptId":conceptunit,"isExcluded":0,"includeDescendants":1,"includeMapped":0}]
		System.out.println("conceptset_json"+res[0]);
		res[1]=matchs;
		}catch(Exception ex){
			JSONArray conceptSet=new JSONArray();
			res[0]=conceptSet.toString();
			res[1]="0";
		}
		return res;
	}
	
	public String[] getCloestConceptByUsagi(String term,String domain){
		System.out.println("!!");
		String[] res=new String[3];
		JSONObject jo=new JSONObject();
		jo.accumulate("term", StringUtil.cleanASCII(term));
		jo.accumulate("domain", domain);
		String result=HttpUtil.doPost(usagi, jo.toString());
		System.out.println(jo.toString());
		System.out.println("result="+result);
		JSONObject bestconcept=JSONObject.fromObject(result);
		//System.out.println("matchScore="+bestconcept.getDouble("matchScore"));
		String matchs=String.format("%.2f", bestconcept.getDouble("matchScore")*100);
		
		JSONObject concept_jo=bestconcept.getJSONObject("concept");
		Integer cId=concept_jo.getInt("conceptId");
		String conceptname=concept_jo.getString("conceptName");
		
		res[0]=String.valueOf(cId);
		res[1]=conceptname;
		res[2]=matchs;
		return res;
	}

	//Get the concept set whose name is exactly the same as the entity name.
	public LinkedHashMap<ConceptSet, Integer> strictlyMapConceptSetByEntityFromAllConceptSets(List<ConceptSet> cslist,String entityname) {
		HashMap<ConceptSet, Integer> candidatecs = new HashMap<ConceptSet, Integer>();
		int distance = 0;

		for (int k = 0; k < cslist.size(); k++) {
			if (cslist.get(k).getName().toLowerCase().equals(entityname.toLowerCase().trim())) {
				//If the name of the concepts is the same as the entity name(the entityname begins with [C2Q])

				// add your own recommendation here
				// Similarity between this word and
				distance = cslist.get(k).getName().length() - entityname.trim().length();
				candidatecs.put(cslist.get(k), distance);
			}
		}
		//Actually, there is only one element in the candidatecs, so there is no need to sort the elements in the candidatecs.
		LinkedHashMap<ConceptSet, Integer> lm = new LinkedHashMap<ConceptSet, Integer>();
		List<Map.Entry<ConceptSet, Integer>> conceptsets = new ArrayList<Map.Entry<ConceptSet, Integer>>(
				candidatecs.entrySet());
		//System.out.println("ConceptSet "+conceptsets.get(0).getValue()+ "_integer "+ conceptsets.get(0).getKey());
		Collections.sort(conceptsets, new Comparator<Map.Entry<ConceptSet, Integer>>() {
			public int compare(Map.Entry<ConceptSet, Integer> o1, Map.Entry<ConceptSet, Integer> o2) {
				return (o1.getValue()).toString().compareTo(o2.getValue().toString());//compare the length difference between each matched concept and the entity
			}
		});
		for (int i = conceptsets.size() - 1; i >= 0; i--) {
			lm.put(conceptsets.get(i).getKey(), conceptsets.get(i).getValue());
		}
		return lm;

	}
	
	
	public LinkedHashMap<ConceptSet, Integer> mapConceptSetByEntityFromAllConceptSets(List<ConceptSet> cslist,String entityname) {
		HashMap<ConceptSet, Integer> candidatecs = new HashMap<ConceptSet, Integer>();
		int distance = 0;
		for (int k = 0; k < cslist.size(); k++) {
			if (cslist.get(k).getName().toLowerCase().contains(entityname.toLowerCase().trim())) {
				// add your own recommendation here
				// Similarity between this word and
				distance = cslist.get(k).getName().length() - entityname.trim().length();
				candidatecs.put(cslist.get(k), distance);
				
			}
		}
		LinkedHashMap<ConceptSet, Integer> lm = new LinkedHashMap<ConceptSet, Integer>();
		List<Map.Entry<ConceptSet, Integer>> conceptsets = new ArrayList<Map.Entry<ConceptSet, Integer>>(
				candidatecs.entrySet());
		
		Collections.sort(conceptsets, new Comparator<Map.Entry<ConceptSet, Integer>>() {
			public int compare(Map.Entry<ConceptSet, Integer> o1, Map.Entry<ConceptSet, Integer> o2) {
				return (o1.getValue()).toString().compareTo(o2.getValue().toString());
			}
		});
		for (int i = conceptsets.size() - 1; i >= 0; i--) {
			lm.put(conceptsets.get(i).getKey(), conceptsets.get(i).getValue());
		}
		return lm;

	}
	
	/**
	 * Map one term to a set of Concept Set
	 * 
	 **/
	public LinkedHashMap<ConceptSet, Integer> mapConceptSetByEntity(String entityname) {
		HashMap<ConceptSet, Integer> candidatecs = new HashMap<ConceptSet, Integer>();
		int distance = 0;
		List<ConceptSet> cslist=getallConceptSet();
		for (int k = 0; k < cslist.size(); k++) {
			if (cslist.get(k).getName().toLowerCase().contains(entityname.toLowerCase().trim())) {
				// add your own recommendation here
				// Similarity between this word and
				distance = cslist.get(k).getName().length() - entityname.trim().length();
				candidatecs.put(cslist.get(k), distance);
				
			}
		}
		LinkedHashMap<ConceptSet, Integer> lm = new LinkedHashMap<ConceptSet, Integer>();
		List<Map.Entry<ConceptSet, Integer>> conceptsets = new ArrayList<Map.Entry<ConceptSet, Integer>>(
				candidatecs.entrySet());
		
		Collections.sort(conceptsets, new Comparator<Map.Entry<ConceptSet, Integer>>() {
			public int compare(Map.Entry<ConceptSet, Integer> o1, Map.Entry<ConceptSet, Integer> o2) {
				return (o1.getValue()).toString().compareTo(o2.getValue().toString());
			}
		});
		for (int i = conceptsets.size() - 1; i >= 0; i--) {
			lm.put(conceptsets.get(i).getKey(), conceptsets.get(i).getValue());
		}
		return lm;

	}

	//Get all Concept sets from http://api.ohdsi.org/WebAPI/conceptset/
	public static List<ConceptSet> getallConceptSet() {
		String strResult = HttpUtil.doGet(conceptseturl);
		JSONArray array = JSONArray.fromObject(strResult);//Creates a JSONArray.
		List<ConceptSet> list = JSONArray.toList(array, ConceptSet.class);
		return list;
	}
	
	
	public Integer createConceptByConceptName(String word,String domain) throws UnsupportedEncodingException, ClientProtocolException, IOException{
		Integer conceptId=0;
		//the most related one
		System.out.println("word=" + word);
		System.out.println("domain=" + domain);
		
		List<Concept> econceptlist = ATLASUtil.searchConceptByNameAndDomain(word, domain);
		String expression=generateConceptSetByConcepts(econceptlist);
		long t1=System.currentTimeMillis();  
		JSONObject jo=new JSONObject();
		jo.accumulate("name", word+"_created_by_"+GlobalSetting.c2qversion);
		jo.accumulate("id", 23333);
		String result=HttpUtil.doPost(conceptseturl, jo.toString());
		JSONObject rejo=JSONObject.fromObject(result);
		HttpUtil.doPut(conceptseturl+rejo.getString("id")+"/items",expression);		
		return Integer.valueOf(rejo.getString("id"));
	}
	
	public Integer createEmptyConceptByConceptName(String word,String domain) throws UnsupportedEncodingException, ClientProtocolException, IOException{
		Integer conceptId=0;
		//the most related one
		System.out.println("word=" + word);
		System.out.println("domain=" + domain);
		List<Concept> econceptlist = ATLASUtil.searchConceptByNameAndDomain(word, domain);
		JSONObject jo=new JSONObject();
		jo.accumulate("name", word+"_decide_later_"+GlobalSetting.c2qversion);
		jo.accumulate("id", 23333);
		String result=HttpUtil.doPost(conceptseturl, jo.toString());
		JSONObject rejo=JSONObject.fromObject(result);	
		return Integer.valueOf(rejo.getString("id"));
	}
	
	public static String generateConceptSetByConcepts(List<Concept> concepts){
		JSONArray conceptSet=new JSONArray();
		for(Concept c:concepts){
			JSONObject jo=formatOneitem(c.getCONCEPT_ID());
			conceptSet.add(jo);		
		}
		//System.out.println("conceptSet="+conceptSet.toString());	
		return conceptSet.toString();
	}
	
	
	//Construct a JSONObject with conceptID, and set isExcluded = 0, includedDescendants = 1, includeMapped = 0
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

	//Map the term with its domain to a concept with a highest matching score.
	public String[] getConceptByUsagi(String term,String domain){
		String[] res=new String[3];
		JSONObject jo=new JSONObject();
		jo.accumulate("term", StringUtil.cleanASCII(term));
		jo.accumulate("domain", domain);
		String result=HttpUtil.doPost(usagi, jo.toString());
		JSONObject bestconcept=JSONObject.fromObject(result);
		JSONObject concept_jo=bestconcept.getJSONObject("concept");

		res[0] = String.valueOf(concept_jo.getInt("conceptId"));
		res[1] = concept_jo.getString("conceptName");
		res[2] = concept_jo.getString("standardConcept");

		return res;
	}

}
