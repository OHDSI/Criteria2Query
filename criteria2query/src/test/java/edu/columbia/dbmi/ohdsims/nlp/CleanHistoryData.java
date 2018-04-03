package edu.columbia.dbmi.ohdsims.nlp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.columbia.dbmi.ohdsims.pojo.Cohort;
import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.tool.ConceptMapping;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import net.sf.json.JSONArray;

public class CleanHistoryData {
	public final static String cohorturl=GlobalSetting.ohdsi_api_base_url+"cohortdefinition/";
	private final static String conceptseturl = GlobalSetting.ohdsi_api_base_url+"conceptset/";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CleanHistoryData chd=new CleanHistoryData();
		chd.deleteCohortBySubString("criteria2query");
		chd.deleteConceptSetBySubString("criteria2query");
	}
	
	public void deleteConceptSetBySubString(String str){
		int count=0;
		ConceptMapping cm = new ConceptMapping();
		LinkedHashMap<ConceptSet, Integer> hcs = cm.mapConceptSetByEntity(str);
		for (Map.Entry<ConceptSet, Integer> entry : hcs.entrySet()) {
			String url=conceptseturl+entry.getKey().getId();
			System.out.println(url);
			HttpUtil.doDelete(url, "");
			count++;
		}
		System.out.println("count="+count);
	}
	
	public void deleteCohortBySubString(String str){
		deleteCohortByCohortIds(getCohortIds(getallCohort(),str));
	}
	
	public void deleteCohortByCohortIds(List<Integer> ids){
		int count=0;
		for(Integer i:ids){
			String url=cohorturl+i;
			System.out.println(url);
			HttpUtil.doDelete(url, "");
			count++;
		}
		System.out.println("count="+count);
	}
	
	public List<Integer> getCohortIds(List<Cohort> cohorts,String str){
		List<Integer> ids=new ArrayList<Integer>();
		for(Cohort c:cohorts){
			if(c.getName().contains(str)){
				ids.add(c.getId());
			}
		}
		return ids;
	}
	
	public List<Cohort> getallCohort() {
		String strResult = HttpUtil.doGet(cohorturl);
		JSONArray array = JSONArray.fromObject(strResult);
		List<Cohort> list = JSONArray.toList(array, Cohort.class);
		return list;
	}
}
