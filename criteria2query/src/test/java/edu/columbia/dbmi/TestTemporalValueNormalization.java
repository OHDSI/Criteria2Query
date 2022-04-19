package edu.columbia.dbmi;

import com.opencsv.CSVWriter;
import edu.columbia.dbmi.ohdsims.pojo.TemporalConstraint;
import edu.columbia.dbmi.ohdsims.tool.CoreNLP;
import edu.columbia.dbmi.ohdsims.tool.ValueNormalization;
import edu.columbia.dbmi.ohdsims.util.TemporalNormalize;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import static edu.columbia.dbmi.ohdsims.util.NumericConvert.recognizeNumbersAdvanced;


public class TestTemporalValueNormalization {

    public static void main(String[] args) throws Exception {
//        String text = "48 hours or less";
//        TemporalNormalize tn = new TemporalNormalize();
//        TemporalConstraint[] temporalConstraint = tn.normalizeTemporal(text);
//        if (temporalConstraint!= null) {
//            System.out.println("start_days: " + temporalConstraint[0].getStart_days().toString());
//            System.out.println("start_offset: " + temporalConstraint[0].getStart_offset().toString());
//            System.out.println("end_days: " + temporalConstraint[0].getEnd_days().toString());
//            System.out.println("end_offset: " + temporalConstraint[0].getEnd_offset().toString());
//        }

//        testAccuracyTemporalNew();
//        testAccuracyValueNew();
//        testAccuracyValueOld();
        testAccuracyTemporalOld();
    }

    public static void testAccuracyTemporalNew() throws Exception {
        String file = "D:\\Work\\WengLab\\C2Q\\Editable_UI\\evaluation\\temporal_test_input_cleaned.csv";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        CoreNLP snlp = new CoreNLP();
        String line;
        TemporalConstraint[] temporalConstraint;
        File outputFile = new File("D:\\Work\\WengLab\\C2Q\\Editable_UI\\evaluation\\temporal_test_output_cleaned.csv");
        try {
            FileWriter output = new FileWriter(outputFile);
            CSVWriter write = new CSVWriter(output);

            // Header column value
            String[] header = {"Text_before_tokenization", "Text_after_tokenization", "start_days", "start_offset", "end_days", "end_offset"};
            write.writeNext(header);
            TemporalNormalize tn = new TemporalNormalize();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                List<String> sss = snlp.splitParagraph(line);
                System.out.println(sss);
                temporalConstraint = tn.normalizeTemporal(sss.get(0));
                String[] data;
                if (temporalConstraint != null) {
                    System.out.println("start_days: " + temporalConstraint[0].getStart_days().toString());
                    System.out.println("start_offset: " + temporalConstraint[0].getStart_offset().toString());
                    System.out.println("end_days: " + temporalConstraint[0].getEnd_days().toString());
                    System.out.println("end_offset: " + temporalConstraint[0].getEnd_offset().toString());

                    data = new String[]{line, sss.get(0), temporalConstraint[0].getStart_days().toString(),
                            temporalConstraint[0].getStart_offset().toString(), temporalConstraint[0].getEnd_days().toString(),
                            temporalConstraint[0].getEnd_offset().toString()};
                } else {
                    data = new String[]{line, sss.get(0), null, null, null, null};
                }
                write.writeNext(data);
            }
            write.close();
            System.out.println("Finished");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }
    }

    public static void testAccuracyTemporalOld() throws Exception {
        String file = "D:\\Work\\WengLab\\C2Q\\Editable_UI\\evaluation\\temporal_test_input_cleaned_old.csv";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        CoreNLP snlp = new CoreNLP();
        String line;
        TemporalConstraint[] temporalConstraint;
        File outputFile = new File("D:\\Work\\WengLab\\C2Q\\Editable_UI\\evaluation\\temporal_test_output_cleaned_old.csv");
        try {
            FileWriter output = new FileWriter(outputFile);
            CSVWriter write = new CSVWriter(output);

            // Header column value
            String[] header = {"Text_before_tokenization", "Text_after_tokenization", "start_days", "start_offset", "end_days", "end_offset"};
            write.writeNext(header);
            TemporalNormalize tn = new TemporalNormalize();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                List<String> sss = snlp.splitParagraph(line);
                System.out.println(sss);
                temporalConstraint = normalizeTemporalOld(sss.get(0));
                String[] data;
                if (temporalConstraint != null) {
                    System.out.println("start_days: " + temporalConstraint[0].getStart_days().toString());
                    System.out.println("start_offset: " + temporalConstraint[0].getStart_offset().toString());
                    System.out.println("end_days: " + temporalConstraint[0].getEnd_days().toString());
                    System.out.println("end_offset: " + temporalConstraint[0].getEnd_offset().toString());

                    data = new String[]{line, sss.get(0), temporalConstraint[0].getStart_days().toString(),
                            temporalConstraint[0].getStart_offset().toString(), temporalConstraint[0].getEnd_days().toString(),
                            temporalConstraint[0].getEnd_offset().toString()};
                } else {
                    data = new String[]{line, sss.get(0), null, null, null, null};
                }
                write.writeNext(data);
            }
            write.close();
            System.out.println("Finished");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }
    }

    public static TemporalConstraint[] normalizeTemporalOld(String temoralplaintext) {
        TemporalConstraint[] tc = new TemporalConstraint[2];
        tc[0] = new TemporalConstraint();
        tc[1] = new TemporalConstraint();
        TemporalNormalize tn = new TemporalNormalize();
        Integer days = tn.temporalNormalizeforNumberUnitOld(temoralplaintext);
        tc[0].setStart_days(days);
        tc[0].setStart_offset(-1);
        tc[0].setEnd_days(0);
        tc[0].setEnd_offset(1);
        if (temoralplaintext.toLowerCase().equals("current")) {
            tc[0] = new TemporalConstraint();
        }

        if (days < 10000) {
            //System.out.println(days);
            return tc;
        } else {
            return null;
        }
    }

    public static void testAccuracyValueNew() throws Exception {
        String file = "D:\\Work\\WengLab\\C2Q\\Editable_UI\\evaluation\\value_test_input_cleaned.csv";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        CoreNLP snlp=new CoreNLP();
        String line;
        Pair<Boolean, JSONArray> pair;
        File outputFile = new File("D:\\Work\\WengLab\\C2Q\\Editable_UI\\evaluation\\value_test_output_cleaned.csv");
        try {
            FileWriter output = new FileWriter(outputFile);
            CSVWriter write = new CSVWriter(output);

            // Header column value
            String[] header = { "Text_before_tokenization", "Text_after_tokenization", "flagOr", "Value"};
            write.writeNext(header);
            ValueNormalization vn = new ValueNormalization();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                List<String> sss = snlp.splitParagraph(line);
                System.out.println(sss);
                pair = vn.recognizeValueAndOp(sss.get(0));
                System.out.println("logic_or: "+pair.getKey());
                System.out.println(pair.getValue());
                int flagOr = pair.getKey() ? 1 : 0;
                String[] data = {line, sss.get(0), String.valueOf(flagOr), String.valueOf(pair.getValue())};
                write.writeNext(data);
            }
            write.close();
            System.out.println("Finished");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }


    }

    public static void testAccuracyValueOld() throws Exception {
        String file = "D:\\Work\\WengLab\\C2Q\\Editable_UI\\evaluation\\value_test_input_cleaned_old.csv";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        CoreNLP snlp = new CoreNLP();
        String line;
        JSONObject jo;
        File outputFile = new File("D:\\Work\\WengLab\\C2Q\\Editable_UI\\evaluation\\value_test_output_cleaned_old_method.csv");
        try {
            FileWriter output = new FileWriter(outputFile);
            CSVWriter write = new CSVWriter(output);

            // Header column value
            String[] header = {"Text_before_tokenization", "Text_after_tokenization", "Value"};
            write.writeNext(header);
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                List<String> sss = snlp.splitParagraph(line);
                System.out.println(sss);
                jo = valueNormalizeOld(sss.get(0));
                System.out.println(jo);
                String[] data = {line, sss.get(0), String.valueOf(jo)};
                write.writeNext(data);
            }
            write.close();
            System.out.println("Finished");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }
    }

    public static JSONObject valueNormalizeOld(String mvalue) {

        JSONObject jo = new JSONObject();
        List<Double> m = recognizeNumbersAdvanced(mvalue);
        if (m != null) {
            if (m.size() == 2) {
                if (mvalue.toLowerCase().contains("or")) {
                    jo.accumulate("Value", m.get(0));
                    jo.accumulate("Extent", m.get(1));
                    jo.accumulate("Op", "!bt");
                } else {
                    jo.accumulate("Value", m.get(0));
                    jo.accumulate("Extent", m.get(1));
                    jo.accumulate("Op", "bt");
                }
            } else if (m.size() == 1) {
                System.out.println("1 number");
                if (((mvalue.indexOf("=") == -1 && mvalue.indexOf(">") != -1) || (mvalue.indexOf("greater") != -1) || (mvalue.indexOf("higher") != -1))) {
                    System.out.println(">");
                    jo.accumulate("Value", m.get(0));
                    jo.accumulate("Op", "gt");
                } else if ((mvalue.indexOf("=") == -1 && mvalue.indexOf("<") != -1) || (mvalue.indexOf("lower") != -1) || (mvalue.indexOf("smaller") != -1)) {
                    System.out.println("<");
                    jo.accumulate("Value", m.get(0));
                    jo.accumulate("Op", "lt");
                } else if ((mvalue.indexOf("≥") != -1) || ((mvalue.indexOf("greater") != -1) && (mvalue.indexOf("equal") != -1)) || ((mvalue.indexOf(">") != -1) && (mvalue.indexOf("=") != -1))) {
                    System.out.println(">=");
                    jo.accumulate("Value", m.get(0));
                    jo.accumulate("Op", "gte");
                } else if ((mvalue.indexOf("≤") != -1 || ((mvalue.indexOf("less") != -1) && (mvalue.indexOf("equal") != -1)))) {
                    System.out.println("<=");
                    jo.accumulate("Value", m.get(0));
                    jo.accumulate("Op", "lte");
                } else if (
                        (mvalue.indexOf(">") != -1 && mvalue.indexOf("<") == -1 && mvalue.indexOf("=") == -1)
                                ||
                                (mvalue.indexOf("older") != -1 && mvalue.indexOf("younger") == -1)
                ) {
                    System.out.println("gt-==?");
                    jo.accumulate("Value", m.get(0));
                    jo.accumulate("Op", "gt");
                } else if ((mvalue.indexOf("<") != -1 && mvalue.indexOf(">") == -1 && mvalue.indexOf("=") == -1)
                        ||
                        (mvalue.indexOf("younger") != -1 && mvalue.indexOf("older") == -1)) {
                    jo.accumulate("Value", m.get(0));
                    jo.accumulate("Op", "lt");
                } else if ((mvalue.indexOf("≤") != -1) || (mvalue.indexOf("at most") != -1) || ((mvalue.indexOf("<") != -1) && (mvalue.indexOf("=") != -1))) {
                    jo.accumulate("Value", m.get(0));
                    jo.accumulate("Op", "lte");
                } else if ((mvalue.indexOf("≥") != -1) || (mvalue.indexOf("at least") != -1) || ((mvalue.indexOf(">") != -1) && (mvalue.indexOf("=") != -1))) {
                    jo.accumulate("Value", m.get(0));
                    jo.accumulate("Op", "gte");
                }
            }
        }
        return jo;

    }

}
