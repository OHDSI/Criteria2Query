package edu.columbia.dbmi.ohdsims.tool;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class FeedBackTool {
	private static Logger logger = LogManager.getLogger(FeedBackTool.class);
	
	public void recordFeedback(String timestamp,String email,String content){
		logger.info("[FeedBack]["+timestamp+"]["+email+"]"+"["+content+"]");
	}

}
