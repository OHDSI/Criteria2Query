package edu.columbia.dbmi.ohdsims.util;

import java.io.*;
import java.util.*;

import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.pojo.TemporalConstraint;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.time.SUTime.Temporal;
import edu.stanford.nlp.util.CoreMap;
import jnr.ffi.annotations.In;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class TemporalNormalize {
    //    Map<String, String> phrase_dict = new HashMap<>();
    Map<String, String> phrase_dict = new TreeMap<String, String>(
            new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    if (s1.length() > s2.length()) {
                        return -1;
                    } else if (s1.length() < s2.length()) {
                        return 1;
                    } else {
                        return s1.compareTo(s2);
                    }
                }
            });

    public static void main(String[] args) {
        //String text = "at least 2 weeks";															// surgery
        String text = "prior";
        TemporalNormalize sd = new TemporalNormalize();
        //sd.temporalNormalize(text);
        System.out.println(sd.temporalNormalizeforNumberUnit(text));
    }

    AnnotationPipeline pipeline;

    public TemporalNormalize() {
        pipeline = new AnnotationPipeline();
        //pipeline.addAnnotator(new PTBTokenizerAnnotator(false));
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        String sutimeRules = "edu/columbia/dbmi/ohdsims/model/defs.sutime.txt," + "edu/columbia/dbmi/ohdsims/model/english.holidays.sutime.txt," + "edu/columbia/dbmi/ohdsims/model/english.sutime.txt";
        Properties props = new Properties();
        props.setProperty("sutime.rules", sutimeRules);
        props.setProperty("sutime.binders", "0");
        pipeline.addAnnotator(new TimeAnnotator("sutime", props));

        Resource fileRource = new ClassPathResource(GlobalSetting.temporalTransDict);
        File f;
        try {
            f = fileRource.getFile();
            String a = file2String(f, "UTF-8");
            String[] words = a.split("\n");
            for (String word : words) {
                String[] item = word.split(",");
                phrase_dict.put(item[0].trim(), item[1].trim());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String file2String(File f, String charset) {
        String result = null;
        try {
            result = stream2String(new FileInputStream(f), charset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String stream2String(InputStream in, String charset) {
        StringBuffer sb = new StringBuffer();
        try {
            Reader r = new InputStreamReader(in, charset);
            int length = 0;
            for (char[] c = new char[1024]; (length = r.read(c)) != -1; ) {
                sb.append(c, 0, length);
            }
            r.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    //Normalize the temporal attribute. Unify it to the "day" unit.
    public List<Integer> temporalNormalizeforNumberUnit(String text) {
        Annotation annotation = new Annotation(text);
        annotation.set(CoreAnnotations.DocDateAnnotation.class, SUTime.getCurrentTime().toString());
        pipeline.annotate(annotation);

        System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));

        List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
        List<Integer> days = new ArrayList<>();
        for (CoreMap cm : timexAnnsAll) {
            String phrase = cm.toString().toLowerCase();
            if (phrase.matches(".*(previous|prior|past|last|preceding|former) year.*")) {
                days.add(365);
                continue;
            } else if (phrase.matches(".*(previous|prior|past|last|preceding|former) month.*")) {
                days.add(30);
                continue;
            } else if (phrase.matches(".*(previous|prior|past|last|preceding|former) week.*")) {
                days.add(7);
                continue;
            } else if (phrase.matches("(past|day|time)")) {
                continue;
            }
            List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
            System.out.println(cm + " [from char offset " + tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) + " to "
                    + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']'
                    + " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());
            System.out.println("!!!!-->" + cm.get(TimeExpression.Annotation.class).getValue());
            System.out.println("---final result---");
            String tstr_all = cm.get(TimeExpression.Annotation.class).getTemporal().toString();
            System.out.println("tstr" + tstr_all);
            String[] tstr = tstr_all.split("/");

            for (int j = 0; j < tstr.length; j++) {
                int k = tstr[j].indexOf("P");
                if (tstr[j].indexOf(")") != -1) {
                    tstr[j] = tstr[j].substring(k, tstr[j].length() - 1);
                }
                double total = TemporalConvert.convertTodayUnit(tstr[j]);
                double number = TemporalConvert.recognizeNumbersFormSUTime(tstr[j]);
                System.out.println("t=" + total);
                System.out.println("n=" + number);
                // System.out.println("unit=" + total);
                if (total == 30 && number == 12) {
                    days.add(365);

                } else {
                    int d = (int) (total * number);
                    if (d >= 0 && d < 10000) {
                        days.add(d);
                    }
                }
            }
        }
        return days;

    }

    public Integer temporalNormalizeforNumberUnitOld(String text) {
        Annotation annotation = new Annotation(text);
        annotation.set(CoreAnnotations.DocDateAnnotation.class, SUTime.getCurrentTime().toString());
        pipeline.annotate(annotation);

        System.out.println(annotation.get(CoreAnnotations.TextAnnotation.class));

        List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
        Integer days = 0;
        for (CoreMap cm : timexAnnsAll) {
            List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
            System.out.println(cm + " [from char offset " + tokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class) + " to "
                    + tokens.get(tokens.size() - 1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class) + ']'
                    + " --> " + cm.get(TimeExpression.Annotation.class).getTemporal());
            System.out.println("!!!!-->" + cm.get(TimeExpression.Annotation.class).getValue());
            System.out.println("---final result---");
            String tstr = cm.get(TimeExpression.Annotation.class).getTemporal().toString();
            System.out.println("tstr" + tstr);
            int k = tstr.indexOf("P");
            if (tstr.indexOf(")") != -1) {
                tstr = tstr.substring(k, tstr.length() - 1);
            }
            double total = TemporalConvert.convertTodayUnit(tstr);
            double number = TemporalConvert.recognizeNumbersFormSUTime(tstr);
            System.out.println("t=" + total);
            System.out.println("n=" + number);
            // System.out.println("unit=" + total);
            if (total == 30 && number == 12) {
                days = 365;

            } else {
                days = (int) (total * number);
            }
        }
        return days;

    }


    //Normalize the temporal attribute. Unify it to "day" unit and save its value into a TemporalConstraint object.
    public TemporalConstraint[] normalizeTemporal(String temporalplaintext) {
        String text = " " + temporalplaintext.toLowerCase() + " ";
        boolean flagNeg = text.matches(".*\\s(no|not)\\s.*");
        String flagOp = "";
        for (String sym_text : phrase_dict.keySet()) {
            if (text.contains(" " + sym_text + " ")) {
                String sym = phrase_dict.get(sym_text);
                if ((sym.matches(".*(>|<).*") && !flagOp.matches(".*(>|<).*")) || !sym.matches(".*(>|<).*")) {
                    flagOp = flagOp.concat(sym);
                }
            }
        }
//        if (flagOp.matches(".*(>|<).*") && text.contains(" or ")) {
//            flagOp = flagOp.concat("=");
//        }
        if (text.contains(" over ") && text.matches(".*(previous|past|last|preceding|former|next|future).*")) {
            flagOp = "<=";
        }

        TemporalConstraint[] tc = new TemporalConstraint[2];
        tc[0] = new TemporalConstraint();
        tc[1] = new TemporalConstraint();

        String text_cleaned = temporalplaintext.replaceAll("(.*?)([^[\\s\\d]]*)([\\d.]+)([^[\\s\\d]]*)(.*?)", "$1$2 $3 $4$5");
        text_cleaned = text_cleaned.replaceAll("\\s+", " ");

        List<Integer> days = temporalNormalizeforNumberUnit(text_cleaned);


        if (text.matches(".*(within.*of|after|from|since|next|subsequent|following|post).*") &&
                !text.matches(".*(previous|prior|past|last|before|preceding|former).*")) {
            //If the event start date is after the index start date
            if (days.size() == 1) {
                int day = days.get(0);
                if ((flagOp.contains("<") && !flagOp.contains("=") && !flagNeg) ||
                        (flagOp.contains(">") && flagOp.contains("=") && flagNeg)) {
                    tc[0].setStart_days(0);
                    tc[0].setStart_offset(-1);
                    tc[0].setEnd_days(day - 1);
                    tc[0].setEnd_offset(1);
                } else if ((flagOp.contains(">") && !flagOp.contains("=") && !flagNeg) ||
                        (flagOp.contains("<") && flagOp.contains("=") && flagNeg)) {
                    tc[0].setStart_days(day + 1);
                    tc[0].setStart_offset(1);
                    tc[0].setEnd_days(9999);
                    tc[0].setEnd_offset(1);
                } else if ((flagOp.contains(">") && flagOp.contains("=") && !flagNeg) ||
                        (flagOp.contains("<") && !flagOp.contains("=") && flagNeg)) {
                    tc[0].setStart_days(day);
                    tc[0].setStart_offset(1);
                    tc[0].setEnd_days(9999);
                    tc[0].setEnd_offset(1);
                } else {
                    tc[0].setStart_days(0);
                    tc[0].setStart_offset(-1);
                    tc[0].setEnd_days(day);
                    tc[0].setEnd_offset(1);
                }

            } else if (days.size() == 2) {
                Collections.sort(days);
                tc[0].setStart_days(days.get(0));
                tc[0].setStart_offset(1);
                tc[0].setEnd_days(days.get(1));
                tc[0].setEnd_offset(1);//-1:before the index start date; 1: after the index start date
            }
        } else {
            //If the event start date is before the index start date,
            if (days.size() == 1) {
                int day = days.get(0);
                if ((flagOp.contains("<") && !flagOp.contains("=") && !flagNeg) ||
                        (flagOp.contains(">") && flagOp.contains("=") && flagNeg)) {
                    tc[0].setStart_days(day - 1);
                    tc[0].setStart_offset(-1);
                    tc[0].setEnd_days(0);
                    tc[0].setEnd_offset(1);
                } else if ((flagOp.contains(">") && !flagOp.contains("=") && !flagNeg) ||
                        (flagOp.contains("<") && flagOp.contains("=") && flagNeg)) {
                    tc[0].setStart_days(9999);
                    tc[0].setStart_offset(-1);
                    tc[0].setEnd_days(day + 1);
                    tc[0].setEnd_offset(-1);
                } else if ((flagOp.contains(">") && flagOp.contains("=") && !flagNeg) ||
                        (flagOp.contains("<") && !flagOp.contains("=") && flagNeg)) {
                    tc[0].setStart_days(9999);
                    tc[0].setStart_offset(-1);
                    tc[0].setEnd_days(day);
                    tc[0].setEnd_offset(-1);
                } else {
                    tc[0].setStart_days(day);
                    tc[0].setStart_offset(-1);
                    tc[0].setEnd_days(0);
                    tc[0].setEnd_offset(1);
                }
            } else if (days.size() == 2) {
                Collections.sort(days, Collections.reverseOrder());
                tc[0].setStart_days(days.get(0));
                tc[0].setStart_offset(-1);
                tc[0].setEnd_days(days.get(1));
                tc[0].setEnd_offset(-1);//-1:before the index start date; 1: after the index start date
            }
        }

        if (tc[0].getStart_days() != null) {
            return tc;
        } else {
            return null;
        }
//        if (days.size() == 1 && days.get(0) < 10000) {
//            //System.out.println(days);
//            return tc;
//        } else if (days.size() == 2 && days.get(1) < 10000) {
//            return tc;
//        } else {
//            return null;
//        }
    }


}
