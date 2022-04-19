package edu.columbia.dbmi.ohdsims.service;

import java.util.List;
import java.util.Map;

import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;
import edu.columbia.dbmi.ohdsims.pojo.DisplayCriterion;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.Paragraph;
import net.sf.json.JSONArray;

public interface IInformationExtractionService {
	public Paragraph translateText(String freetext, boolean include);

	public Document runIE4Doc(Document doc);

	public Document translateByDoc(String initial_event, String inclusion_criteria, String exclusion_criteria);

	public List<DisplayCriterion> displayDoc(List<Paragraph> ps);

	public Document patchIEResults(Document doc);

	public Document abbrExtensionByDoc(Document doc);

	public List<String> getAllInitialEvents(Document doc);

	public Document reconIEResults(Document doc);

	public Document continueTranslateByDoc(Document doc, JSONArray iniResult, JSONArray incResult, JSONArray excResult);
}
