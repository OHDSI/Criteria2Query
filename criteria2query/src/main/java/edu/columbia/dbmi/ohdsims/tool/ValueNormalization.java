package edu.columbia.dbmi.ohdsims.tool;

import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.util.FileUtil;
import edu.columbia.dbmi.ohdsims.util.RomanConvert;
import edu.stanford.nlp.ie.NumberNormalizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVWriter;

import javax.validation.constraints.Null;

public class ValueNormalization {
    Map<String, String> phrase_dict = new HashMap<>();
    MaxentTagger tagger = new MaxentTagger("edu/columbia/dbmi/ohdsims/model/english-left3words-distsim.tagger");
    RomanConvert romanConvert = new RomanConvert();

    public ValueNormalization() {
        Resource fileRource = new ClassPathResource(GlobalSetting.valueTransDict);
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

    public static void main(String[] args) throws Exception {
        ValueNormalization valueNormalization = new ValueNormalization();
//        //JSONObject obj= valueNormalization.recognizeValueAndOp("age 2 ^ 4");
//        //System.out.println(obj);
        Pair<Boolean, JSONArray> pair = valueNormalization.recognizeValueAndOp("up to 5L");//""smaller than or equal to 90 and larger than 50");//"> 120 mmHg");
        System.out.println("logic_or: "+pair.getKey());
        System.out.println(pair.getValue());

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


    public Pair<Boolean,JSONArray> recognizeValueAndOp(String phrase) {


        boolean flagNeg = (" "+phrase.toLowerCase()+" ").matches(".*\\s(no|not)\\s.*");
        String phrasePos = tagger.tagString(phrase);


        //Dealing with "fifty four"
        Pattern pattern = Pattern.compile("([a-zA-Z]+)_CD ([a-zA-Z]+)_CD");
        Matcher matcher = pattern.matcher(phrasePos);
        while (matcher.find()) {
            phrasePos = phrasePos.replace(matcher.group(0), matcher.group(1) + "-" + matcher.group(2) + "_CD");
        }

        pattern = Pattern.compile("([0-9.]+)_CD ([\\^+*x/])_(NN|SYM|HYPH) ([0-9.E]+)_CD");
        matcher = pattern.matcher(phrasePos);
        Double num1, num2, num3;
        String sym;
        while (matcher.find()) {
            num1 = Double.valueOf(matcher.group(1));
            num2 = Double.valueOf(matcher.group(4));
            sym = matcher.group(2);
            if (sym.equals("^")) {
                num3 = Math.pow(num1, num2);
            } else if (sym.equals("+")) {
                num3 = num1 + num2;
            }else if (sym.equals("/")){
                num3 = num1/num2;
            }
            else {
                //Since "x", "*" are always used as the connection between the number and its unit,
                // we will not regard them as the times sign.
                num3 = num1;
            }
            phrasePos = phrasePos.replace(matcher.group(0), num3.toString() + "_CD");
        }

        List<Double> numbers = new ArrayList<>();
        String[] words = phrasePos.split(" ");
        String flagOp = "";
        Boolean wordNum = false;
        Boolean percent = false;
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String[] Pos = word.trim().split("_");
            if (Pos[1].matches(".*(CD).*") || Pos[0].matches("[0-9].*")||(Pos[1].matches(".*(NN).*")&&Pos[0].matches("(I|V).*"))) {
                if (Pos[0].matches("^[0-9E./*x]*$")) {
                    Double d;
                    if (Pos[0].contains("/")){
                        String[] parts = Pos[0].split("/");
                        d = Double.valueOf(parts[0]);
                        for(int j=1; j<parts.length;j++){
                            d = d/Double.valueOf(parts[j]);
                        }
                    }else if(Pos[0].matches(".*(\\*|x).*")){ //We regard * as the connection between number and unit, not the times sign
                        String[] parts = Pos[0].split("(\\*|x)");
                        d = Double.valueOf(parts[0]);
                    }else {
                        d = Double.valueOf(Pos[0]);
                    }
                    numbers.add(d);
                    flagOp = flagOp.concat(d.toString());
                } else {
                    Pattern p = Pattern.compile("[^0-9]");
                    Matcher m = p.matcher(Pos[0]);
                    String s = m.replaceAll("").trim();
                    if (!s.isEmpty()) {
                        Double d = Double.valueOf(s);
                        numbers.add(d);
                        flagOp = flagOp.concat(d.toString());
                    }else {
                        Double d;
                        try {
                            d = (double)romanConvert.romanToInt(Pos[0]);
                        }catch (NullPointerException e1){
                            try{
                                d = NumberNormalizer.wordToNumber(Pos[0]).doubleValue();
                                wordNum = true;
                            }
                            catch (NumberFormatException e2){
                                continue;
                            }

                        }
                        numbers.add(d);
                        flagOp = flagOp.concat(d.toString());
                    }

                }
            } else if (Pos[1].matches(".*(CC|,).*")||Pos[0].equals(",")) {
                if (Pos[0].toLowerCase().equals("or")) {
                    //flagOr = true;
                    flagOp = flagOp.concat("|");
                } else if (Pos[0].toLowerCase().equals("and")) {
                    flagOp = flagOp.concat("&");
                } else {
                    flagOp = flagOp.concat(",");
                }
            } else if (Pos[0].equals("%") || Pos[0].toLowerCase().equals("percent")) {
                percent = true;
            } else{
                if (Pos[0].toLowerCase().equals("up") && i<words.length-1 && words[i+1].trim().substring(0,2).equals("to")){
                    flagOp = flagOp.concat("<=");
                }else {
                    sym = phrase_dict.get(Pos[0].toLowerCase());
                    if (sym != null) {
                        flagOp = flagOp.concat(sym);
                    }
                }
            }
        }


        if (wordNum == true && numbers.size() > 1) {
            //Combine numbers in word format, e.g."one hundred and forty five"
            List<Double> intergratedNumbers = new ArrayList<>();
            Double num = numbers.get(0).doubleValue();
            for (int i = 1; i < numbers.size(); i++) {
                if (getNumLength(numbers.get(i).doubleValue()) < getNumLength(numbers.get(i - 1).doubleValue())) {
                    num = num + numbers.get(i).doubleValue();
                } else {
                    if (percent == true) {
                        intergratedNumbers.add(num * 0.01);
                    } else {
                        intergratedNumbers.add(num);
                    }

                    num = numbers.get(i).doubleValue();
                }
            }
            if (percent == true) {
                intergratedNumbers.add(num * 0.01);
            } else {
                intergratedNumbers.add(num);
            }
            numbers = intergratedNumbers;
        } else {
            if (percent == true) {
                for (int i = 0; i < numbers.size(); i++) {
                    numbers.set(i, numbers.get(i) * 0.01);
                }
            }
        }

        JSONArray ja = new JSONArray();
        JSONObject jo = new JSONObject();
        boolean flagOr = false;
        if (numbers.size() == 1) {
            jo = buildObjectWithOneNum(flagOp, numbers.get(0), flagNeg);
            ja.add(jo);
        } else if (numbers.size() == 2) {
            if (flagOp.contains("b")) {
                Collections.sort(numbers);
                jo.accumulate("Value", numbers.get(0));
                jo.accumulate("Extent", numbers.get(1));
                jo.accumulate("Op", "bt");
                ja.add(jo);
            }else if (!flagOp.matches(".*(>|<).*")){
                flagOr = true;
                int i =0;
                while(i<numbers.size()){
                    jo.accumulate("Value", numbers.get(i));
                    jo.accumulate("Op", "eq");
                    ja.add(jo);
                    i++;
                    jo = new JSONObject();
                }
            }else {
                int i = 1, j = 0, k = 0;
                while (i < flagOp.length()-1) {
                    char ch = flagOp.charAt(i);
                    char ch_f = flagOp.charAt(i+1);
                    char ch_b = flagOp.charAt(i-1);
                    if ((ch == '|' || ch == ',' || ch == '&') &&
                            (Character.isDigit(ch_b) || Character.isDigit(ch_f)) &&
                            !(Character.isDigit(ch_b) && Character.isDigit(ch_f))) {
                        String s = flagOp.substring(j, i);
                        if (s.matches(".*\\d.*")) {
                            jo = buildObjectWithOneNum(s, numbers.get(k), flagNeg);
                            j = i + 1;
                            k++;
                            ja.add(jo);
                            if (('|' == ch)||('|'==ch_f)||('|'==ch_b)) {
                                flagOr = true;
                            }
                        }
                    }
                    i++;
                }
                String s= flagOp.substring(j);
                if (s.matches(".*\\d.*")) {
                    jo = buildObjectWithOneNum(s, numbers.get(k), flagNeg);
                    ja.add(jo);
                }

            }
        }else{ //For cases where there are more than 2 numbers, we assume the logic among them is OR.
            flagOr = true;
            int i =0;
            while(i<numbers.size()){
                jo.accumulate("Value", numbers.get(i));
                jo.accumulate("Op", "eq");
                ja.add(jo);
                i++;
                jo = new JSONObject();
            }
        }

        return Pair.of(flagOr, ja);
    }




    private static JSONObject buildObjectWithOneNum(String flagOp, double numbers, boolean flagNeg) {
        JSONObject jo = new JSONObject();
        if ((flagOp.indexOf(">") != -1) && (!flagOp.matches(".*[=&].*")) && !flagOp.contains("|")) {
            jo.accumulate("Value", numbers);
            jo.accumulate("Op", "gt");
        } else if ((flagOp.indexOf("<") != -1) && (!flagOp.matches(".*[=&].*")) && !flagOp.contains("|")) {
            jo.accumulate("Value", numbers);
            jo.accumulate("Op", "lt");
        } else if (((flagOp.indexOf(">") != -1) && flagOp.matches(".*[=&].*")) || ((flagOp.indexOf(">") != -1) && flagOp.contains("|"))) {
            jo.accumulate("Value", numbers);
            jo.accumulate("Op", "gte");
        } else if (((flagOp.indexOf("<") != -1) && flagOp.matches(".*[=&].*")) || ((flagOp.indexOf("<") != -1) && flagOp.contains("|"))) {
            jo.accumulate("Value", numbers);
            jo.accumulate("Op", "lte");
        } else {
            jo.accumulate("Value", numbers);
            jo.accumulate("Op", "eq");
        }
        if (flagNeg){
            jo = changeOp(jo);
        }
        return jo;
    }

    private static int getNumLength(Double num) {
        num = num > 0 ? num : -num;
        if (num == 0) {
            return 1;
        }
        return (int) Math.log10(num) + 1;
    }

    public static JSONObject changeOp(JSONObject jsonObject) {
        String operation = (String) jsonObject.get("Op");
        if (operation == "bt") {
            jsonObject.put("Op", "!bt");
        } else if (operation == "!bt") {
            jsonObject.put("Op", "bt");
        } else if (operation == "gt") {
            jsonObject.put("Op", "lte");
        } else if (operation == "lt") {
            jsonObject.put("Op", "gte");
        } else if (operation == "lte") {
            jsonObject.put("Op", "gt");
        } else if (operation == "gte") {
            jsonObject.put("Op", "lt");
        } else {
            //Op=="eq"
            jsonObject.accumulate("Extent", jsonObject.get("Value"));
            jsonObject.put("Op", "!bt");
        }
        return jsonObject;
    }


}
