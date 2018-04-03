package edu.columbia.dbmi.ohdsims.nlp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;  
  
public class TestRunTime {  
    public static void main(String[] args) {  
        //windows  
//      String cmd = "F:\\apache-tomcat-6.0.20.exe";  
//      String cmd = "D:\\Program Files\\Microsoft Office\\OFFICE11\\WINWORD.EXE F:\\test.doc";  
//      String cmd = "cmd.exe /c start F:\\test.doc";  
        String cmd = "sh /Users/yuanchi/Documents/git/EliIE/wrapper_for_parsing.sh";  
        InputStream in = null;  
        try {  
            Process pro = Runtime.getRuntime().exec(new String[]{"sh",  
                                     "/Users/yuanchi/Documents/git/EliIE/yccall.sh","/Users/yuanchi/Documents/TestEli",  
                                     "temp.txt","/Users/yuanchi/Documents/TestEli"});  
            pro.waitFor();  
            in = pro.getInputStream();  
            BufferedReader read = new BufferedReader(new InputStreamReader(in));  
            String result = read.readLine();  
            System.out.println("INFO:"+result);  
             result = read.readLine();  
            System.out.println("INFO:"+result);  
            result = read.readLine();  
            System.out.println("INFO:"+result); 
            result = read.readLine();  
            System.out.println("INFO:"+result); 
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}  
