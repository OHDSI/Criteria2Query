package edu.columbia.dbmi.ohdsims.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.columbia.dbmi.ohdsims.pojo.Cdmentity;
import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.service.IConceptFilteringService;
import edu.columbia.dbmi.ohdsims.service.IConceptMappingService;
import edu.columbia.dbmi.ohdsims.service.IInformationExtractionService;
import edu.columbia.dbmi.ohdsims.tool.ConceptMapping;
import edu.columbia.dbmi.ohdsims.tool.NERTool;
import edu.columbia.dbmi.ohdsims.util.ATLASUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/map")
public class ConceptMappingController {
	private Logger logger = Logger.getLogger(ConceptMappingController.class);
	
	@Resource
	private IConceptMappingService conceptMappingService;

	@RequestMapping("/mapConceptSets")
	@ResponseBody
	public Map<String, Object> mapConceptSetsByDoc(HttpSession httpSession, HttpServletRequest request)
			throws Exception {
		String remoteAddr = "";
	    if (request != null) {
	            remoteAddr = request.getHeader("X-FORWARDED-FOR");
	            if (remoteAddr == null || "".equals(remoteAddr)) {
	                remoteAddr = request.getRemoteAddr();
	            }
	     }
		logger.info("[IP:"+remoteAddr+"][Jump to ConceptSet Mapping");
		Map<String, Object> map = new HashMap<String, Object>();
		Document doc = (Document) httpSession.getAttribute("allcriteria");
		List<Term> terms = this.conceptMappingService.getDistinctTerm(doc);
		logger.info("[IP:"+remoteAddr+"][Start Creating Concept Set");
		Map<String, Integer> conceptSetIds = this.conceptMappingService.createConceptsByTerms(terms);
		logger.info("[IP:"+remoteAddr+"][Finish Creating Concept Set");
		System.out.println("after create concepts by terms");
		logger.info("[IP:"+remoteAddr+"][Start Linking Concept Set");
		Document newdoc = this.conceptMappingService.linkConceptSetsToTerms(doc, conceptSetIds);
		logger.info("[IP:"+remoteAddr+"][Finish Linking Concept Set");
		System.out.println("set link concept sets");
		httpSession.setAttribute("allcriteria", newdoc);
		System.out.println("set http sessions");
		int index = 0; 
		System.out.println("term size="+terms.size());
		logger.info("[IP:"+remoteAddr+"][Start Concept Set Visualization");
		// query all concept set for display purpose (map and sort them by similarity (string distance) )
		for (int j = 0; j < terms.size(); j++) {
			String conceptsetname = terms.get(j).getText();
			String domain = terms.get(j).getCategorey();
			List<ConceptSet> lscst = this.conceptMappingService.mapAndSortConceptSetByEntityName(conceptsetname);
			map.put("conceptset" + index, lscst);
			map.put("cstname" + index, conceptsetname);
			map.put("csetid" + index, terms.get(j).getVocabularyId());
			map.put("domain" + index, domain);
			System.out.println("conceptset"+index+":"+lscst);
			System.out.println("cstname"+index+":"+conceptsetname);
			System.out.println("csetid"+index+":"+terms.get(j).getVocabularyId());
			System.out.println("domain"+index+":"+domain);
			index++;
		}
		logger.info("[IP:"+remoteAddr+"][Finish Concept Set Visualization");
		return map;
	}
	
	@RequestMapping("/syncConceptSets")
	@ResponseBody
	public Map<String, Object> syncConceptSets(HttpSession httpSession, HttpServletRequest request)
			throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Document doc = (Document) httpSession.getAttribute("allcriteria");
		List<Term> terms = this.conceptMappingService.getDistinctTerm(doc);
		int index = 0; 
		// query all concept set for display purpose (map and sort them by similarity (string distance) )
		for (int j = 0; j < terms.size(); j++) {
			String conceptsetname = terms.get(j).getText();
			String domain = terms.get(j).getCategorey();
			List<ConceptSet> lscst = this.conceptMappingService.mapAndSortConceptSetByEntityName(conceptsetname);
			map.put("conceptset" + index, lscst);
			map.put("cstname" + index, conceptsetname);
			map.put("domain" + index, domain);
			index++;
		}
		return map;
	}

	@RequestMapping("/ignoreTerm")
	@ResponseBody
	public Map<String, Object> ignoreTerm(HttpSession httpSession, String term) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Document doc = (Document) httpSession.getAttribute("allcriteria");
		System.out.println("ignore" + term);
		Document newdoc = this.conceptMappingService.ignoreTermByEntityText(doc, term);
		httpSession.setAttribute("allcriteria", newdoc);
		return map;
	}
	
	@RequestMapping("/linkConceptSet")
	@ResponseBody
	public Map<String, Object> translateToJSON(HttpSession httpSession, String conceptsets){
		JSONArray ja=JSONArray.fromObject(conceptsets);
		Map<String,Integer> conceptset_manual=new HashMap<String,Integer>();
		for(int i=0;i<ja.size();i++){
			JSONObject jo=(JSONObject) ja.get(i);
			System.out.println(jo.get("entity"));
			conceptset_manual.put((String) jo.get("entity"), (Integer)jo.get("conceptsetid"));
		}	
		Document doc = (Document) httpSession.getAttribute("allcriteria");
		doc = this.conceptMappingService.linkConceptSetsToTerms(doc, conceptset_manual);
		httpSession.setAttribute("allcriteria",doc);
		Map<String,Object> map=new HashMap<String,Object>();
		return map;
	}
}
