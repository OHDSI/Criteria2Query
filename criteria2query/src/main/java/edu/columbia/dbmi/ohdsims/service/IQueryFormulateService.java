package edu.columbia.dbmi.ohdsims.service;

import java.util.List;

import edu.columbia.dbmi.ohdsims.pojo.CdmCohort;
import edu.columbia.dbmi.ohdsims.pojo.CdmCriteria;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.Paragraph;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface IQueryFormulateService {
public CdmCohort translateByDoc(Document doc);
public List<CdmCriteria> translateByParagraph(Paragraph p,boolean include);
public CdmCriteria translateBySentence(Sentence s,boolean include) ;
public JSONObject formualteCohortQuery(Document doc);
public Integer storeInATLAS(JSONObject expression,String cohortname);
public JSONArray generateReport(String query, String dataset);
}
