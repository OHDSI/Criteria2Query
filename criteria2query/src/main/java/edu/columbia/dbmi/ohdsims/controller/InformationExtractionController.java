package edu.columbia.dbmi.ohdsims.controller;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.columbia.dbmi.ohdsims.pojo.*;
import edu.columbia.dbmi.ohdsims.service.IConceptMappingService;
import net.sf.json.JSONArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import edu.columbia.dbmi.ohdsims.service.IConceptFilteringService;
import edu.columbia.dbmi.ohdsims.service.IInformationExtractionService;
import edu.columbia.dbmi.ohdsims.service.impl.InformationExtractionServiceImpl;
import edu.columbia.dbmi.ohdsims.tool.CoreNLP;
import edu.columbia.dbmi.ohdsims.tool.FeedBackTool;
import edu.columbia.dbmi.ohdsims.tool.NERTool;
import edu.columbia.dbmi.ohdsims.tool.NegReTool;
import edu.columbia.dbmi.ohdsims.util.IOUtil;
import edu.columbia.dbmi.ohdsims.util.StringUtil;
import edu.columbia.dbmi.ohdsims.util.TemporalNormalize;
import edu.columbia.dbmi.ohdsims.util.WebUtil;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;
import net.sf.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.HttpClients;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/ie")
public class InformationExtractionController {
	private Logger logger = LogManager.getLogger(InformationExtractionController.class);
	@Resource
	private IInformationExtractionService ieService;
	@Resource
	private IConceptFilteringService cfService;
	@Resource
	private IConceptMappingService conceptMappingService;

	@RequestMapping("/parse")
	@ResponseBody
	public Map<String, Object> parseAllCriteria(HttpSession httpSession, HttpServletRequest request,String initialevent, String inc, String exc,boolean abb, boolean recon,String obstart,String obend,String daysbefore,String daysafter,String limitto){
		Map<String, Object> map = new HashMap<String, Object>();
		//System.out.println("recon="+recon);
		
		String remoteAddr = "";
	     if (request != null) {
	            remoteAddr = request.getHeader("X-FORWARDED-FOR");
	            if (remoteAddr == null || "".equals(remoteAddr)) {
	                remoteAddr = request.getRemoteAddr();
	            }
	     }
		logger.info("[IP:"+remoteAddr+"][Click Parse]");
		logger.info("[IP:"+remoteAddr+"][Initial Event]"+initialevent);
		logger.info("[IP:"+remoteAddr+"][Inclusion Criteria]"+inc);
		logger.info("[IP:"+remoteAddr+"][Exclusion Criteria]"+exc);

		Document doc = this.ieService.translateByDoc(initialevent, inc, exc);

		List<DisplayCriterion> display_initial_event=this.ieService.displayDoc(doc.getInitial_event());
		List<DisplayCriterion> display_inclusion_criteria=this.ieService.displayDoc(doc.getInclusion_criteria());
		List<DisplayCriterion> display_exclusion_criteria=this.ieService.displayDoc(doc.getExclusion_criteria());



		logger.info("[IP:"+remoteAddr+"][Information Extraction Process Part 1 Parsing Results]"+JSONObject.fromObject(doc));
		httpSession.setAttribute("allcriteria", doc);
		map.put("initial_event", display_initial_event);
		map.put("include", display_inclusion_criteria);
		map.put("exclude", display_exclusion_criteria);

		logger.info("[IP:"+remoteAddr+"][Part 1 Finished]");
		return map;
	}
	
	

	/**
	 * download json format criteria
	 * Chi 
	 * 
	 * */
	@RequestMapping("/download")
	public void saveFile(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) {
		String remoteAddr = "";
	     if (request != null) {
	            remoteAddr = request.getHeader("X-FORWARDED-FOR");
	            if (remoteAddr == null || "".equals(remoteAddr)) {
	                remoteAddr = request.getRemoteAddr();
	            }
	     }
		logger.info("[IP:"+remoteAddr+"][Download Parsing Results]");
		Document doc = (Document) httpSession.getAttribute("allcriteria");
		
		StringBuffer jsonsb = new StringBuffer();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("eligibility criteria", doc);
		jsonsb.append(JSON.toJSONString(map));
		response.setContentType("text/plain");
		String fileName = "Criteria2Query_result";
		try {
			fileName = URLEncoder.encode("Criteria2Query", "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".json");
		BufferedOutputStream buff = null;
		ServletOutputStream outSTr = null;
		try {
			outSTr = response.getOutputStream();
			buff = new BufferedOutputStream(outSTr);
			buff.write(jsonsb.toString().getBytes("UTF-8"));
			buff.flush();
			buff.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buff.close();
				outSTr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	//Get criteria from ClinicalTrials.gov with a given NCTID,
	//remove the extra spaces and bullet symbols from the sentences,
	//and split the criteria into inclusion criteria and exclusion criteria.
	@RequestMapping(value = "/getct", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> getCrteriafromCT(HttpSession httpSession, HttpServletRequest request, String nctid) {

		Map<String, Object> map = new HashMap<>();

		try {
			// Log client IP
			String remoteAddr = "";
			if (request != null) {
				remoteAddr = request.getHeader("X-FORWARDED-FOR");
				if (remoteAddr == null || "".equals(remoteAddr)) {
					remoteAddr = request.getRemoteAddr();
				}
			}
			logger.info("[IP:" + remoteAddr + "][Fetch Criteria From ClinicalTrials.gov]");

			// Build API URL
			String url = "https://clinicaltrials.gov/api/v2/studies/" + nctid;


			// Fetch JSON response
			String jsonResponse = getJsonFromUrl(url);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(jsonResponse);
			JsonNode eligibilityNode = root.path("protocolSection").path("eligibilityModule");

			String gender = eligibilityNode.path("sex").asText("");
			String minimum_age = eligibilityNode.path("minimumAge").asText("");
			String maximum_age = eligibilityNode.path("maximumAge").asText("");
			String criteriaText = eligibilityNode.path("eligibilityCriteria").asText("");
			String healthy_volunteers = String.valueOf(eligibilityNode.path("healthyVolunteers").asBoolean(false));

			// Process criteria text (your original logic)
//			String[] lines = criteriaText.replaceAll("(\n)+", "\n").split("\n");
//			StringBuffer sb = new StringBuffer();
//
//			for (int x = 0; x < lines.length; x++) {
//				if (x > 0 && lines[x - 1].trim().length() == 0) {
//					String res = lines[x].trim();
//					if (res.startsWith("-  ")) {
//						res = res.substring(3);
//						sb.append(res);
//					} else {
//						sb.append("\n" + res);
//					}
//				} else {
//					if (lines[x].trim().isEmpty()) {
//						sb.append("\n");
//					} else {
//						sb.append(" " + lines[x].trim());
//					}
//				}
//			}
//
//			String ecstr = sb.toString();
			String ecstr = criteriaText.replaceAll("(\n)+", "\n");

			// Split into inclusion/exclusion criteria (use your existing IOUtil)
			String[] inc_exc = IOUtil.separateIncExc(ecstr);

			// Build result map
			map.put("gender", gender);
			map.put("minimum_age", minimum_age);
			map.put("maxmum_age", maximum_age);
			map.put("healthy_volunteers", healthy_volunteers);
			map.put("inc", inc_exc[0]);
			map.put("exc", inc_exc[1]);

		} catch (Exception e) {
			logger.error("Error in getCrteriafromCT for NCTID " + nctid + ": " + e.getMessage(), e);
			// Optionally you can put empty/default values in map here if desired
		}

		return map;
	}

	public static String getJsonFromUrl(String url) throws Exception {
		HttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet(url);
		request.addHeader("Accept", "application/json");

		HttpResponse response = client.execute(request);
		return EntityUtils.toString(response.getEntity(), "UTF-8");
	}




}
