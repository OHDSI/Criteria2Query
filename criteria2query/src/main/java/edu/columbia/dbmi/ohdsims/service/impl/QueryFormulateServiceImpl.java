package edu.columbia.dbmi.ohdsims.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import edu.columbia.dbmi.ohdsims.pojo.*;
import edu.columbia.dbmi.ohdsims.tool.ValueNormalization;
import net.sf.json.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.xpath.operations.Bool;
import org.springframework.stereotype.Service;

import edu.columbia.dbmi.ohdsims.service.IQueryFormulateService;
import edu.columbia.dbmi.ohdsims.tool.OHDSIApis;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import edu.columbia.dbmi.ohdsims.util.NumericConvert;
import edu.columbia.dbmi.ohdsims.util.TemporalNormalize;
import edu.stanford.nlp.util.Triple;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.json.Json;

import static edu.columbia.dbmi.ohdsims.util.SQLUtil.executeSQL;

@Service("qfService")
public class QueryFormulateServiceImpl implements IQueryFormulateService {
    final static public String cohorturl = GlobalSetting.ohdsi_api_base_url + "cohortdefinition/";
    ValueNormalization valueNormalization = new ValueNormalization();
    TemporalNormalize tn = new TemporalNormalize();

    @Override
    //Translate the document into a CdmCohort object, where terms with category "value", "temporal" have been normalized.
    public CdmCohort translateByDoc(Document doc) {
        CdmCohort cohort = new CdmCohort();
        cohort.setDaysAfter(doc.getInitial_event_constraint().getDaysAfter());
        cohort.setDaysBefore(doc.getInitial_event_constraint().getDaysBefore());
        cohort.setLimitTo(doc.getInitial_event_constraint().getLimitTo());
        OccurrenceStart os = new OccurrenceStart();
        os.setStart(doc.getInitial_event_constraint().getStartDate());
        os.setEnd(doc.getInitial_event_constraint().getEndDate());
        os.setOperator("bt");
        cohort.setOccurrenceStart(os);
        List<CdmCriteria> initial_event = new ArrayList<CdmCriteria>();
        List<CdmCriteria> additional_criteria = new ArrayList<CdmCriteria>();
        if (doc.getInitial_event() != null) {
            List<Paragraph> initial_ps = doc.getInitial_event();
            for (Paragraph p : initial_ps) {
                initial_event.addAll(translateByParagraph(p, true));
                //Translate each sentence in the paragraph into a CdmCriteria object,
                // and the paragraph is therefore translated into a CdmCrtiteria list.
                // The terms with category "value", ”temporal" have been normalized.
                // Add all the CdmCriteria objects into the initial_event.
            }
        }
        if (doc.getInclusion_criteria() != null) {
            List<Paragraph> inclusion = doc.getInclusion_criteria();
            for (Paragraph p : inclusion) {
                additional_criteria.addAll(translateByParagraph(p, true));
                //Translate each sentence in the paragraphs in Inclusion criteria into a CdmCriteria object,
                // and the paragraph is therefore translated into a CdmCrtiteria list.
                // The terms with category "value", ”temporal" have been normalized.
                // Add all the CdmCriteria objects into the additional_criteria.
            }
        }
        if (doc.getExclusion_criteria() != null) {
            List<Paragraph> exclusion = doc.getExclusion_criteria();
            for (Paragraph p : exclusion) {
                additional_criteria.addAll(translateByParagraph(p, false));
                //Translate each sentence in the paragraphs in Exclusion criteria into a CdmCriteria object,
                // and the paragraph is therefore translated into a CdmCrtiteria list.
                // The terms with category "value", ”temporal" have been normalized.
                // Add all the CdmCriteria objects into the additional_criteria.
            }
        }
        cohort.setInitial_event(initial_event);
        cohort.setAdditional_criteria(additional_criteria);
        return cohort;
    }

    @Override
    //Translate each sentence in the paragraph into a CdmCriteria object,
    // and the paragraph is therefore translated into a CdmCrtiteria list.
    // The terms with category "value", ”temporal" have been normalized.
    public List<CdmCriteria> translateByParagraph(Paragraph p, boolean include) {
        // TODO Auto-generated method stub
        List<CdmCriteria> cdmCriterias = new ArrayList<CdmCriteria>();
        List<Sentence> sents = p.getSents();
        if (sents != null) {
            for (int i = 0; i < sents.size(); i++) {
                Sentence s = sents.get(i);
                CdmCriteria c = translateBySentence(s, include);
                //Translate sentence into a CdmCriteria object
                //where the terms in the sentence have been translated into CdmCriterion object.
                //If the term is an temporal/value attribute, it has been normalized.
                c.setIncluded(include);
                cdmCriterias.add(c);

            }
        }
        return cdmCriterias;
    }

    @Override
    //Translate sentence into a CdmCriteria object
    //where the terms in the sentence have been translated into CdmCriterion object.
    //If the term is an temporal/value attribute, it has been normalized.
    public CdmCriteria translateBySentence(Sentence s, boolean include) {
        CdmCriteria cdmc = new CdmCriteria();
        cdmc.setText(s.getText());
        cdmc.setDesc(GlobalSetting.c2qversion);
        List<Term> terms = s.getTerms();
        List<Triple<Integer, Integer, String>> relations = s.getRelations();
        List<CdmCriterion> clist = new ArrayList<CdmCriterion>();
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
                        if (r.third.equals("has_temporal")) {
                            TemporalConstraint[] temporalwindow = tn.normalizeTemporal(findTermById(terms, r.second).getText());
                            //Get the "temporal" term by matching the termId and then get the its text
                            //Normalize the temporal attribute. Unify it to "day" unit and save its value into a TemporalConstraint object.
                            // There is a special case for the temporal attribute with text "current".

                            if (temporalwindow == null) {
                                continue;
                            }
                            cunit.setTemporalwindow(temporalwindow);
                        } else if (r.third.equals("has_value")) {
                            String valuestr = findTermById(terms, r.second).getText();
                            //Get the "value" term by matching the termId and then get the its text
                            Map<String, String> map = new HashMap<String, String>();
                            if (t.getCategorey().equals("Demographic") && t.getText().toLowerCase().contains("age")) {
                                map.put("age_range", valuestr);
                                System.err.println("value_str=" + valuestr);
                            } else if (t.getCategorey().equals("Measurement")) {
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

    //Get the term by matching the termId.
    public Term findTermById(List<Term> terms, Integer termId) {
        for (Term t : terms) {
            if (t.getTermId() == termId) {
                return t;
            }
        }
        return null;
    }

    //Normalize the temporal attribute. Unify it to "day" unit and save its value into a TemporalConstraint object.
    // There is a special case for the temporal attribute with text "current".
    public TemporalConstraint[] normalizeTemporal(String temporalplaintext) {
        boolean flagNeg = (" " + temporalplaintext.toLowerCase() + " ").matches(".*\\s(no|not)\\s.*");

        TemporalConstraint[] tc = new TemporalConstraint[2];
        tc[0] = new TemporalConstraint();
        tc[1] = new TemporalConstraint();
        TemporalNormalize tn = new TemporalNormalize();
        //Integer days = tn.temporalNormalizeforNumberUnit(temoralplaintext);//Normalize the temporal attribute. Unify it to the "day" unit.
        List<Integer> days = tn.temporalNormalizeforNumberUnit(temporalplaintext);
        if (temporalplaintext.matches(".*(within|after|from|since|next|subsequent).*") &&
                !temporalplaintext.matches(".*(previous|prior|past|last|before|preceding|former).*")) {
            //If the event start date is after the index start date
            if (days.size() == 1) {
                tc[0].setStart_days(0);
                tc[0].setStart_offset(-1);
                tc[0].setEnd_days(days.get(0));
                tc[0].setEnd_offset(1);
            } else if (days.size() == 2) {
                Collections.sort(days);
                tc[0].setStart_days(days.get(0));
                tc[0].setStart_offset(1);
                tc[0].setEnd_days(days.get(1));
                tc[0].setEnd_offset(1);//-1:before the index start date; 1: after the index start date
            }
        } else {
            //If the event start date is before the index start date,
            if (days.size() == 1) {
                tc[0].setStart_days(days.get(0));
                tc[0].setStart_offset(-1);
                tc[0].setEnd_days(0);
                tc[0].setEnd_offset(1);
            } else if (days.size() == 2) {
                Collections.sort(days, Collections.reverseOrder());
                tc[0].setStart_days(days.get(0));
                tc[0].setStart_offset(-1);
                tc[0].setEnd_days(days.get(1));
                tc[0].setEnd_offset(-1);//-1:before the index start date; 1: after the index start date
            }
        }
        if (temporalplaintext.toLowerCase().matches(".*(current|((by|at).*(screen|enrol))).*") ||
                temporalplaintext.toLowerCase().equals("currently")) {
            tc[0].setStart_days(180);
            tc[0].setStart_offset(-1);
            tc[0].setEnd_days(0);
            tc[0].setEnd_offset(1);//-1:before the index start date; 1: after the index start date
        }


        if (days.size() == 1 && days.get(0) < 10000) {
            //System.out.println(days);
            return tc;
        } else if (days.size() == 2 && days.get(1) < 10000) {
            return tc;
        } else {
            return null;
        }
    }

    //Extract all the concept IDs from Initial_event, Inclusion criteria, Exclusion criteria.
    public List<Integer> extractConceptsetByDoc(Document doc) {
        List<Integer> conceptSetIds = new ArrayList<Integer>();
        conceptSetIds.addAll(extractConceptsetByPargraphList(doc.getInitial_event()));//Extract all the concept IDs in the initial event.
        conceptSetIds.addAll(extractConceptsetByPargraphList(doc.getInclusion_criteria()));
        conceptSetIds.addAll(extractConceptsetByPargraphList(doc.getExclusion_criteria()));
        return conceptSetIds;
    }

    //Extract all the concept IDs from a list of paragraphs.
    public List<Integer> extractConceptsetByPargraphList(List<Paragraph> plist) {
        List<Integer> conceptSetIds = new ArrayList<Integer>();
        if (plist != null) {
            for (Paragraph p : plist) {
                if (p.getSents() != null) {
                    for (Sentence s : p.getSents()) {
                        if (s.getTerms() != null) {
                            for (Term t : s.getTerms()) {
                                if (Arrays.asList(GlobalSetting.conceptSetDomains).contains(t.getCategorey())) {
                                    System.out.println("add Concept SetId:" + t.getVocabularyId());
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
        //Translate the document into a CdmCohort object, where terms with category "value", "temporal" have been normalized.
        CdmCohort omopcohort = translateByDoc(doc);

        JSONObject cohort = new JSONObject();
        //Merge All criteria to one criteria; Merge all CdmCriterion objects translated from terms into a list.
        CdmCriteria initial_criteria = new CdmCriteria();
        List<CdmCriterion> clist = new ArrayList<CdmCriterion>();
        for (CdmCriteria c : omopcohort.getInitial_event()) {//c is in sentence level
            if (c != null) {
                clist.addAll(c.getClist());//Clist is in term level; A term has been translated into a CdmCriterion object.
            }
        }
        StringBuffer initialeventtext = new StringBuffer();
        for (CdmCriteria cdmc : omopcohort.getInitial_event()) {//cdmc is in sentence level
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
        cohort.accumulate("ConceptSets", formulateConceptSetbyConcept(doc));
        // initial events
        cohort.accumulate("PrimaryCriteria", formulatePrimaryCriteria(initial_criteria));
        //Formulate a JSONObject which represents an initial event.
        if (initial_criteria.getText() != null && initial_criteria.getText().length() > 0) {
            System.out.println("=>additional criteria");
            cohort.accumulate("AdditionalCriteria", formulateOneCdmCriteria(initial_criteria));
        }
        cohort.accumulate("QualifiedLimit", formulateQualifiedLimit());
        cohort.accumulate("ExpressionLimit", formulateExpressionLimit());
        // other criteria
        //omopcohort.Additional_criteria contains CdmCriteria objects translated from sentences in the inclusion criteria and exclusion criteria.
        //Translate logic relations "AND" and "OR" to logic expressions in the target CDM's cohort definition format.
        //Translate a list of CdmCriteria to JSONArray.
        cohort.accumulate("InclusionRules", formulateInclusionRules(omopcohort.getAdditional_criteria()));
        // set null for CensoringCriteria and CollapseSettings
        cohort.accumulate("CensoringCriteria", formulateCensoringCriteria());
        cohort.accumulate("CollapseSettings", formulateCollapseSettings());
        return cohort;
    }


    //Get the information about each concept set from the conceptseturl with its conceptSetID in a JSONObject format, and formulate a JSONArray for all the concepts.
    public JSONArray formulateConceptSet(List<Integer> conceptsetIds) {
        JSONArray conceptsetarr = new JSONArray();
        System.out.println("conceptId size=>" + conceptsetIds.size());
        System.out.println("conceptId =>" + conceptsetIds.get(0));

        for (Integer i : conceptsetIds) {
            if (i != null) {
                System.out.println("conceptId=>" + i);
                conceptsetarr.add(OHDSIApis.querybyconceptSetid(i));
                //Get a JSONObject with keys createdBy, modifiedBy, createdDate, modifiedDate, id, name, expression
                // by making Get requests to the conceptseturl with a conceptSetID. Then add it to the conceptsetarr JSONArray.
            }
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

    //Formulate a JSONObject which represents an initial event.
    public JSONObject formulatePrimaryCriteria(CdmCriteria cdmc) {
        JSONObject primarycriteria = new JSONObject();
        JSONObject observationWindow = new JSONObject();
        if (cdmc != null && cdmc.getClist() != null && hasEvents(cdmc)) {
            JSONArray criterialist = formualtePrimaryCriteriaList(cdmc);
            //Formulate a JSONArray containing the JSONObjects each of which represents the CdmCriterion object of a term
            // with different keys according to its category/domain("Condition", "Drug", "Observation", "Procedure", "Measurement") and the reconstruction of the "measure_value" type of "attributes" in the CdmCriterion object.
            primarycriteria.accumulate("CriteriaList", criterialist);
            observationWindow.accumulate("PriorDays", cdmc.getPriorDays());
            observationWindow.accumulate("PostDays", cdmc.getPostDays());
            primarycriteria.accumulate("ObservationWindow", observationWindow);
            JSONObject primaryCriteriaLimit = new JSONObject();
            primaryCriteriaLimit.accumulate("Type", cdmc.getLimitTo());
            primarycriteria.accumulate("PrimaryCriteriaLimit", primaryCriteriaLimit);
        } else {
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

    //Check if there exists a CdmCriterion object(translated from a term) which is in one of the domains"Condition","Observation","Measurement","Drug","Procedure", "Device".
    public boolean hasEvents(CdmCriteria cdmc) {
        if (cdmc != null) {
            for (CdmCriterion c : cdmc.getClist()) {
                if (Arrays.asList(GlobalSetting.conceptSetDomains).contains(c.getDomain())) {
                    return true;
                }
            }
        }
        return false;
    }

    public JSONArray defaultInitialEvent() {
        JSONArray jacriterialist = new JSONArray();
        JSONObject jo = new JSONObject();
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
     */
    public JSONArray formulateInclusionRules(List<CdmCriteria> clist) {
        JSONArray json_arr = new JSONArray();
        for (int i = 0; i < clist.size(); i++) {//Traverse each CdmCriteria object(translated from a sentence)
            JSONObject jo = new JSONObject();
            CdmCriteria cdmc = clist.get(i);
            System.out.println("is include?" + cdmc.isIncluded());
            String inctag = cdmc.isIncluded() ? "[INC]" : "[EXC]";
            jo.accumulate("name", inctag + cdmc.getText());
            jo.accumulate("description", cdmc.getDesc());
            JSONObject expression = formulateOneCdmCriteria(cdmc);
            System.out.println(expression);
            jo.accumulate("expression", expression);
            json_arr.add(jo);

        }
        return json_arr;
    }


    //Translate logic relations "AND" and "OR" to logic expressions in the target CDM's cohort definition format.
    //Formulate JSONObject from the criteria(translated from a sentence) according to its inclusion, temporal windows,
    //and the CdmCriterion Objects(translated from terms) with "demographic" domain(category).
    public JSONObject formulateOneCdmCriteria(CdmCriteria cdmc) {
        JSONObject expression = new JSONObject();
        if (cdmc.getLogic_groups() != null && cdmc.getLogic_groups().size() > 0) {
            boolean contain_or = false;
            for (LinkedHashSet<Integer> elem : cdmc.getLogic_groups()) {
                if (elem.size()>1) {
                    contain_or = true;
                    break;
                }
            }
            if (contain_or && cdmc.isIncluded()) {
                cdmc.setType("ANY");
            }
        }

        expression.accumulate("Type", cdmc.getType());
        Pair<JSONArray, JSONArray> pair = translateIncludeRules2Json(cdmc.getClist(), cdmc.isNeg(), cdmc.isIncluded());
        JSONArray criteriaList = pair.getKey();
        JSONArray groups = pair.getValue();
        expression.accumulate("CriteriaList", criteriaList);
        pair = formulateDemographic(cdmc.getClist(), cdmc.isIncluded());
        JSONArray demographicCriteriaList = pair.getKey();
        groups.addAll(pair.getValue());
        expression.accumulate("DemographicCriteriaList", demographicCriteriaList);
        expression.accumulate("Groups", groups);
        return expression;
    }

    // inclusion
    //Logic Translation
    public Pair<JSONArray, JSONArray> translateIncludeRules2Json(List<CdmCriterion> criteria_list, boolean paragraphneg,
                                                                 boolean included) {
        JSONArray criterialist = new JSONArray();
        JSONArray groups = new JSONArray();
        for (CdmCriterion cdmcriterion : criteria_list) {
            if (Arrays.asList(GlobalSetting.conceptSetDomains).contains(cdmcriterion.getDomain())) {
                Pair<Boolean, JSONArray> pair = formualteCriteria(cdmcriterion);
                JSONArray classConceptArray = pair.getValue();
                Boolean flagOr = pair.getKey();
                JSONArray tempArray = new JSONArray();
                Boolean typeNegFlag = cdmcriterion.isNeg() == included;
                for (int i = 0; i < classConceptArray.size(); i++) {
                    JSONObject jo = classConceptArray.getJSONObject(i);
                    JSONObject criterion = new JSONObject();
                    criterion.accumulate("Criteria", jo);
                    // Temporal windows
                    TemporalConstraint[] tcarr = cdmcriterion.getTemporalwindow();
                    if (tcarr == null) {
                        criterion.accumulate("StartWindow", translateTemporal(null));
                        criterion.accumulate("EndWindow", translateTemporal(null));
                    } else {
                        if (tcarr[0] != null) {
                            criterion.accumulate("StartWindow", translateTemporal(tcarr[0]));
                        }
                        if (tcarr[1] != null) {
                            criterion.accumulate("EndWindow", translateTemporal(tcarr[1]));
                        }
                    }
                    if (typeNegFlag) {
                        JSONObject occurrence = new JSONObject();
                        occurrence.accumulate("Type", 0);
                        occurrence.accumulate("Count", 0);
                        criterion.accumulate("Occurrence", occurrence);
                    } else {
                        JSONObject occurrence = new JSONObject();
                        occurrence.accumulate("Type", 2);
                        occurrence.accumulate("Count", 1);
                        criterion.accumulate("Occurrence", occurrence);
                    }
                    tempArray.add(criterion);
//                    criterialist.add(criterion);
                }
                if (tempArray.size() > 1 && (flagOr == typeNegFlag)) {
                    JSONObject groupObj = new JSONObject();
                    groupObj.accumulate("Type", "ALL");
                    JSONArray janull = new JSONArray();
                    groupObj.accumulate("CriteriaList", tempArray);
                    groupObj.accumulate("DemographicCriteriaList", janull);
                    groupObj.accumulate("Groups", janull);
                    groups.add(groupObj);
                } else if (tempArray.size() > 1 && !(flagOr == typeNegFlag)) {
                    JSONObject groupObj = new JSONObject();
                    groupObj.accumulate("Type", "ANY");
                    JSONArray janull = new JSONArray();
                    groupObj.accumulate("CriteriaList", tempArray);
                    groupObj.accumulate("DemographicCriteriaList", janull);
                    groupObj.accumulate("Groups", janull);
                    groups.add(groupObj);
                } else {
                    criterialist.addAll(tempArray);
                }

            }
        }
        return Pair.of(criterialist, groups);
    }

    public Pair<JSONArray, JSONArray> formulateDemographic(List<CdmCriterion> criteria_list, boolean included) {
        ArrayList<Demographic> ageList = new ArrayList<>();
        ArrayList<Demographic> genderList = new ArrayList<>();
        ArrayList<Demographic> ageGenderList = new ArrayList<>();
        int j = 0;
        for (CdmCriterion cdmcriterion : criteria_list) {
            if (cdmcriterion.getDomain().equals("Demographic")) {
                if (cdmcriterion.getOrginialtext().toLowerCase().contains("age") && cdmcriterion.getAttributes() != null) {
                    Demographic age = new Demographic();
                    age.setId(j);
                    age.setText(cdmcriterion.getAttributes().get("age_range"));
                    age.setCriterion(cdmcriterion);
                    age.setType("age");
                    ageList.add(age);
                    ageGenderList.add(age);
                    j++;
                } else if (StringUtils.indexOfAny(" " + cdmcriterion.getOrginialtext().toLowerCase() + " ", GenderGroup.maletriggers) != -1 ||
                        StringUtils.indexOfAny(" " + cdmcriterion.getOrginialtext().trim().toLowerCase() + " ", GenderGroup.femaletriggers) != -1) {
                    Demographic gender = new Demographic();
                    gender.setId(j);
                    gender.setText(cdmcriterion.getOrginialtext().trim().toLowerCase());
                    gender.setCriterion(cdmcriterion);
                    gender.setType("gender");
                    genderList.add(gender);
                    ageGenderList.add(gender);
                    j++;
                }
            }
        }
        if (genderList.size() == 0) {
            JSONArray groups = new JSONArray();
            JSONArray demographicCriteriaList = new JSONArray();
            for (Demographic age : ageList) {
                Pair<Boolean, JSONArray> pair = valueNormalization.recognizeValueAndOp(age.getText());
                JSONArray ja = pair.getValue();
                Boolean flagOr = pair.getKey();
                JSONArray demographicarr = new JSONArray();
                for (int i = 0; i < ja.size(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    if (!included) {
                        jo = valueNormalization.changeOp(jo);
                    }
                    if (!jo.isEmpty()) {
                        JSONObject agejson = new JSONObject();
                        agejson.accumulate("Age", jo);
                        demographicarr.add(agejson);
                    }
                }
                if (ja.size() > 1 && ((flagOr && included) || (!flagOr && !included))) {
                    //If logic between values is "OR"
                    JSONObject groupObj = new JSONObject();
                    groupObj.accumulate("Type", "ANY");
                    JSONArray janull = new JSONArray();
                    groupObj.accumulate("CriteriaList", janull);
                    groupObj.accumulate("DemographicCriteriaList", demographicarr);
                    groupObj.accumulate("Groups", janull);
                    groups.add(groupObj);
                } else {
                    demographicCriteriaList.addAll(demographicarr);
                }
            }
            if (!included) {
                JSONObject groupObj = new JSONObject();
                groupObj.accumulate("Type", "ANY");
                JSONArray janull = new JSONArray();
                groupObj.accumulate("CriteriaList", janull);
                groupObj.accumulate("DemographicCriteriaList", demographicCriteriaList);
                groupObj.accumulate("Groups", janull);
                groups.add(groupObj);
                demographicCriteriaList = new JSONArray();
            }
            return Pair.of(demographicCriteriaList, groups);
        } else if (ageList.size() == 0) {
            JSONArray groups = new JSONArray();
            JSONArray demographicCriteriaList = new JSONArray();
            for (Demographic gender : genderList) {
                JSONArray demographicarr = new JSONArray();
                if ((StringUtils.indexOfAny(" " + gender.getText() + " ", GenderGroup.maletriggers) != -1 && included) ||
                        (StringUtils.indexOfAny(" " + gender.getText() + " ", GenderGroup.femaletriggers) != -1 && !included)) {
                    JSONArray genderarr = new JSONArray();
                    JSONObject genderjson = new JSONObject();
                    genderarr.add(setMale());
                    genderjson.accumulate("Gender", genderarr);
                    demographicarr.add(genderjson);
                }
                if ((StringUtils.indexOfAny(" " + gender.getText() + " ", GenderGroup.femaletriggers) != -1 && included) ||
                        (StringUtils.indexOfAny(" " + gender.getText() + " ", GenderGroup.maletriggers) != -1 && !included)) {
                    JSONArray genderarr = new JSONArray();
                    JSONObject genderjson = new JSONObject();
                    genderarr.add(setFeMale());
                    genderjson.accumulate("Gender", genderarr);
                    demographicarr.add(genderjson);
                }

                if (demographicarr.size() > 1 && included) {
                    JSONObject groupObj = new JSONObject();
                    groupObj.accumulate("Type", "ANY");
                    JSONArray janull = new JSONArray();
                    groupObj.accumulate("CriteriaList", janull);
                    groupObj.accumulate("DemographicCriteriaList", demographicarr);
                    groupObj.accumulate("Groups", janull);
                    groups.add(groupObj);
                } else if (demographicarr.size() > 1 && !included) {
                    JSONObject groupObj = new JSONObject();
                    groupObj.accumulate("Type", "ALL");
                    JSONArray janull = new JSONArray();
                    groupObj.accumulate("CriteriaList", janull);
                    groupObj.accumulate("DemographicCriteriaList", demographicarr);
                    groupObj.accumulate("Groups", janull);
                    groups.add(groupObj);
                } else {
                    demographicCriteriaList.add(demographicarr.get(0));
                }
            }
            return Pair.of(demographicCriteriaList, groups);
        } else if (genderList.size() == 1 && ageList.size() > 0) {
            JSONArray outerGroups = new JSONArray();
            JSONArray groups = new JSONArray();
            JSONArray demographicCriteriaList = new JSONArray();
            JSONObject outerGroup = new JSONObject();
            outerGroup.accumulate("Type", "ALL");
            JSONArray janull = new JSONArray();
            outerGroup.accumulate("CriteriaList", janull);
            List<CdmCriterion> gender1List = new ArrayList<>();
            gender1List.add(genderList.get(0).getCriterion());
            Pair<JSONArray, JSONArray> pair = formulateDemographic(gender1List, true);
            demographicCriteriaList.addAll(pair.getKey());
            groups.addAll(pair.getValue());
            List<CdmCriterion> age1List = new ArrayList<>();
            for (Demographic age : ageList) {
                age1List.add(age.getCriterion());
            }
            pair = formulateDemographic(age1List, included);
            demographicCriteriaList.addAll(pair.getKey());
            groups.addAll(pair.getValue());
            outerGroup.accumulate("DemographicCriteriaList", demographicCriteriaList);
            outerGroup.accumulate("Groups", groups);
            outerGroups.add(outerGroup);
            if (!included) {
                JSONArray outerOuterGroups = new JSONArray();
                JSONObject outerOuterGroup = new JSONObject();
                outerOuterGroup.accumulate("Type", "ANY");
                outerOuterGroup.accumulate("CriteriaList", janull);
                JSONArray demographicarr = new JSONArray();
                JSONArray genderarr = new JSONArray();
                JSONObject genderjson = new JSONObject();
                if (StringUtils.indexOfAny(" " + genderList.get(0).getText() + " ", GenderGroup.femaletriggers) != -1) {
                    genderarr.add(setMale());
                } else {
                    genderarr.add(setFeMale());
                }
                genderjson.accumulate("Gender", genderarr);
                demographicarr.add(genderjson);
                outerOuterGroup.accumulate("DemographicCriteriaList", demographicarr);
                outerOuterGroup.accumulate("Groups", outerGroups);
                outerOuterGroups.add(outerOuterGroup);
                return Pair.of(new JSONArray(), outerOuterGroups);

            }
            return Pair.of(new JSONArray(), outerGroups);
        } else if (genderList.size() == 2 && ageList.size() == 1) {
            JSONArray groups = new JSONArray();
            JSONArray demographicCriteriaList = new JSONArray();
            JSONArray outerGroups = new JSONArray();

            for (Demographic gender : genderList) {
                List<CdmCriterion> gender1Age1List = new ArrayList<>();
                gender1Age1List.add(gender.getCriterion());
                gender1Age1List.add(ageList.get(0).getCriterion());
                Pair<JSONArray, JSONArray> pair = formulateDemographic(gender1Age1List, included);
                demographicCriteriaList.addAll(pair.getKey());
                groups.addAll(pair.getValue());
                if (!included) {
                    JSONObject outerGroup = new JSONObject();
                    outerGroup.accumulate("Type", "ALL");
                    JSONArray janull = new JSONArray();
                    outerGroup.accumulate("CriteriaList", janull);
                    outerGroup.accumulate("DemographicCriteriaList", demographicCriteriaList);
                    outerGroup.accumulate("Groups", groups);
                    outerGroups.add(outerGroup);
                    groups = new JSONArray();
                    demographicCriteriaList = new JSONArray();
                }
            }
            if (!included) {
                return Pair.of(new JSONArray(), outerGroups);
            } else {
                return Pair.of(demographicCriteriaList, groups);
            }
        } else {
            JSONArray groups = new JSONArray();
            JSONArray demographicCriteriaList = new JSONArray();
            JSONArray outerGroups = new JSONArray();
            int k1 = 0;
            int k2 = 1;
            Pair<JSONArray, JSONArray> pair;
            List<CdmCriterion> subList = new ArrayList<>();
            if (ageGenderList.get(0).getType().equals("age")) {
                Collections.reverse(ageGenderList);
            }
            while (k1 < ageGenderList.size()) {
                Demographic demoItem1 = ageGenderList.get(k1);
                if (demoItem1.getType().equals("gender")) {
                    subList.add(demoItem1.getCriterion());
                    Demographic demoItem2 = ageGenderList.get(k2);
                    while (demoItem2.getType().equals("age")) {
                        subList.add(demoItem2.getCriterion());
                        k2++;
                        if (k2 == ageGenderList.size()) {
                            break;
                        }
                        demoItem2 = ageGenderList.get(k2);
                    }
                    pair = formulateDemographic(subList, included);
                    demographicCriteriaList.addAll(pair.getKey());
                    groups.addAll(pair.getValue());
                    k1 = k2;
                    k2 = k1 + 1;
                    subList = new ArrayList<>();
                    if (k2 == ageGenderList.size()) {
                        demoItem1 = ageGenderList.get(k1);
                        subList.add(demoItem1.getCriterion());
                        pair = formulateDemographic(subList, included);
                        demographicCriteriaList.addAll(pair.getKey());
                        groups.addAll(pair.getValue());
                        break;
                    }
                } else {
                    subList.add(demoItem1.getCriterion());
                    k1++;
                    k2++;
                    if (k1 == ageGenderList.size()) {
                        pair = formulateDemographic(subList, included);
                        demographicCriteriaList.addAll(pair.getKey());
                        groups.addAll(pair.getValue());
                    }
                }
            }


            return Pair.of(demographicCriteriaList, groups);
        }

    }

    public JSONObject setMale() {
        JSONObject jo = new JSONObject();
        jo.accumulate("CONCEPT_CODE", "M");
        jo.accumulate("CONCEPT_ID", 8507);
        jo.accumulate("CONCEPT_NAME", "MALE");
        jo.accumulate("DOMAIN_ID", "Gender");
        jo.accumulate("VOCABULARY_ID", "Gender");
        return jo;
    }

    public JSONObject setFeMale() {
        JSONObject jo = new JSONObject();
        jo.accumulate("CONCEPT_CODE", "F");
        jo.accumulate("CONCEPT_ID", 8532);
        jo.accumulate("CONCEPT_NAME", "FEMALE");
        jo.accumulate("DOMAIN_ID", "Gender");
        jo.accumulate("VOCABULARY_ID", "Gender");
        return jo;
    }

    //Formulate a JSONArray containing the JSONObjects each of which represents the CdmCriterion object of a term
    // with different keys according to its category/domain and the reconstruction of the "measure_value" type of "attributes" in the CdmCriterion object.
    public JSONArray formualtePrimaryCriteriaList(CdmCriteria cdmc) {

        List<CdmCriterion> criteria_list = cdmc.getClist();//A list of CdmCriterion which is translated from a list of terms.
        JSONArray criterialist = new JSONArray();

        for (CdmCriterion cdmcriterion : criteria_list) {
            cdmcriterion.setOccurenceStart(cdmc.getOccurenceStart());
            Pair<Boolean, JSONArray> pair = formualtePrimaryCriteriaInInitialEvent2(cdmcriterion);
            JSONArray classConceptArray = pair.getValue();
            for (int i = 0; i < classConceptArray.size(); i++) {
                //Formulate a JSONObject which represents the CdmCriterion object of a term
                // with different keys according to its category/domain and the reconstruction of the "measure_value" type of "attributes" in the CdmCriterion object.
                JSONObject classandconcept = classConceptArray.getJSONObject(i);
                if (classandconcept != null) {
                    criterialist.add(classandconcept);
                }
            }
        }
        return criterialist;
    }


    //Formulate JSONObject for the CdmCriterion object translated from a term.
    public Pair<Boolean, JSONArray> formualteCriteria(CdmCriterion cdmcriterion) {
        JSONObject conceptsetid = new JSONObject();
        JSONObject classandconcept = new JSONObject();
        JSONArray classConceptArray = new JSONArray();
        Boolean flagOr = false;
        if (cdmcriterion.getDomain().equals("Condition")) {
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            classandconcept.accumulate("ConditionOccurrence", conceptsetid);
            classConceptArray.add(classandconcept);
        } else if (cdmcriterion.getDomain().equals("Drug")) {
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            classandconcept.accumulate("DrugExposure", conceptsetid);
            classConceptArray.add(classandconcept);
        } else if (cdmcriterion.getDomain().equals("Observation")) {
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            classandconcept.accumulate("Observation", conceptsetid);
            classConceptArray.add(classandconcept);
        } else if (cdmcriterion.getDomain().equals("Procedure")) {
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            classandconcept.accumulate("ProcedureOccurrence", conceptsetid);
            classConceptArray.add(classandconcept);
        } else if (cdmcriterion.getDomain().equals("Device")) {
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            classandconcept.accumulate("DeviceExposure", conceptsetid);
            classConceptArray.add(classandconcept);
        } else if (cdmcriterion.getDomain().equals("Measurement")) {
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            Map<String, String> attributes = cdmcriterion.getAttributes();
            if (attributes != null) {
                String mvalue = attributes.get("measure_value");
                if (mvalue != null) {
                    Pair<Boolean, JSONArray> pair = valueNormalization.recognizeValueAndOp(mvalue);
                    JSONArray ja = pair.getValue();
                    flagOr = pair.getKey();
                    for (int i = 0; i < ja.size(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        if (!jo.isEmpty()) {
                            JSONObject ci = new JSONObject();
                            ci.accumulate("CodesetId", cdmcriterion.getConceptsetId());
                            ci.accumulate("ValueAsNumber", jo);
                            JSONObject cc = new JSONObject();
                            cc.accumulate("Measurement", ci);
                            classConceptArray.add(cc);
                        }

                    }

                }

            } else {
                classandconcept.accumulate("Measurement", conceptsetid);
                classConceptArray.add(classandconcept);
            }
        }
        return Pair.of(flagOr, classConceptArray);
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
            } else {
                start.accumulate("Coeff", -1);
                end.accumulate("Coeff", 1);
                temporalwindow.accumulate("Start", start);
                temporalwindow.accumulate("End", end);
            }
        }
        return temporalwindow;
    }

    @Override
    public Integer storeInATLAS(JSONObject expression, String cohortname) {
        // TODO Auto-generated method stub

        JSONObject cohortdef = new JSONObject();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS");
        String date = df.format(new Date());

        cohortdef.put("name", cohortname + date);
        cohortdef.put("expressionType", "SIMPLE_EXPRESSION");
        String jsonstr = expression.toString();
        System.out.println("jsonstr=" + jsonstr);
        cohortdef.put("expression", jsonstr);
        String result = HttpUtil.doPost(cohorturl, cohortdef.toString());
        System.out.println("cohort=" + cohortdef.toString());
        JSONObject resultjson = JSONObject.fromObject(result);
        Integer cohortId = (Integer) resultjson.get("id");
        return cohortId;
    }

    //Execute the SQL script and return the result.
    public JSONArray generateReport(String query, String dataset) {
        JSONArray result = executeSQL(query, dataset);
        return result;

    }

    //Formulate a conceptSets array containing all concept sets in the document.
    public JSONArray formulateConceptSetbyConcept(Document doc) {
        JSONArray conceptSets = new JSONArray();
        Set<Integer> conceptSetIds = new HashSet<>();
        conceptSets = formulateConceptSetbyParagraphList(doc.getInitial_event(), conceptSets, conceptSetIds);
        conceptSets = formulateConceptSetbyParagraphList(doc.getInclusion_criteria(), conceptSets, conceptSetIds);
        conceptSets = formulateConceptSetbyParagraphList(doc.getExclusion_criteria(), conceptSets, conceptSetIds);
        return conceptSets;
    }

    //Formulate a conceptSets array containing all concept sets in the initial event or inclusion criteria or exclusion criteria.
    public JSONArray formulateConceptSetbyParagraphList(List<Paragraph> pas, JSONArray conceptSets, Set<Integer> conceptSetIds) {
        if (pas != null) {
            for (Paragraph pa : pas) {
                if (pa.getSents() != null) {
                    for (Sentence s : pa.getSents()) {
                        if (s.getTerms() != null) {
                            for (Term term : s.getTerms()) {
                                boolean contain = conceptSetIds.contains(term.getVocabularyId());
                                if (term.getConceptId() != null && !contain) {
                                    JSONObject concept = new JSONObject();
                                    concept.accumulate("CONCEPT_ID", term.getConceptId());
                                    JSONObject item = new JSONObject();
                                    item.accumulate("concept", concept);
                                    item.accumulate("isExcluded", false);
                                    item.accumulate("includeDescendants", true);
                                    item.accumulate("includeMapped", false);
                                    JSONArray items = new JSONArray();
                                    items.add(item);
                                    JSONObject expression = new JSONObject();
                                    expression.accumulate("items", items);
                                    JSONObject conceptSet = new JSONObject();
                                    conceptSet.accumulate("id", term.getVocabularyId());
                                    conceptSet.accumulate("name", term.getText());
                                    conceptSet.accumulate("expression", expression);
                                    conceptSets.add(conceptSet);
                                    conceptSetIds.add(term.getVocabularyId());
                                }
                            }
                        }
                    }
                }
            }
        }
        return conceptSets;
    }

    public Pair<Boolean, JSONArray> formualtePrimaryCriteriaInInitialEvent2(CdmCriterion cdmcriterion) {
        JSONObject conceptsetid = new JSONObject();
        JSONObject classandconcept = new JSONObject();
        JSONObject occursStartContent = new JSONObject();
        JSONObject occursEndContent = new JSONObject();
        JSONArray classConceptArray = new JSONArray();
        Boolean flagOr = false;
        if (cdmcriterion.getOccurenceStart().getEnd() != null) {
            occursStartContent.accumulate("Value", cdmcriterion.getOccurenceStart().getEnd());
            occursStartContent.accumulate("Op", "lte");
        }
        if (cdmcriterion.getOccurenceStart().getStart() != null) {
            occursEndContent.accumulate("Value", cdmcriterion.getOccurenceStart().getStart());
            occursEndContent.accumulate("Op", "gte");
        }


        if (cdmcriterion.getDomain().equals("Condition")) {// Condition
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            if (occursStartContent.containsKey("Value")) {
                conceptsetid.accumulate("OccurrenceStartDate", occursStartContent);
            }
            if (occursEndContent.containsKey("Value")) {
                conceptsetid.accumulate("OccurrenceEndDate", occursEndContent);
            }
            classandconcept.accumulate("ConditionOccurrence", conceptsetid);
            classConceptArray.add(classandconcept);

        } else if (cdmcriterion.getDomain().equals("Drug")) {
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            if (occursStartContent.containsKey("Value")) {
                conceptsetid.accumulate("OccurrenceStartDate", occursStartContent);
            }
            if (occursEndContent.containsKey("Value")) {
                conceptsetid.accumulate("OccurrenceEndDate", occursEndContent);
            }
            classandconcept.accumulate("DrugExposure", conceptsetid);
            classConceptArray.add(classandconcept);
        } else if (cdmcriterion.getDomain().equals("Observation")) {
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            if (occursStartContent.containsKey("Value")) {
                conceptsetid.accumulate("OccurrenceStartDate", occursStartContent);
            }
            if (occursEndContent.containsKey("Value")) {
                conceptsetid.accumulate("OccurrenceEndDate", occursEndContent);
            }
            classandconcept.accumulate("Observation", conceptsetid);
            classConceptArray.add(classandconcept);

        } else if (cdmcriterion.getDomain().equals("Procedure")) {
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            if (occursStartContent.containsKey("Value")) {
                conceptsetid.accumulate("OccurrenceStartDate", occursStartContent);
            }
            if (occursEndContent.containsKey("Value")) {
                conceptsetid.accumulate("OccurrenceEndDate", occursEndContent);
            }
            classandconcept.accumulate("ProcedureOccurrence", conceptsetid);
            classConceptArray.add(classandconcept);
        } else if (cdmcriterion.getDomain().equals("Device")) {
            conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
            if (occursStartContent.containsKey("Value")) {
                conceptsetid.accumulate("OccurrenceStartDate", occursStartContent);
            }
            if (occursEndContent.containsKey("Value")) {
                conceptsetid.accumulate("OccurrenceEndDate", occursEndContent);
            }
            classandconcept.accumulate("DeviceExposure", conceptsetid);
            classConceptArray.add(classandconcept);
        } else if (cdmcriterion.getDomain().equals("Measurement")) {
            Map<String, String> attributes = cdmcriterion.getAttributes();
            if (attributes != null) {
                String mvalue = attributes.get("measure_value");
                System.out.println("mvalut=" + mvalue);
                if (mvalue != null) {
                    Pair<Boolean, JSONArray> pair = valueNormalization.recognizeValueAndOp(mvalue);
                    JSONArray ja = pair.getValue();
                    flagOr = pair.getKey();
                    for (int i = 0; i < ja.size(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        if (!jo.isEmpty()) {
                            JSONObject ci = new JSONObject();
                            ci.accumulate("CodesetId", cdmcriterion.getConceptsetId());
                            if (occursStartContent.containsKey("Value")) {
                                ci.accumulate("OccurrenceStartDate", occursStartContent);
                            }
                            if (occursEndContent.containsKey("Value")) {
                                ci.accumulate("OccurrenceEndDate", occursEndContent);
                            }
                            ci.accumulate("ValueAsNumber", jo);
                            JSONObject cc = new JSONObject();
                            cc.accumulate("Measurement", ci);
                            classConceptArray.add(cc);
                        }
                    }

                }
            } else {
                conceptsetid.accumulate("CodesetId", cdmcriterion.getConceptsetId());
                if (occursStartContent.containsKey("Value")) {
                    conceptsetid.accumulate("OccurrenceStartDate", occursStartContent);
                }
                if (occursEndContent.containsKey("Value")) {
                    conceptsetid.accumulate("OccurrenceEndDate", occursEndContent);
                }
                classandconcept.accumulate("Measurement", conceptsetid);
                classConceptArray.add(classandconcept);
            }
        }

        return Pair.of(flagOr, classConceptArray);
    }

}
