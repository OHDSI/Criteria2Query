package edu.columbia.dbmi.ohdsims.util;

import java.util.LinkedHashMap;
import java.util.List;

import edu.columbia.dbmi.ohdsims.pojo.Cdmentity;

public class PreprocessUtil {
	
	public static String geekcharTranslate(String str){
		str=str.replaceAll("α", "alpha");
		str=str.replaceAll("β", "beta");
		str=str.replaceAll("γ", "gamma");
		return str;
	}
	
	public static String markMetaMapLiteResult(String orignal,List<Cdmentity> nerlist){
		StringBuffer sb=new StringBuffer();
		int startindex=0;
		int endindex=-1;
		for(int i=0;i<nerlist.size();i++){
			startindex=nerlist.get(i).getStartindex();
			System.out.println((endindex+1)+"$"+startindex);
			sb.append(orignal.substring(endindex+1, startindex));	
			sb.append("<mark data-entity=\""+nerlist.get(i).getDomain().toLowerCase()+"\">");
			sb.append(nerlist.get(i).getEntityname());
			sb.append("</mark>");
			endindex=nerlist.get(i).getEndindex();
		}
		if(endindex!=orignal.length()){
			sb.append(orignal.substring(endindex+1));
		}
		return sb.toString();
	}
	
	public static String subSentence(String[] arrstr,int start,int endindex){
		StringBuffer sb=new StringBuffer();
		for(int x=start;x<endindex;x++){
			sb.append(arrstr[x]+" ");
		}
		return sb.toString();
	}
	public static int getwordlength(String entity){
		return entity.trim().split(" ").length;
		
	}
}
