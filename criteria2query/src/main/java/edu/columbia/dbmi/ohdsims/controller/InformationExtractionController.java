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
	public Map<String, Object> getCrteriafromCT(HttpSession httpSession, HttpServletRequest request, String nctid)
			throws Exception {
		String remoteAddr = "";
	    if (request != null) {
	            remoteAddr = request.getHeader("X-FORWARDED-FOR");
	            if (remoteAddr == null || "".equals(remoteAddr)) {
	                remoteAddr = request.getRemoteAddr();
	            }
	     }
		logger.info("[IP:"+remoteAddr+"][Fetch Criteria From ClinicalTrials.gov]");
		String url = "https://clinicaltrials.gov/show/" + nctid + "?displayxml=true";
		String response = WebUtil.getCTByNctid(nctid);
		//Parse the XMl file and get the useful information, including criteria, gender, minimum_age, maximum_age,
		//sampling_method, study_pop, healthy_volunteers.
		String[] criteria = WebUtil.parse(response);
		StringBuffer insb = new StringBuffer();
		StringBuffer exsb = new StringBuffer();
		String gender = criteria[1];
		String minimum_age = criteria[2];
		String maxmum_age = criteria[3];
		String sampling_method = criteria[4];
		String study_pop = criteria[5];
		String healthy_volunteers = criteria[6];

		//Split the criteria text into sentences based on the bullet symbols "-  "
		//and delete the spaces and "-  " before sentences.
		//Combine the sentences or words which belong to the same point
		boolean flag = false;
		String[] lines = criteria[0].split("\n");
		StringBuffer sb = new StringBuffer();
		for (int x = 1; x < lines.length; x++) {
			if (lines[x - 1].trim().length() == 0) {
				String res = lines[x].trim();
				if (res.startsWith("-  ")) {
					res = res.substring(3);
					sb.append(res);
				}else{
					sb.append("\n" + res);
				}

			} else {
				if(lines[x].trim().isEmpty()){
					sb.append("\n");
				}else{//A sentence seperates into several lines.
					sb.append(" "+lines[x].trim());
				}
			}
		}
		String ecstr = sb.toString();
		//Separate inclusion and exclusion criteria from the criteria text.
		String[] inc_exc=IOUtil.separateIncExc(ecstr);
		Map<String, Object> map = null;
		map = new HashMap<String, Object>();
		map.put("gender", gender);
		map.put("minimum_age", minimum_age);
		map.put("maxmum_age", maxmum_age);

		map.put("sampling_method", sampling_method);
		map.put("study_pop", study_pop);
		map.put("healthy_volunteers", healthy_volunteers);
		map.put("inc", inc_exc[0]);
		map.put("exc", inc_exc[1]);
		return map;
	}




}
