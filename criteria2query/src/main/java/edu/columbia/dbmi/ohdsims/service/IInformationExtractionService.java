package edu.columbia.dbmi.ohdsims.service;

import java.util.List;

import edu.columbia.dbmi.ohdsims.pojo.DisplayCriterion;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.Paragraph;

public interface IInformationExtractionService {
	public Paragraph translateText(String freetext, boolean include);
	public Document runIE4Doc(Document doc);
	public Document translateByDoc(String initial_event,String inclusion_criteria,String exclusion_criteria);
	public List<Paragraph> translateByBlock(String text);
	public List<DisplayCriterion> displayDoc(List<Paragraph> ps);
	public Document patchIEResults(Document doc);
	public Document abbrExtensionByDoc(Document doc);
}
