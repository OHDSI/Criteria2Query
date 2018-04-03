package edu.columbia.dbmi.ohdsims.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import java.util.Properties;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import edu.columbia.dbmi.ohdsims.pojo.Attribute;
import edu.columbia.dbmi.ohdsims.pojo.Concept;
import edu.columbia.dbmi.ohdsims.pojo.ConceptRecordCount;
import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import edu.columbia.dbmi.ohdsims.pojo.DisplayCriterion;
import edu.columbia.dbmi.ohdsims.pojo.Cdmentity;
import edu.columbia.dbmi.ohdsims.pojo.ExtendConcept;
import edu.columbia.dbmi.ohdsims.tool.FeedBackTool;
import edu.columbia.dbmi.ohdsims.util.CmdUtil;
import edu.columbia.dbmi.ohdsims.util.FileUtil;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import edu.columbia.dbmi.ohdsims.util.ATLASUtil;
import edu.columbia.dbmi.ohdsims.util.JSONUtil;
import edu.columbia.dbmi.ohdsims.util.PreprocessUtil;
import edu.columbia.dbmi.ohdsims.util.WebUtil;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.util.CoreMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import opennlp.tools.util.StringUtil;

@Controller
@RequestMapping("/nlpmethod")
public class NlpController {

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
		// Using stanfordNLP to do sentence segment
		Reader reader = new StringReader(criteria[0]);

		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();

		for (List<HasWord> sentence : dp) {
			// SentenceUtils not Sentence
			String sentenceString = SentenceUtils.listToString(sentence);
			sentenceList.add(sentenceString);
		}
		ArrayList<String> inlist = new ArrayList<String>();
		ArrayList<String> exlist = new ArrayList<String>();
//		if (gender.length() > 0) {
//			inlist.add("Gender Eligible for Study: " + gender);
//		}
//		if (minimum_age.length() > 0) {
//			inlist.add("Minimum Age Eligible for Study: " + minimum_age);
//		}
//		if (maxmum_age.length() > 0) {
//			inlist.add("Maximum Age Eligible for Study: " + maxmum_age);
//		}
//		if (healthy_volunteers.length() > 0) {
//			inlist.add("Accepts Healthy Volunteers: " + healthy_volunteers);
//		}
//		if (sampling_method.length() > 0) {
//			inlist.add("Sampling Method :" + sampling_method);
//		}
//		if (study_pop.length() > 0) {
//			inlist.add("Study Population :" + StringEscapeUtils.escapeHtml(study_pop));
//		}
		
		boolean flag = false;
		for (String sentence : sentenceList) {

			if (sentence.toLowerCase().contains("inclusion criteria")
					&& (sentence.toLowerCase().contains("exclusion criteria") == false)) {

				int pos = sentence.toLowerCase().indexOf("inclusion criteria :");
				inlist.add(sentence.toString().substring(pos + "inclusion criteria :".length()));

			} else if ((sentence.toLowerCase().contains("inclusion criteria") == false)
					&& sentence.toLowerCase().contains("exclusion criteria")) {
				flag = true;

				int pos = sentence.toLowerCase().indexOf("exclusion criteria :");
				exlist.add(sentence.toString().substring(pos + "exclusion criteria :".length()));

			} else if (sentence.toLowerCase().contains("inclusion criteria")
					&& sentence.toLowerCase().contains("exclusion criteria")) {
				flag = true;
				int inindex = sentence.toLowerCase().indexOf("inclusion criteria :");
				int posindex = sentence.toLowerCase().indexOf("exclusion criteria :");
				inlist.add(sentence.toString().substring(inindex + "inclusion criteria :".length(), posindex));
				exlist.add(sentence.toString().substring(posindex + "exclusion criteria :".length()));
			} else {
				if (flag == false) {
					inlist.add(sentence);
				} else {
					exlist.add(sentence);
				}
			}

		}
		for (int x = 0; x < inlist.size(); x++) {
			System.out.println(inlist.get(x));
			// NLPUtil.ner4Sentence(inlist.get(x), null);
			insb.append(inlist.get(x).replaceAll("-LRB-", "(").replaceAll("-RRB-", ")"));
			insb.append("\n");
		}

		for (int y = 0; y < exlist.size(); y++) {
			System.out.println(exlist.get(y));
			// NLPUtil.ner4Sentence(exlist.get(y), null);
			exsb.append(exlist.get(y).replaceAll("-LRB-", "(").replaceAll("-RRB-", ")"));
			exsb.append("\n");
		}
		Map<String, Object> map = null;
		map = new HashMap<String, Object>();
		map.put("gender", gender);
		map.put("minimum_age", minimum_age);
		map.put("maxmum_age", maxmum_age);

		map.put("sampling_method", sampling_method);
		map.put("study_pop", study_pop);
		map.put("healthy_volunteers", healthy_volunteers);
		map.put("inc", insb.toString());
		map.put("exc", exsb.toString());// ----
		JSONObject jsonObject = JSONObject.fromObject(map);
		System.out.println(jsonObject);
		System.out.println("!!!!!!");
		return jsonObject;
	}


	

	@RequestMapping("/conceptset")
	public String conceptSet(HttpSession httpSession, HttpServletRequest request, String inc, String exc,
			ModelMap model) throws Exception {
		return "conceptSetPage";

	}
	@RequestMapping("/gojson")
	public String showJsonPage(@RequestParam("id") String id,HttpSession httpSession, HttpServletRequest request, ModelMap model) throws Exception {
		return "jsonPage";
	}


	@RequestMapping("/jsonpage")
	public String toJSONPage(HttpSession httpSession, String conceptsets) throws Exception {
		return "jsonresultPage";
	}
	
	@RequestMapping("/sqlpage")
	public String toSQLPage(HttpSession httpSession) throws Exception {
//		System.out.println("id="+id);
//		httpSession.setAttribute("cohortid", id);
		return "sqlPage";
	}
	
//	@RequestMapping("/sqlpage")
//	public String toSQLPage(HttpSession httpSession) throws Exception {
//		System.out.println("id="+id);
//		httpSession.setAttribute("cohortid", id);
//		return "sqlPage";
//	}
	
	@RequestMapping("/sqlget")
	@ResponseBody
	public Map<String, Object> getSQLstr(HttpSession httpSession) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String cohortid = (String) httpSession.getAttribute("cohortid");
		String jsonstr = (String) httpSession.getAttribute("jsonresult");
		map.put("jsonstr", jsonstr);
		map.put("cohortid", cohortid);
		return map;
	}
	

	@RequestMapping("/jsonresult")
	@ResponseBody
	public Map<String, Object> jumptoConceptSets(HttpSession httpSession, String conceptsets) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String jsonstr = (String) httpSession.getAttribute("jsonresult");
		map.put("jsonstr", jsonstr);
		return map;
	}

	@RequestMapping("/feedback")
	@ResponseBody
	public Map<String, Object> sendFeedback(HttpSession httpSession, String email, String content)
			throws Exception {
		//System.out.println("email="+email);
		//System.out.println("content="+content);
		//write to comments file	
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp=df.format(new Date());
		FeedBackTool fbt=new FeedBackTool();
		fbt.recordFeedback(timestamp,email,content);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", "success");
		return map;
	}

}
