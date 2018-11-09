package edu.columbia.dbmi.ohdsims.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.columbia.dbmi.ohdsims.pojo.CdmCohort;
import edu.columbia.dbmi.ohdsims.pojo.CdmCriteria;
import edu.columbia.dbmi.ohdsims.pojo.CdmCriterion;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.GenderGroup;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.OccurrenceStart;
import edu.columbia.dbmi.ohdsims.pojo.Paragraph;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.pojo.TemporalConstraint;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.columbia.dbmi.ohdsims.service.IQueryFormulateService;
import edu.columbia.dbmi.ohdsims.tool.OHDSIApis;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import edu.columbia.dbmi.ohdsims.util.NumericConvert;
import edu.columbia.dbmi.ohdsims.util.TemporalNormalize;
import edu.stanford.nlp.util.Triple;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("qfService")
public class QueryFormulateServiceImpl implements IQueryFormulateService {
	final static public String cohorturl=GlobalSetting.ohdsi_api_base_url+"cohortdefinition/";
	
	@Override
	public CdmCohort translateByDoc(Document doc) {
		CdmCohort cohort = new CdmCohort();
		cohort.setDaysAfter(doc.getInitial_event_constraint().getDaysAfter());
		cohort.setDaysBefore(doc.getInitial_event_constraint().getDaysBefore());
		cohort.setLimitTo(doc.getInitial_event_constraint().getLimitTo());
		OccurrenceStart os=new OccurrenceStart();
		os.setStart(doc.getInitial_event_constraint().getStartDate());
		os.setEnd(doc.getInitial_event_constraint().getEndDate());
		os.setOperator("bt");
		cohort.setOccurrenceStart(os);
		List<CdmCriteria> initial_event = new ArrayList<CdmCriteria>();
		List<CdmCriteria> additional_criteria = new ArrayList<CdmCriteria>();
		if (doc.getInitial_event() != null) {
			List<Paragraph> initial_ps = doc.getInitial_event();
			for (Paragraph p : initial_ps) {
				initial_event.addAll(translateByParagraph(p,true));
			}
		}
		if (doc.getInclusion_criteria() != null) {
			List<Paragraph> inclusion = doc.getInclusion_criteria();
			for (Paragraph p : inclusion) {
				additional_criteria.addAll(translateByParagraph(p,true));
			}
		}
		if (doc.getExclusion_criteria() != null) {
			List<Paragraph> exclusion = doc.getExclusion_criteria();
			for (Paragraph p : exclusion) {
				additional_criteria.addAll(translateByParagraph(p,false));
			}
		}
		cohort.setInitial_event(initial_event);
		cohort.setAdditional_criteria(additional_criteria);
		return cohort;
	}

	@Override
	public List<CdmCriteria> translateByParagraph(Paragraph p,boolean include) {
		// TODO Auto-generated method stub
		List<CdmCriteria> cdmCriterias = new ArrayList<CdmCriteria>();
		List<Sentence> sents = p.getSents();
		if (sents != null) {
			for (int i = 0; i < sents.size(); i++) {
				Sentence s = sents.get(i);
				CdmCriteria c = translateBySentence(s, include);
				c.setIncluded(include);
				cdmCriterias.add(c);
				
			}
		}
		return cdmCriterias;
	}

	@Override
	public CdmCriteria translateBySentence(Sentence s, boolean include) {
		CdmCriteria cdmc = new CdmCriteria();
		cdmc.setText(s.getText());
		cdmc.setDesc(GlobalSetting.c2qversion);
		List<Term> terms = s.getTerms();
		List<Triple<Integer, Integer, String>> relations = s.getRelations();
		List<CdmCriterion> clist=new ArrayList<CdmCriterion>();
		for (Term t : terms) {
			if (Arrays.asList(GlobalSetting.primaryEntities).contains(t.getCategorey())) {
				CdmCriterion cunit = new CdmCriterion();
				cunit.setOrginialtext(t.getText());
				cunit.setCriterionId(t.getTermId());
				cunit.setConceptsetId(t.getVocabularyId());
				cunit.setNeg(t.isNeg());
				cunit.setDomain(t.getCategorey());
				for (Triple<Integer, Integer, String> r : relations) {
					if (t.getTermId() == r.first) {
						if(r.third.equals("has_temporal")){
							TemporalConstraint[] temporalwindow = normalizeTemporal(findTermById(terms,r.second).getText());
							if(temporalwindow==null){
								continue;
							}
							cunit.setTemporalwindow(temporalwindow);
						}else if(r.third.equals("has_value")){
							String valuestr=findTermById(terms,r.second).getText();
							Map<String, String> map = new HashMap<String,String>();
							if(t.getCategorey().equals("Demographic")&&t.getText().toLowerCase().contains("age")){
								map.put("age_range", valuestr);
								System.err.println("value_str="+valuestr);
							}else if(t.getCategorey().equals("Measurement")){
								map.put("measure_value", valuestr);
							}
							cunit.setAttributes(map);
						}
					}
				}
				clist.add(cunit);
			}
		}
		cdmc.setClist(clist);
		cdmc.setLogic_groups(s.getLogic_groups());
		return cdmc;
	}
	
	public Term findTermById(List<Term> terms,Integer termId){
		for(Term t:terms){
			if(t.getTermId()==termId){
				return t;
			}
		}
		return null;
	}
	
	public  TemporalConstraint[] normalizeTemporal(String temoralplaintext) {
		TemporalConstraint[] tc = new TemporalConstraint[2];
		tc[0] = new TemporalConstraint();
		tc[1] = new TemporalConstraint();
		TemporalNormalize tn = new TemporalNormalize();
		Integer days = tn.temporalNormalizeforNumberUnit(temoralplaintext);
		tc[0].setStart_days(days);
		tc[0].setStart_offset(-1);
		tc[0].setEnd_days(0);
		tc[0].setEnd_offset(1);
		if(temoralplaintext.toLowerCase().equals("current")){
			tc[0]=new TemporalConstraint();
		}
		
		if(days<10000){
			//System.out.println(days);
		return tc;
		}else{
			return null;
		}
	}
	
	public List<Integer> extractConceptsetByDoc(Document doc){
		List<Integer> conceptSetIds=new ArrayList<Integer>();
		conceptSetIds.addAll(extractConceptsetByPargraphList(doc.getInitial_event()));
		conceptSetIds.addAll(extractConceptsetByPargraphList(doc.getInclusion_criteria()));
		conceptSetIds.addAll(extractConceptsetByPargraphList(doc.getExclusion_criteria()));
		return conceptSetIds;
	}
	
	public List<Integer> extractConceptsetByPargraphList(List<Paragraph> plist){
		List<Integer> conceptSetIds=new ArrayList<Integer>();
		if(plist!=null){
			for(Paragraph p:plist){
				if(p.getSents()!=null){
					for(Sentence s:p.getSents()){
						if(s.getTerms()!=null){
							for(Term t:s.getTerms()){
								if(Arrays.asList(GlobalSetting.conceptSetDomains).contains(t.getCategorey())){
								conceptSetIds.add(t.getVocabularyId());
								}
							}
						}
					}
				}
			}
		}
		return conceptSetIds;
	}
	
	public JSONObject formualteCohortQuery(Document doc) {
		List<Integer> conceptsetIds=extractConceptsetByDoc(doc);
		CdmCohort omopcohort=translateByDoc(doc);
		JSONObject cohort = new JSONObject();
		//Merge All criteria to one criteria 
		CdmCriteria initial_criteria=new CdmCriteria();
		List<CdmCriterion> clist=new ArrayList<CdmCriterion>();
		for(CdmCriteria c:omopcohort.getInitial_event()){
			if(c!=null){
				clist.addAll(c.getClist());
			}
		}
		StringBuffer initialeventtext=new StringBuffer();
		for(CdmCriteria cdmc:omopcohort.getInitial_event()){
			initialeventtext.append(cdmc.getText());
		}
		initial_criteria.setText(initialeventtext.toString());
		initial_criteria.setInitialevent(true);
		initial_criteria.setIncluded(true);
		initial_criteria.setClist(clist);
		initial_criteria.setPriorDays(omopcohort.getDaysBefore());
		initial_criteria.setPostDays(omopcohort.getDaysAfter());
		initial_criteria.setLimitTo(omopcohort.getLimitTo());
		initial_criteria.setOccurenceStart(omopcohort.getOccurrenceStart());
		cohort.accumulate("ConceptSets", formulateConceptSet(conceptsetIds));
		// initial events
		cohort.accumulate("PrimaryCriteria", formulatePrimaryCriteria(initial_criteria));
		if(initial_criteria.getText()!=null &&initial_criteria.getText().length() >0){
			System.out.println("=>additional criteria");
			cohort.accumulate("AdditionalCriteria", formulateOneCdmCriteria(initial_criteria));
		}
		cohort.accumulate("QualifiedLimit", formulateQualifiedLimit());
		cohort.accumulate("ExpressionLimit", formulateExpressionLimit());
		// other criteria
		cohort.accumulate("InclusionRules", formulateInclusionRules(omopcohort.getAdditional_criteria()));
		// set null for CensoringCriteria and CollapseSettings
		cohort.accumulate("CensoringCriteria", formulateCensoringCriteria());
		cohort.accumulate("CollapseSettings", formulateCollapseSettings());
		return cohort;
	}
	
	

	public JSONArray formulateConceptSet(List<Integer> conceptsetIds) {
		JSONArray conceptsetarr = new JSONArray();
		for (Integer i:conceptsetIds) {
			System.out.println(i);
			conceptsetarr.add(OHDSIApis.querybyconceptSetid(i));
		}
		return conceptsetarr;
	}

	public JSONObject formulateQualifiedLimit() {
		JSONObject qualifiedlimit = new JSONObject();
		qualifiedlimit.accumulate("Type", "First");
		return qualifiedlimit;
	}

	public JSONObject formulateExpressionLimit() {
		JSONObject expressionlimit = new JSONObject();
		expressionlimit.accumulate("Type", "First");
		return expressionlimit;
	}

	public JSONArray formulateCensoringCriteria() {
		JSONArray censorcriteria = new JSONArray();
		return censorcriteria;
	}

	public JSONObject formulateCollapseSettings() {
		JSONObject collapseSettings = new JSONObject();
		collapseSettings.accumulate("CollapseType", "ERA");
		collapseSettings.accumulate("EraPad", 0);
		return collapseSettings;
	}

	public JSONObject formulatePrimaryCriteria(CdmCriteria cdmc) {
		JSONObject primarycriteria = new JSONObject();
		JSONObject observationWindow = new JSONObject();
		if (cdmc != null&&cdmc.getClist()!=null && hasEvents(cdmc)) {
			JSONArray criterialist = formualtePrimaryCriteriaList(cdmc);
			primarycriteria.accumulate("CriteriaList", criterialist);
			observationWindow.accumulate("PriorDays", cdmc.getPriorDays());
			observationWindow.accumulate("PostDays", cdmc.getPostDays());
			primarycriteria.accumulate("ObservationWindow", observationWindow);
			JSONObject primaryCriteriaLimit = new JSONObject();
			primaryCriteriaLimit.accumulate("Type", cdmc.getLimitTo());
			primarycriteria.accumulate("PrimaryCriteriaLimit", primaryCriteriaLimit);
		} else {
			// If there is no initial event, it will set any visit as its
			// initial event
			JSONArray criterialist = defaultInitialEvent();
			primarycriteria.accumulate("CriteriaList", criterialist);
			observationWindow.accumulate("PriorDays", cdmc.getPriorDays());
			observationWindow.accumulate("PostDays", cdmc.getPostDays());
			primarycriteria.accumulate("ObservationWindow", observationWindow);
			JSONObject primaryCriteriaLimit = new JSONObject();
			primaryCriteriaLimit.accumulate("Type", cdmc.getLimitTo());
			primarycriteria.accumulate("PrimaryCriteriaLimit", primaryCriteriaLimit);
		}
		return primarycriteria;
	}
	
	public boolean hasEvents(CdmCriteria cdmc){
		if(cdmc!=null){
			for(CdmCriterion c:cdmc.getClist()){
				if(Arrays.asList(GlobalSetting.conceptSetDomains).contains(c.getDomain())){
					return true;
				}
			}
		}
		return false;
	}

	public JSONArray defaultInitialEvent() {
		JSONArray jacriterialist = new JSONArray();
		JSONObject jo = new JSONObject();
		// JSONObject jnull=new JSONObject();
		JSONObject jvo = new JSONObject();
		jvo.accumulate("VisitSourceConcept", null);
		jvo.accumulate("First", null);
		jo.accumulate("VisitOccurrence", jvo);

		jacriterialist.add(jo);
		return jacriterialist;
	}

	public JSONObject formulateInitialEventPrimaryCriteria(List<CdmCriteria> initial_event) {
		JSONObject expression = new JSONObject();
		return expression;
	}


	/**
	 * translate a list of CdmCriteria to JSON
	 * 
	 */
	public JSONArray formulateInclusionRules(List<CdmCriteria> clist) {
		JSONArray json_arr = new JSONArray();
		for (int i = 0; i < clist.size(); i++) {
			JSONObject jo = new JSONObject();
			CdmCriteria cdmc = clist.get(i);
			System.out.println("is include?"+cdmc.isIncluded());
			String inctag=cdmc.isIncluded()?"[INC]":"[EXC]";
			jo.accumulate("name", inctag+cdmc.getText());
			jo.accumulate("description", cdmc.getDesc());
			JSONObject expression = formulateOneCdmCriteria(cdmc);
			System.out.println(expression);
			jo.accumulate("expression", expression);
			json_arr.add(jo);

		}
		return json_arr;
	}

	public JSONObject formulateOneCdmCriteria(CdmCriteria cdmc) {
		JSONObject expression = new JSONObject();
		if(cdmc.getLogic_groups()!=null&&cdmc.getLogic_groups().size()>0){
			if(cdmc.getLogic_groups().get(0).size()>1 && cdmc.isIncluded()){
				cdmc.setType("ANY");
			}
		}
		expression.accumulate("Type", cdmc.getType());
		JSONArray criterialist = translateIncludeRules2Json(cdmc.getClist(), cdmc.isNeg(), cdmc.isIncluded());
		expression.accumulate("CriteriaList", criterialist);
		JSONArray janull = new JSONArray();
		JSONArray demographic = formulateDemographic(cdmc.getClist());
		expression.accumulate("DemographicCriteriaList", demographic);
		expression.accumulate("Groups", janull);
		return expression;
	}
	// inclusion
	public JSONArray translateIncludeRules2Json(List<CdmCriterion> criteria_list, boolean paragraphneg,
			boolean included) {
		JSONArray criterialist = new JSONArray();
		for (CdmCriterion cdmcriterion : criteria_list) {
			if (Arrays.asList(GlobalSetting.conceptSetDomains).contains(cdmcriterion.getDomain())) {
				JSONObject criterion = new JSONObject();
				JSONObject classandconcept = new JSONObject();
				classandconcept = formualteCriteria(cdmcriterion);
				criterion.accumulate("Criteria", classandconcept);
				System.out.println(criterion);
				// Temporal windows
				TemporalConstraint[] tcarr = cdmcriterion.getTemporalwindow();
				if (tcarr == null) {
					criterion.accumulate("StartWindow", translateTemporal(null));
				} else {
					if (tcarr[0] != null) {
						criterion.accumulate("StartWindow", translateTemporal(tcarr[0]));
					}
					if (tcarr[1] != null) {
						criterion.accumulate("EndWindow", translateTemporal(tcarr[1]));
					}
				}
				if (!(cdmcriterion.isNeg() ^ (included))) {
					JSONObject occurrence = new JSONObject();
					occurrence.accumulate("Type", 0);
					occurrence.accumulate("Count", 0);
					criterion.accumulate("Occurrence", occurrence);
				}else{
					JSONObject occurrence = new JSONObject();
					occurrence.accumulate("Type", 2);
					occurrence.accumulate("Count", 1);
					criterion.accumulate("Occurrence", occurrence);
				}
				criterialist.add(criterion);
			}
		}
		return criterialist;
	}
	
	public JSONArray formulateDemographic(List<CdmCriterion> criteria_list){
		JSONArray demographicarr = new JSONArray();
		for (CdmCriterion cdmcriterion : criteria_list) {
			if(cdmcriterion.getDomain().equals("Demographic")){
				//age
				if(cdmcriterion.getOrginialtext().toLowerCase().contains("age")){
					JSONObject jo=new JSONObject();
					if(cdmcriterion.getAttributes()!=null){
						String agerangestr=cdmcriterion.getAttributes().get("age_range");
						System.out.println("==???age???==");
						List<Double> m=NumericConvert.recognizeNumbersAdvanced(agerangestr);
						if(m!=null){
							System.out.println("m_size="+m.size());
							System.out.println("number="+m.get(0));
							System.err.println("str="+agerangestr);
							if(m.size()==1 && (((agerangestr.indexOf(">")!=-1 )&&agerangestr.indexOf("<")==-1&&agerangestr.indexOf("=")==-1)||(((agerangestr.indexOf("older") !=-1))&&(agerangestr.indexOf("younger") ==-1)))){
								jo.accumulate("Value",m.get(0));
								jo.accumulate("Op", "gt");
							}else if(m.size()==1 &&((agerangestr.indexOf("<")!=-1 )&&agerangestr.indexOf(">")==-1&&agerangestr.indexOf("=")==-1)||((agerangestr.indexOf("younger") !=-1)&&( agerangestr.indexOf("older") ==-1))){
								jo.accumulate("Value",m.get(0));
								jo.accumulate("Op", "lt");
							}else if(m.size()==2){
								jo.accumulate("Value",m.get(0));
								jo.accumulate("Extent",m.get(1));
								jo.accumulate("Op", "bt");
							}else if(m.size()==1 && (agerangestr.indexOf("≤")!=-1 )){
								System.out.println("lte=====>");
								jo.accumulate("Value",m.get(0));
								jo.accumulate("Op", "lte");
							}else if((m.size()==1 && (agerangestr.indexOf("≥")!=-1 )||((agerangestr.indexOf(">")!=-1)&&(agerangestr.indexOf("=")!=-1)))){
								System.err.println("gte=========================================>");
							
								System.out.println("gte=====>");
								jo.accumulate("Value",m.get(0));
								jo.accumulate("Op", "gte");
							}
						}
					}
					JSONObject agejson=new JSONObject();
					agejson.accumulate("Age", jo);
					demographicarr.add(agejson);
				}
				if(Arrays.asList(GenderGroup.maletriggers).contains(cdmcriterion.getOrginialtext().toLowerCase())||Arrays.asList(GenderGroup.femaletriggers).contains(cdmcriterion.getOrginialtext().toLowerCase())){
					JSONArray genderarr=new JSONArray();
					JSONObject genderjson=new JSONObject();
					if(Arrays.asList(GenderGroup.maletriggers).contains(cdmcriterion.getOrginialtext().toLowerCase())){
						genderarr.add(setMale());
					}
					if(Arrays.asList(GenderGroup.femaletriggers).contains(cdmcriterion.getOrginialtext().toLowerCase())){
						genderarr.add(setFeMale());
					}
					genderjson.accumulate("Gender", genderarr);
					demographicarr.add(genderjson);
				}
			}
		}
		return demographicarr;
	}
	public JSONObject setMale(){
		JSONObject jo=new JSONObject();
		jo.accumulate("CONCEPT_CODE", "M");
		jo.accumulate("CONCEPT_ID", 8507);
		jo.accumulate("CONCEPT_NAME", "MALE");
		jo.accumulate("DOMAIN_ID", "Gender");
		jo.accumulate("VOCABULARY_ID", "Gender");
		return jo;
	}
	
	public JSONObject setFeMale(){
		JSONObject jo=new JSONObject();
		jo.accumulate("CONCEPT_CODE", "F");
		jo.accumulate("CONCEPT_ID", 8532);
		jo.accumulate("CONCEPT_NAME", "FEMALE");
		jo.accumulate("DOMAIN_ID", "Gender");
		jo.accumulate("VOCABULARY_ID", "Gender");
		return jo;
	}

	public JSONArray formualtePrimaryCriteriaList(CdmCriteria cdmc) {
		
		List<CdmCriterion> criteria_list=cdmc.getClist();
		JSONArray criterialist = new JSONArray();
		for (CdmCriterion cdmcriterion : criteria_list) {
			cdmcriterion.setOccurenceStart(cdmc.getOccurenceStart());
			JSONObject classandconcept = new JSONObject();
			classandconcept = formualtePrimaryCriteriaInInitialEvent(cdmcriterion);
			if(classandconcept!=null){
				criterialist.add(classandconcept);
			}
		}
		return criterialist;
	}
	public JSONObject formualtePrimaryCriteriaInInitialEvent(CdmCriterion cdmcriterion) {
		JSONObject conceptsetid = new JSONObject();
		JSONObject classandconcept = new JSONObject();
		JSONObject occurscontent=new JSONObject();
		if(cdmcriterion.getOccurenceStart()!=null&&cdmcriterion.getOccurenceStart().getStart()!=null){
			occurscontent.accumulate("Value", cdmcriterion.getOccurenceStart().getStart());
			occurscontent.accumulate("Extent", cdmcriterion.getOccurenceStart().getEnd());
			occurscontent.accumulate("Op","bt");
		}
		if (cdmcriterion.getDomain().equals("Condition")) {// Condition
			conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
			if(occurscontent.containsKey("Value")){
				conceptsetid.accumulate("OccurrenceStartDate",occurscontent);
			}
			classandconcept.accumulate("ConditionOccurrence", conceptsetid);
			
		} else if (cdmcriterion.getDomain().equals("Drug")) {// Drug
			conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
			if(occurscontent.containsKey("Value")){
				conceptsetid.accumulate("OccurrenceStartDate",occurscontent);
			}
			classandconcept.accumulate("DrugExposure", conceptsetid);
		} else if (cdmcriterion.getDomain().equals("Observation")) {// Observation
			conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
			if(occurscontent.containsKey("Value")){
				conceptsetid.accumulate("OccurrenceStartDate",occurscontent);
			}
			classandconcept.accumulate("Observation", conceptsetid);

		} else if (cdmcriterion.getDomain().equals("Procedure")) {// Procedure
			conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
			if(occurscontent.containsKey("Value")){
				conceptsetid.accumulate("OccurrenceStartDate",occurscontent);
			}
			classandconcept.accumulate("ProcedureOccurrence", conceptsetid);
		} else if (cdmcriterion.getDomain().equals("Measurement")) {// Procedure
			conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());	
			if(occurscontent.containsKey("Value")){
				conceptsetid.accumulate("OccurrenceStartDate",occurscontent);
			}
			Map<String,String> attributes=cdmcriterion.getAttributes();
			if(attributes!=null){
				String mvalue=attributes.get("measure_value");
				System.out.println("mvalut="+mvalue);
				JSONObject jo=new JSONObject();
				if(mvalue!=null){					
					List<Double> m=NumericConvert.recognizeNumbersAdvanced(mvalue);
					if(m!=null){
						if(m.size()==2){
							if(mvalue.toLowerCase().contains("or")){
								jo.accumulate("Value",m.get(0));
								jo.accumulate("Extent",m.get(1));
								jo.accumulate("Op", "!bt");
							}else{
								jo.accumulate("Value",m.get(0));
								jo.accumulate("Extent",m.get(1));
								jo.accumulate("Op", "bt");
							}
						}else if(m.size()==1){
							System.out.println("1 number");
						if(((mvalue.indexOf("=")==-1&& mvalue.indexOf(">")!=-1)||(mvalue.indexOf("greater")!=-1)||(mvalue.indexOf("higher")!=-1))){
							System.out.println(">");
							jo.accumulate("Value",m.get(0));
							jo.accumulate("Op", "gt");
						}else if((mvalue.indexOf("=")==-1 &&mvalue.indexOf("<")!=-1)||(mvalue.indexOf("lower")!=-1)||(mvalue.indexOf("smaller")!=-1)){
							System.out.println("<");
							jo.accumulate("Value",m.get(0));
							jo.accumulate("Op", "lt");
						}else if((mvalue.indexOf("≥")!=-1)||((mvalue.indexOf("greater")!=-1)&&(mvalue.indexOf("equal")!=-1))||((mvalue.indexOf(">")!=-1)&&(mvalue.indexOf("=")!=-1))){
							System.out.println(">=");
							jo.accumulate("Value",m.get(0));
							jo.accumulate("Op", "gte");
						}else if((mvalue.indexOf("≤")!=-1||((mvalue.indexOf("less")!=-1)&&(mvalue.indexOf("equal")!=-1)))){
							System.out.println("<=");
							jo.accumulate("Value",m.get(0));
							jo.accumulate("Op", "lte");
						}
					}
				}
				}
				conceptsetid.accumulate("ValueAsNumber", jo);
			}
			classandconcept.accumulate("Measurement", conceptsetid);
			
		}else{
			return null;
		}
		return classandconcept;
	}
	
	public JSONObject formualteCriteria(CdmCriterion cdmcriterion) {
		JSONObject conceptsetid = new JSONObject();
		JSONObject classandconcept = new JSONObject();
		if (cdmcriterion.getDomain().equals("Condition")) {// Condition
			conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());	
			classandconcept.accumulate("ConditionOccurrence", conceptsetid);
			
		} else if (cdmcriterion.getDomain().equals("Drug")) {// Drug
			conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());		
			classandconcept.accumulate("DrugExposure", conceptsetid);
		} else if (cdmcriterion.getDomain().equals("Observation")) {// Observation
			conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());		
			classandconcept.accumulate("Observation", conceptsetid);

		} else if (cdmcriterion.getDomain().equals("Procedure")) {// Procedure
			conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());		
			classandconcept.accumulate("ProcedureOccurrence", conceptsetid);
		} else if (cdmcriterion.getDomain().equals("Measurement")) {// Procedure
			conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());	
			Map<String,String> attributes=cdmcriterion.getAttributes();
			if(attributes!=null){
				String mvalue=attributes.get("measure_value");
				JSONObject jo=new JSONObject();
				if(mvalue!=null){					
					List<Double> m=NumericConvert.recognizeNumbersAdvanced(mvalue);
					if(m!=null){
						if(m.size()==2){
							if(mvalue.toLowerCase().contains("or")){
								jo.accumulate("Value",m.get(0));
								jo.accumulate("Extent",m.get(1));
								jo.accumulate("Op", "!bt");
							}else{
								jo.accumulate("Value",m.get(0));
								jo.accumulate("Extent",m.get(1));
								jo.accumulate("Op", "bt");
							}
						}else if(m.size()==1){
							System.out.println("1 number");
						if(((mvalue.indexOf(">")!=-1&&mvalue.indexOf("=")==-1)||(mvalue.indexOf("greater")!=-1)||(mvalue.indexOf("higher")!=-1))){
							System.out.println(">");
							jo.accumulate("Value",m.get(0));
							jo.accumulate("Op", "gt");
						}else if(((mvalue.indexOf("<")!=-1&&mvalue.indexOf("=")==-1)||(mvalue.indexOf("lower")!=-1)||(mvalue.indexOf("smaller")!=-1))){
							System.out.println("<");
							jo.accumulate("Value",m.get(0));
							jo.accumulate("Op", "lt");
						}else if((mvalue.indexOf("≥")!=-1)||((mvalue.indexOf("greater")!=-1)&&(mvalue.indexOf("equal")!=-1))||((mvalue.indexOf(">")!=-1)&&(mvalue.indexOf("=")!=-1))){
							System.out.println(">=");
							jo.accumulate("Value",m.get(0));
							jo.accumulate("Op", "gte");
						}else if((mvalue.indexOf("≤")!=-1)||((mvalue.indexOf("lower")!=-1)&&(mvalue.indexOf("equal")!=-1))||((mvalue.indexOf(">")!=-1)&&(mvalue.indexOf("=")!=-1))){//(mvalue.indexOf("≤")!=-1)||((mvalue.indexOf("greater")!=-1)&&(mvalue.indexOf("equal")!=-1))||((mvalue.indexOf(">")!=-1)&&(mvalue.indexOf("=")!=-1))
							System.out.println("<=");
							jo.accumulate("Value",m.get(0));
							jo.accumulate("Op", "lte");
						}
					}
				}
				}
				conceptsetid.accumulate("ValueAsNumber", jo);
			}
			classandconcept.accumulate("Measurement", conceptsetid);
			
		}else{
			return null;
		}
		return classandconcept;
	}

	public JSONObject translateTemporal(TemporalConstraint temporal) {
		JSONObject temporalwindow = new JSONObject();
		JSONObject start = new JSONObject();
		JSONObject end = new JSONObject();
		if (temporal == null) {
			start.accumulate("Coeff", -1);
			end.accumulate("Coeff", 1);
			temporalwindow.accumulate("Start", start);
			temporalwindow.accumulate("End", end);
		} else {
			if (temporal.getStart_days() != null && temporal.getEnd_days() != null) {
				start.accumulate("Days", temporal.getStart_days());
				start.accumulate("Coeff", temporal.getStart_offset());
				temporalwindow.accumulate("Start", start);
				end.accumulate("Days", temporal.getEnd_days());
				end.accumulate("Coeff", temporal.getEnd_offset());
				temporalwindow.accumulate("End", end);
			} else if (temporal.getStart_days() == null && temporal.getEnd_days() != null) {
				start.accumulate("Coeff", -1);
				temporalwindow.accumulate("Start", start);
				end.accumulate("Days", temporal.getEnd_days());
				end.accumulate("Coeff", temporal.getEnd_offset());
				temporalwindow.accumulate("End", end);
			} else if (temporal.getStart_days() != null && temporal.getEnd_days() == null) {
				start.accumulate("Days", temporal.getStart_days());
				start.accumulate("Coeff", temporal.getStart_offset());
				end.accumulate("Coeff", 1);
				temporalwindow.accumulate("Start", start);
				temporalwindow.accumulate("End", end);
			}
		}
		return temporalwindow;
	}

	@Override
	public Integer storeInATLAS(JSONObject expression,String cohortname) {
		// TODO Auto-generated method stub
		HashMap<String,String> cohortmap=new HashMap<String,String>();
		cohortmap.put("name", cohortname);
		cohortmap.put("expressionType", "SIMPLE_EXPRESSION");
		String jsonstr=expression.toString();
		System.out.println("jsonstr="+jsonstr);
		cohortmap.put("expression",jsonstr);
		String result=HttpUtil.doPost(cohorturl, JSONObject.fromObject(cohortmap).toString());
		JSONObject resultjson=JSONObject.fromObject(result);
		Integer cohortId=(Integer) resultjson.get("id");
		return cohortId;
	}
}
