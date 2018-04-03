package edu.columbia.dbmi.ohdsims.tool;

import org.apache.log4j.Logger;

public class FeedBackTool {
	private static Logger logger = Logger.getLogger(FeedBackTool.class);
	
	public void recordFeedback(String timestamp,String email,String content){
		logger.info("[FeedBack]["+timestamp+"]["+email+"]"+"["+content+"]");
	}

}
