package edu.columbia.dbmi.ohdsims.controller;

import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.ObservationConstraint;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.service.IConceptFilteringService;
import edu.columbia.dbmi.ohdsims.service.IConceptMappingService;
import edu.columbia.dbmi.ohdsims.service.IInformationExtractionService;
import edu.columbia.dbmi.ohdsims.service.IQueryFormulateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/main")
public class MainController {
	@Resource
	private IInformationExtractionService ieService;

	@Resource
	private IConceptMappingService conceptMappingService;

	@Resource
	private IQueryFormulateService qfService;
	
	@Resource
	private IConceptFilteringService cfService;

	@RequestMapping("/shownewpage")
	public String shownewPage() throws Exception {
		return "newPage";
	}

	@RequestMapping("/gojson")
	public String showJsonPage(HttpSession httpSession, HttpServletRequest request, ModelMap model) throws Exception {
		return "jsonPage";
	}

	@RequestMapping("/sqlpage")
	public String toSQLPage(HttpSession httpSession) throws Exception {
		return "sqlPage";
	}

	@RequestMapping("/slides")
	public String showsearchPICO(HttpSession httpSession) throws Exception {
		return "slidesPage";
	}

	
	
	@RequestMapping("/autoparse")
	@ResponseBody
	public Map<String, Object> runPipeLine(HttpSession httpSession, HttpServletRequest request, String initialevent, String inc,
		String exc, boolean abb,String obstart,String obend,String daysbefore,String daysafter,String limitto) {
		Document doc = this.ieService.translateByDoc(initialevent, inc, exc);
		doc = this.ieService.patchIEResults(doc);
		if (abb == true) {
			doc = this.ieService.abbrExtensionByDoc(doc);
		}
		List<ConceptSet> allsts= this.conceptMappingService.getAllConceptSets();
		List<Term> terms = this.conceptMappingService.getDistinctTerm(doc);
		Map<String, Integer> conceptSetIds = this.conceptMappingService.createConceptsByTerms(allsts,terms);
		doc = this.conceptMappingService.linkConceptSetsToTerms(doc, conceptSetIds);
//		JSONObject cohortjson = this.qfService.formualteCohortQuery(doc);
//		Map<String,Object> map=new HashMap<String,Object>();
//		map.put("jsonResult", cohortjson.toString());
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
		httpSession.setAttribute("allcriteria",doc);
		Map<String,Object> map=new HashMap<String,Object>();
		return map;
	}

}
