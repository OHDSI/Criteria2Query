package edu.columbia.dbmi.ohdsims.controller;

import java.io.BufferedOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import edu.columbia.dbmi.ohdsims.pojo.Cdmentity;
import edu.columbia.dbmi.ohdsims.pojo.DisplayCriterion;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.ObservationConstraint;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.service.IConceptFilteringService;
import edu.columbia.dbmi.ohdsims.service.IInformationExtractionService;
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
	@Resource
	private IInformationExtractionService ieService;
	@Resource
	private IConceptFilteringService cfService;
	
	@RequestMapping("/parse")
	@ResponseBody
	public Map<String, Object> parseAllCriteria(HttpSession httpSession, HttpServletRequest request,String initialevent, String inc, String exc,boolean abb,String obstart,String obend,String daysbefore,String daysafter,String limitto) {
		Map<String, Object> map = new HashMap<String, Object>();
		Document doc = this.ieService.translateByDoc(initialevent, inc, exc);
		doc = this.ieService.patchIEResults(doc);
		ObservationConstraint oc=new ObservationConstraint();
		oc.setDaysAfter(Integer.valueOf(daysafter));
		oc.setDaysBefore(Integer.valueOf(daysbefore));
		oc.setLimitTo(limitto);
		if(obstart.length()>0){
			oc.setStartDate(obstart);
		}else{
			oc.setStartDate(null);
		}
		if(obend.length()>0){
			oc.setEndDate(obend);
		}else{
			oc.setEndDate(null);
		}
		doc.setInitial_event_constraint(oc);
		List<DisplayCriterion> display_initial_event=this.ieService.displayDoc(doc.getInitial_event());
		List<DisplayCriterion> display_inclusion_criteria=this.ieService.displayDoc(doc.getInclusion_criteria());
		List<DisplayCriterion> display_exclusion_criteria=this.ieService.displayDoc(doc.getExclusion_criteria());
		if(abb==true){
			doc= this.ieService.abbrExtensionByDoc(doc);
		}
		doc=this.cfService.removeRedundency(doc);
		httpSession.setAttribute("allcriteria", doc);
		map.put("initial_event", display_initial_event);
		map.put("include", display_inclusion_criteria);
		map.put("exclude", display_exclusion_criteria);
		return map;
	}

	/**
	 * download json format criteria
	 * Chi 
	 * 
	 * */
	@RequestMapping("/download")
	public void saveFile(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) {
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
	
	@RequestMapping(value = "/getct", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> getCrteriafromCT(HttpSession httpSession, HttpServletRequest request, String nctid)
			throws Exception {
		String url = "https://clinicaltrials.gov/show/" + nctid + "?displayxml=true";
		String response = WebUtil.getCTByNctid(nctid);
		String[] criteria = WebUtil.parse(response);
		StringBuffer insb = new StringBuffer();
		StringBuffer exsb = new StringBuffer();
		String gender = criteria[1];
		String minimum_age = criteria[2];
		String maxmum_age = criteria[3];
		String sampling_method = criteria[4];
		String study_pop = criteria[5];
		String healthy_volunteers = criteria[6];
		
		
		boolean flag = false;
		String[] lines = criteria[0].split("\n");
		StringBuffer sb = new StringBuffer();
		for (int x = 1; x < lines.length; x++) {
			if (lines[x - 1].trim().length() == 0) {
				// System.out.println("iiiiin=" +
				// removeMultiSpace(lines[x]));
				String res = IOUtil.removeMultiSpace(lines[x]);
				if (res.startsWith("-  ")) {
					res = res.substring(3);
				}
				sb.append("\n" + res);
			} else {
				// System.out.println("add=" + removeMultiSpace(lines[x]));
				sb.append(" " + IOUtil.removeMultiSpace(lines[x]));
			}
		}
		//System.out.println("____________");
		//System.out.println(sb.toString());
		String ecstr = sb.toString();
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
		map.put("exc", inc_exc[1]);// ----
		return map;
	}
}
