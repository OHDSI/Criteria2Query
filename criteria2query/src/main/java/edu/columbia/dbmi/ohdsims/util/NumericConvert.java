package edu.columbia.dbmi.ohdsims.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumericConvert {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String a = ">8";

		// Integer in=convertTodayUnit(str);
		// System.out.println("in=="+in);

		// System.out.println(recognizeNumbers(a));

		recognizeNumbersAdvanced(a);
		// System.out.println(checkFloat("2.5","+"));
	}
	public static List<Double> recognizeNumbersAdvanced(String a) {
		System.out.println("to be matched-->"+a);
		if(a.contains("(")){
			int start=a.indexOf("(");
			a=a.substring(0,start);
		}
		System.out.println("after remove "+a);
		List<Double> ssi = new ArrayList<Double>();
		Pattern pattern = Pattern.compile("[\\d\\.]+");
		Matcher matcher = pattern.matcher(a);
		while (matcher.find()) {
			if(matcher.group().equals(".")){
				continue;
			}
			System.out.println("=matcher="+matcher.group()+"startindex="+matcher.start()+",endindex="+matcher.end());
			ssi.add(Double.valueOf(matcher.group()));
		}
		System.out.println("ssi_size="+ssi.size());
		Collections.sort(ssi);
		if (ssi.size() > 0) {
			return ssi;
		} else {
			return null;
		}
	}
	
	public static List<Double[]> extractNumberPositions(String a) {
		//System.out.println("to be matched-->"+a);
		List<Double[]> numberlist=new ArrayList<Double[]>();
		Pattern pattern = Pattern.compile("[\\d\\.]+");
		Matcher matcher = pattern.matcher(a);
		while (matcher.find()) {
			Double[] numpos=new Double[3];
			//System.out.println(matcher.group()); // 打印
			if(matcher.group().equals(".")){
				continue;
			}
			//System.out.println("matcher="+matcher.group()+"startindex="+matcher.start()+",endindex="+matcher.end());	
			try{
				numpos[0]=Double.valueOf(matcher.group());
				numpos[1]=(double) matcher.start();
				numpos[2]=(double) matcher.end();
				numberlist.add(numpos);
			}catch (Exception ex){
				return null;
			}
		}
		return numberlist;
		
	}
	
	

	public static double convertTodayUnit(String str) {
		char c = str.charAt(str.length() - 1);
		double x = -1;
		switch (c) {
		case 'M':
			if (str.contains("PT")) {
				x = (1.0 / (24.0 * 60.0));
			} else {
				x = 30.0;
			}
			break;
		case 'H':
			x = (1 / 24.0);
			break;
		case 'D':
			x = 1.0;
			break;
		case 'Y':
			x = 365.0;
			break;
		case 'W':
			x = 7.0;
			break;
		}
		return x;
	}
	public static double recognizeNumbersFormSUTime(String a) {
		String tfstr = a;
		List<String> ss = new ArrayList<String>();
		for (String sss : tfstr.replaceAll("[^0-9]", ",").split(",")) {
			if (sss.length() > 0)
				ss.add(sss);
		}
		List<Integer> ssi = new ArrayList<Integer>();
		for (int i = 0; i < ss.size(); i++) {
			ssi.add(Integer.valueOf(ss.get(i)));
		}
		if (ssi.size() > 0) {
			return ssi.get(ssi.size() - 1);
		} else {
			return -100;
		}
	}
}
