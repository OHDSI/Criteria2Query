package edu.columbia.dbmi.ohdsims.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.alibaba.fastjson.JSON;
import edu.columbia.dbmi.ohdsims.pojo.*;
import edu.columbia.dbmi.ohdsims.tool.JSON2SQL;
import edu.columbia.dbmi.ohdsims.util.FileUtil;
import edu.columbia.dbmi.ohdsims.util.HttpUtil;
import net.sf.json.JSONArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.transform.SourceURIASTTransformation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.columbia.dbmi.ohdsims.service.IConceptFilteringService;
import edu.columbia.dbmi.ohdsims.service.IConceptMappingService;
import edu.columbia.dbmi.ohdsims.service.IInformationExtractionService;
import edu.columbia.dbmi.ohdsims.service.IQueryFormulateService;
import edu.stanford.nlp.util.Triple;
import net.sf.json.JSONObject;

import static edu.columbia.dbmi.ohdsims.pojo.GlobalSetting.ohdsi_api_base_url;

@Controller
@RequestMapping("/main")
public class MainController {
    private Logger logger = LogManager.getLogger(MainController.class);
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
    public Map<String, Object> runPipeLine(HttpSession httpSession, HttpServletRequest request, String nctid, String initialevent, String inc,
                                           String exc, boolean abb, String obstart, String obend, String daysbefore, String daysafter, String limitto) {
        Document doc = this.ieService.translateByDoc(initialevent, inc, exc);//Parse the document.
        doc = this.ieService.patchIEResults(doc);//Add "Demographic" term together with the "has_value" relation.
        if (abb == true) {
            doc = this.ieService.abbrExtensionByDoc(doc);//Extend the abbreviation.
        }
        List<ConceptSet> allsts = this.conceptMappingService.getAllConceptSets();//Get pre-existing Concept sets in a ConceptSet list format from http://api.ohdsi.org/WebAPI/conceptset/
        List<Term> terms = this.conceptMappingService.getDistinctTerm(doc);////Get the term list which only contains distinct terms with category: "Condition","Observation","Measurement","Drug","Procedure"
        Map<String, Integer> conceptSetIds = this.conceptMappingService.createConceptsByTerms(allsts, terms);//Get a map whose keys are the names of entities, and the values are the matched concept set IDs.
        doc = this.conceptMappingService.linkConceptSetsToTerms(doc, conceptSetIds);//Update the vocabularyId attribute of each term with the corresponding concept set Id.
        ObservationConstraint oc = new ObservationConstraint();
        oc.setDaysAfter(Integer.valueOf(daysafter));
        oc.setDaysBefore(Integer.valueOf(daysbefore));
        oc.setLimitTo(limitto);


        if (obstart.length() > 0) {
            oc.setStartDate(obstart);
        } else {
            oc.setStartDate(null);
        }
        if (obend.length() > 0) {
            oc.setEndDate(obend);
        } else {
            oc.setEndDate(null);
        }
        doc.setInitial_event_constraint(oc);
        httpSession.setAttribute("allcriteria", doc);
        Map<String, Object> map = new HashMap<String, Object>();

        return map;
    }

    //Continue parsing the initial event, inclusion criteria and exclusion criteria with the latest terms.
    @RequestMapping(value = "/continueParsing")
    @ResponseBody
    public Map<String, Object> continueParse(HttpSession httpSession, HttpServletRequest request, String dataset, String nctid, String time, String exc, String inc, String initialEvent,
                                             boolean abb, boolean recon, String obstart, String obend, String daysbefore, String daysafter, String limitto) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        Document doc = (Document) httpSession.getAttribute("allcriteria");
        JSONArray excArray = JSONArray.fromObject(exc);
        JSONArray incArray = JSONArray.fromObject(inc);
        JSONArray iniArray = JSONArray.fromObject(initialEvent);

        //Concept Mapping, Negation Detection, and Relation Extraction
        doc = this.ieService.continueTranslateByDoc(doc, iniArray, incArray, excArray);
        doc = this.ieService.patchIEResults(doc);//Add "Demographic" term together with the "has_value" relation.
        if (recon) {
            doc = this.ieService.reconIEResults(doc);
        }
        abb = false;
        if (abb == true) {
            doc = this.ieService.abbrExtensionByDoc(doc);//Extend the abbreviation.
        }
        //doc = this.cfService.removeRedundency(doc);

        ObservationConstraint oc = new ObservationConstraint();
        oc.setDaysAfter(Integer.valueOf(daysafter));
        oc.setDaysBefore(Integer.valueOf(daysbefore));
        oc.setLimitTo(limitto);

        if (obstart.length() > 0) {
            oc.setStartDate(obstart);
        } else {
            oc.setStartDate(null);
        }
        if (obend.length() > 0) {
            oc.setEndDate(obend);
        } else {
            oc.setEndDate(null);
        }
        doc.setInitial_event_constraint(oc);
        logger.info("[IP:" + remoteAddr + "][Parsing Results]" + JSONObject.fromObject(doc));

        //Process of Query Formulation
        logger.info("[IP:" + remoteAddr + "][Start formulateCohort]");
        JSONObject expressionstr = this.qfService.formualteCohortQuery(doc);
        //System.out.println(cohortjson);
        logger.info("[IP:" + remoteAddr + "][End formulateCohort]");

        JSONObject expression = new JSONObject();
        expression.accumulate("expression", expressionstr);
        httpSession.setAttribute("jsonResult", expressionstr.toString());
        System.out.println("expressionstr=" + expression);

        //generate SQL template
        logger.info("[IP:" + remoteAddr + "][Start SQL Formulation]");
        long startTime = System.currentTimeMillis();
        String results = JSON2SQL.SQLTemplate(expression.toString());
        httpSession.setAttribute("sqlResult", results);
        //SQL template -> different SQLs
        JSONObject sqljson = new JSONObject();
        sqljson.accumulate("SQL", results);
        sqljson.accumulate("targetdialect", "postgresql");
        String sqlResult = JSON2SQL.template2Postgres(sqljson.toString());
        sqlResult = JSON2SQL.fixAge(sqlResult, "PostgreSQL");
        long endTime = System.currentTimeMillis();
        System.out.println("time(Local WebAPI): " + (endTime - startTime) + "ms");

        httpSession.setAttribute("postgreSQLResult", sqlResult);
        logger.info("[IP:" + remoteAddr + "][End SQL Formulation]");
        logger.info("[IP:" + remoteAddr + "][Start GenerateReport]");
        JSONArray queryResult = this.qfService.generateReport(sqlResult, dataset);
        logger.info("[IP:" + remoteAddr + "][End GenerateReport]");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("queryResult", queryResult);
        httpSession.setAttribute("queryResult", queryResult);

        return map;
    }

    @RequestMapping("/runPipeline")
    @ResponseBody
    public Map<String, Object> runWholePipeLine(HttpSession httpSession, HttpServletRequest request, String nctid, String dataset, String initialevent, String inc, String exc, boolean abb, boolean recon, String obstart, String obend, String daysbefore, String daysafter, String limitto) {
        //Process of Information Extraction
        Map<String, Object> map = new HashMap<String, Object>();
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        logger.info("[IP:" + remoteAddr + "][Click Parse]");
        logger.info("[IP:" + remoteAddr + "][Initial Event]" + initialevent);
        logger.info("[IP:" + remoteAddr + "][Inclusion Criteria]" + inc);
        logger.info("[IP:" + remoteAddr + "][Exclusion Criteria]" + exc);

        Document doc = this.ieService.translateByDoc(initialevent, inc, exc);

        doc = this.ieService.patchIEResults(doc);//Add "Demographic" term and "has_value" relations to the document.
        if (recon) {
            doc = this.ieService.reconIEResults(doc);//Not understand
        }

        //Set the constraints for the document
        ObservationConstraint oc = new ObservationConstraint();
        oc.setDaysAfter(Integer.valueOf(daysafter));
        oc.setDaysBefore(Integer.valueOf(daysbefore));
        oc.setLimitTo(limitto);
        if (obstart.length() > 0) {
            oc.setStartDate(obstart);
        } else {
            oc.setStartDate(null);
        }
        if (obend.length() > 0) {
            oc.setEndDate(obend);
        } else {
            oc.setEndDate(null);
        }
        doc.setInitial_event_constraint(oc);

        List<DisplayCriterion> display_initial_event = this.ieService.displayDoc(doc.getInitial_event());
        List<DisplayCriterion> display_inclusion_criteria = this.ieService.displayDoc(doc.getInclusion_criteria());
        List<DisplayCriterion> display_exclusion_criteria = this.ieService.displayDoc(doc.getExclusion_criteria());
        abb = false;
        if (abb == true) {
            doc = this.ieService.abbrExtensionByDoc(doc);//Extend the abbreviation term.
        }
        //doc = this.cfService.removeRedundency(doc);
        logger.info("[IP:" + remoteAddr + "][Parsing Results]" + JSONObject.fromObject(doc));
        httpSession.setAttribute("allcriteria", doc);
        map.put("display_initial_event", display_initial_event);
        map.put("display_include", display_inclusion_criteria);
        map.put("display_exclude", display_exclusion_criteria);

        //Process of Query Formulation
        logger.info("[IP:" + remoteAddr + "][Start formulateCohort]");
        JSONObject expressionstr = this.qfService.formualteCohortQuery(doc);
        logger.info("[IP:" + remoteAddr + "][End formulateCohort]");

        JSONObject expression = new JSONObject();
        expression.accumulate("expression", expressionstr);
        httpSession.setAttribute("jsonResult", expressionstr.toString());

        //generate SQL template
        long startTime = System.currentTimeMillis();
        System.out.println(expression.toString());
        String results = JSON2SQL.SQLTemplate(expression.toString());
        httpSession.setAttribute("sqlResult", results);
        //SQL template -> different SQLs
        JSONObject sqljson = new JSONObject();
        sqljson.accumulate("SQL", results);
        sqljson.accumulate("targetdialect", "postgresql");
        String sqlResult = JSON2SQL.template2Postgres(sqljson.toString());
        sqlResult = JSON2SQL.fixAge(sqlResult, "PostgreSQL");
        long endTime = System.currentTimeMillis();
        System.out.println("time cost(Local WebAPI): " + (endTime - startTime) + "ms");
        httpSession.setAttribute("postgreSQLResult", sqlResult);
        logger.info("[IP:" + remoteAddr + "][Start GenerateReport]");
        JSONArray queryResult = this.qfService.generateReport(sqlResult, dataset);
        logger.info("[IP:" + remoteAddr + "][End GenerateReport]");
        map.put("queryResult", queryResult);
        httpSession.setAttribute("queryResult", queryResult);

        return map;
    }

    @RequestMapping("/downloadJSON")
    public void saveJSONFile(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        logger.info("[IP:" + remoteAddr + "][Download Parsing Results]");
        String jsonResult = (String) httpSession.getAttribute("jsonResult");
        response.setContentType("text/plain");
        String fileName = "Criteria2Query_result";
        try {
            fileName = URLEncoder.encode("Criteria2Query_JSON", "UTF-8");
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
            buff.write(jsonResult.getBytes("UTF-8"));
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

    @RequestMapping("/downloadSQL")
    public void saveSQLFile(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        logger.info("[IP:" + remoteAddr + "][Download Parsing Results]");
        String sqlDialect = request.getParameter("sqlDialect");
        String sqlResult = "";
        if (sqlDialect.equals("PostgreSQL")) {
            sqlResult = (String) httpSession.getAttribute("postgreSQLResult");
        } else {
            String results = (String) httpSession.getAttribute("sqlResult");
            JSONObject sqljson = new JSONObject();
            sqljson.accumulate("SQL", results);
            if (sqlDialect.equals("MSSQL_Server")) {
                sqljson.accumulate("targetdialect", "sql server");
            }
            sqlResult = JSON2SQL.template2Postgres(sqljson.toString());
            sqlResult = JSON2SQL.fixAge(sqlResult, sqlDialect);
        }
        response.setContentType("text/plain");
        String fileName = "Criteria2Query_result";
        try {
            fileName = URLEncoder.encode("Criteria2Query_SQL", "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".txt");
        BufferedOutputStream buff = null;
        ServletOutputStream outSTr = null;
        try {
            outSTr = response.getOutputStream();
            buff = new BufferedOutputStream(outSTr);
            buff.write(sqlResult.getBytes("UTF-8"));
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

    @RequestMapping(value = "/queryAgencyPage")
    @ResponseBody
    public JSONObject pageOne(String offset, String limit, HttpServletRequest request,
                              HttpServletResponse response, HttpSession session) {
        Integer offset1 = Integer.parseInt(offset);
        Integer limit1 = Integer.parseInt(limit);
        JSONArray personArray = (JSONArray) session.getAttribute("queryResult");
        int max = 0;
        if (offset1 == 0 && limit1 == -1) {
            max = personArray.size();
        } else {
            if (offset1 + limit1 <= personArray.size()) {
                max = offset1 + limit1;
            } else {
                max = personArray.size();
            }
        }

        JSONArray subset = new JSONArray();
        for (int i = offset1; i < max; i++) {
            subset.add(personArray.get(i));
        }
        JSONObject result = new JSONObject();
        result.accumulate("rows", subset);
        result.accumulate("total", personArray.size());
        return result;
    }


    @RequestMapping(value = "/changeDataset")
    @ResponseBody
    public void changeDataset(HttpSession httpSession, HttpServletRequest request, String dataset) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        String sqlResult = (String) httpSession.getAttribute("postgreSQLResult");
        logger.info("[IP:" + remoteAddr + "][Start GenerateReport]");
        JSONArray queryResult = this.qfService.generateReport(sqlResult, dataset);
        logger.info("[IP:" + remoteAddr + "][End GenerateReport]");
        httpSession.setAttribute("queryResult", queryResult);
    }


}
