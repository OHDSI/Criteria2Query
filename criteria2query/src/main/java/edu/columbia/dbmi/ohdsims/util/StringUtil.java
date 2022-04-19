package edu.columbia.dbmi.ohdsims.util;

import java.text.Normalizer;

public class StringUtil {
	public static String replaceString(String str, String target, String replacestr) {
		int begin = str.toLowerCase().indexOf(target.toLowerCase());
		int end = begin + target.length();
		String processedstr = str.substring(0, begin) + replacestr + str.substring(end);
		return processedstr;
	}
	
	public static String cleanASCII(String subjectString) {
		// String subjectString = "aaaua";//"öäü";
		subjectString = Normalizer.normalize(subjectString, Normalizer.Form.NFD);
		String resultString = subjectString.replaceAll("[^\\x00-\\x7F]", "");
		return resultString;
		}
}
