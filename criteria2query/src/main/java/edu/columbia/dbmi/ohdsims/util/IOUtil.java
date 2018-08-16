package edu.columbia.dbmi.ohdsims.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.columbia.dbmi.ohdsims.pojo.CdmCriterion;
import edu.columbia.dbmi.ohdsims.pojo.Document;
import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.Paragraph;
import edu.columbia.dbmi.ohdsims.pojo.Sentence;
import edu.columbia.dbmi.ohdsims.pojo.TemporalConstraint;
import edu.columbia.dbmi.ohdsims.pojo.Term;
import edu.stanford.nlp.util.Triple;

public class IOUtil {
	public static String doc2Str(Document doc) {
		List<Paragraph> incps = doc.getInclusion_criteria();
		List<Paragraph> excps = doc.getExclusion_criteria();
		StringBuffer sb = new StringBuffer();

		String inc_entities = Pargraph2List(incps, "INC");
		String exc_entities = Pargraph2List(excps, "EXC");

		return inc_entities + exc_entities;
	}

	public static String Pargraph2List(List<Paragraph> incps, String inctag) {
		StringBuffer sb = new StringBuffer();
		if (incps != null) {
			for (Paragraph p : incps) {
				List<Sentence> sents = p.getSents();
				if (sents != null) {
					for (Sentence s : sents) {
						List<Term> terms = s.getTerms();
						List<Triple<Integer, Integer, String>> relations = s.getRelations();
						for (Term t : terms) {
							if (Arrays.asList(GlobalSetting.primaryEntities).contains(t.getCategorey())) {
								CdmCriterion cunit = new CdmCriterion();
								cunit.setOrginialtext(t.getText());
								cunit.setCriterionId(t.getTermId());
								cunit.setConceptsetId(t.getVocabularyId());
								cunit.setNeg(t.isNeg());
								cunit.setDomain(t.getCategorey());
								String temporalstr = "no_temporal";
								String valuestr = "no_value";
								for (Triple<Integer, Integer, String> r : relations) {
									if (t.getTermId() == r.first) {
										if (r.third.equals("has_temporal")) {
											temporalstr = findTermById(terms, r.second).getText();
										} else if (r.third.equals("has_value")) {
											valuestr = findTermById(terms, r.second).getText();
										}
									}

									// allinfo.add(inctag+"\t"+t.getText()+"\t"+t.getCategorey()+"\t"+t.isNeg()+"\t"+temporalstr+"\t"+valuestr+"\n");
									
								}
								sb.append(inctag + "\t" + t.getText() + "\t" + t.getCategorey() + "\t" + t.isNeg()
								+ "\t" + temporalstr + "\t" + valuestr + "\n");
							}
							
						}
					}
				}
			}
		}
		return sb.toString();

	}

	public static Term findTermById(List<Term> terms, Integer termId) {
		for (Term t : terms) {
			if (t.getTermId() == termId) {
				return t;
			}
		}
		return null;
	}
	
	public static String removeMultiSpace(String s) {
		String[] str = s.split("\\s+ ");
		// System.out.println("~~"+str.length);
		// if (str.length > 1) {
		// return str[1];
		// } else {
		// return s;
		// }
		int index = 0;
		for (int i = 0; i < s.length() - 1; i++) {
			if (s.charAt(i) != ' ') {
				index = i;
				break;
			}
		}
		//System.out.println(s.substring(index));
		return s.substring(index);
	}
	
	public static String[] separateIncExc(String ecstr) {
		int incindex = ecstr.toLowerCase().indexOf("inclusion criteria");
		int excindex = ecstr.toLowerCase().indexOf("exclusion criteria");
		String[] eclines = ecstr.split("\n");
		String[] inc_exc = new String[2];
		StringBuffer incsb = new StringBuffer();
		StringBuffer excsb = new StringBuffer();
		boolean incflag = false;
		boolean excflag = false;
		if (incindex > -1 && excindex > -1) {
			for (String r : eclines) {
				if (r.toLowerCase().contains("inclusion criteria:")) {
					incflag = true;
					excflag = false;
					continue;
				}
				if (r.toLowerCase().contains("exclusion criteria:")) {
					excflag = true;
					incflag = false;
					continue;
				}
				if (incflag == true && excflag == false) {
					incsb.append(r + "\n");
				}
				if (excflag == true && incflag == false) {
					excsb.append(r + "\n");
				}
			}

		} else if (incindex != -1 && excindex == -1) {
			for (String r : eclines) {
				if (r.toLowerCase().contains("inclusion criteria")) {
					incflag = true;
					continue;
				}
				incsb.append(r + "\n");
			}

		} else if (incindex == -1 && excindex != -1) {
			for (String r : eclines) {
				if (r.toLowerCase().contains("exclusion criteria")) {
					continue;
				}
				excsb.append(r + "\n");
			}
		} else {
			incsb.append(ecstr);
		}
		inc_exc[0]=incsb.toString();
		inc_exc[1]=excsb.toString();
		return inc_exc;
	}
}
