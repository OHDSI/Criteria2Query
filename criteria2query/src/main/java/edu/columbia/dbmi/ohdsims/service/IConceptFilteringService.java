package edu.columbia.dbmi.ohdsims.service;

import edu.columbia.dbmi.ohdsims.pojo.Document;

public interface IConceptFilteringService {
	public Document removeRedundency(Document doc);
	
}
