package edu.columbia.dbmi.ohdsims.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.columbia.dbmi.ohdsims.pojo.CdmCohort;
import edu.columbia.dbmi.ohdsims.pojo.CdmCriteria;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.service.IConceptMappingService;
import edu.columbia.dbmi.ohdsims.service.IInformationExtractionService;
import edu.columbia.dbmi.ohdsims.service.IQueryFormulateService;
import edu.columbia.dbmi.ohdsims.tool.ConceptMapping;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/queryformulate")

public class QueryFormulationController {
	private Logger logger = LogManager.getLogger(QueryFormulationController.class);
	@Resource
	private IQueryFormulateService qfService;
	@Resource
	private IInformationExtractionService ieService;

	@RequestMapping("/formulateCohort")
	@ResponseBody
	public Map<String, Object> formulateCohort(HttpSession httpSession, HttpServletRequest request, String conceptsets){
		String remoteAddr = "";
	    if (request != null) {
	            remoteAddr = request.getHeader("X-FORWARDED-FOR");
	            if (remoteAddr == null || "".equals(remoteAddr)) {
	                remoteAddr = request.getRemoteAddr();
	            }
	    }
	    logger.info("[IP:"+remoteAddr+"][Start formulateCohort]");
		Document doc = (Document) httpSession.getAttribute("allcriteria");
		JSONObject cohortjson=this.qfService.formualteCohortQuery(doc);
		System.out.println(cohortjson);
		httpSession.setAttribute("jsonResult",cohortjson);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("jsonResult", cohortjson.toString());
		logger.info("[IP:"+remoteAddr+"][End formulateCohort]");
		return map;
	}
	
	@RequestMapping("/storeInATLAS")
	@ResponseBody
	public Map<String, Object> storeCohortInATLAS(HttpSession httpSession, HttpServletRequest request, String conceptsets){
		String remoteAddr = "";
	    if (request != null) {
	            remoteAddr = request.getHeader("X-FORWARDED-FOR");
	            if (remoteAddr == null || "".equals(remoteAddr)) {
	                remoteAddr = request.getRemoteAddr();
	            }
	    }
	    logger.info("[IP:"+remoteAddr+"][Start connecting to ATLAS]");
		Document doc = (Document) httpSession.getAttribute("allcriteria");
		Map<String,Object> map=new HashMap<String,Object>();
		JSONObject expression=(JSONObject) httpSession.getAttribute("jsonResult");
		List<String> initial_events=ieService.getAllInitialEvents(doc);
		StringBuffer sb=new StringBuffer();
		for(String e:initial_events){
			sb.append(e+" ");
		}
		
		Integer cohortId=this.qfService.storeInATLAS(expression,"[C2Q]"+sb.toString());
		map.put("id", cohortId);
		logger.info("[IP:"+remoteAddr+"][Finish connecting to ATLAS]");
		return map;
	}

	@RequestMapping("/getSQL")
	@ResponseBody
	public Map<String, Object> prepareSQL(HttpSession httpSession, String conceptsets) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String cohortId=(String) httpSession.getAttribute("cohortId");
//		String dbtype = (String) httpSession.getAttribute("dbtype");
		JSONObject expressionstr = (JSONObject) httpSession.getAttribute("jsonResult");
		JSONObject expression=new JSONObject();
		expression.accumulate("expression", expressionstr);
		//System.out.println("expressionstr="+expressionstr);
		//generate SQL template
		String results=HttpUtil.doPost(GlobalSetting.ohdsi_api_base_url+"cohortdefinition/sql", expression.toString());
		JSONObject resultjson=JSONObject.fromObject(results);
		System.out.println("SQL template="+resultjson.get("templateSql"));
		//SQL template -> different SQLs
		JSONObject sqljson=new JSONObject();
		sqljson.accumulate("SQL", resultjson.get("templateSql"));
//		if(dbtype.equals("PostgreSQL")){
//			sqljson.accumulate("targetdialect", "postgresql");
//		}else if(dbtype.equals("MSSQL")){
//			sqljson.accumulate("targetdialect", "sql server");
//		}else if(dbtype.equals("Oracle")){
//			sqljson.accumulate("targetdialect", "oracle");
//		}
		sqljson.accumulate("targetdialect", "postgresql");
		results=HttpUtil.doPost(GlobalSetting.ohdsi_api_base_url+"sqlrender/translate", sqljson.toString());
		resultjson=JSONObject.fromObject(results);
		String sqlresult=(String) resultjson.get("targetSQL");	
		map.put("sqlResult", sqlresult);
		return map;
	}
	
	@RequestMapping("/getJSON")
	@ResponseBody
	public Map<String, Object> prepareJSON(HttpSession httpSession, String conceptsets) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonstr=(JSONObject) httpSession.getAttribute("jsonResult");
		map.put("jsonResult", jsonstr.toString());
		return map;
	}
	
	@RequestMapping("/setDBtype")
	@ResponseBody
	public Map<String, Object> setDBtype(HttpSession httpSession, String dbtype) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		httpSession.setAttribute("dbtype",dbtype);
		return map;
	}
	
	@RequestMapping("/getCohortId")
	@ResponseBody
	public Map<String, Object> getCohortId(HttpSession httpSession) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String id=(String) httpSession.getAttribute("cohortId");
		map.put("id", id);
		return map;
	}
	
}
