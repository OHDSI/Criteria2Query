package edu.columbia.dbmi.ohdsims.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CmdUtil {
	public static int save2File(String fileName, String content) {
		File file = new File(fileName);
		try {
			// if the file is not exist, create it!
			if (file.exists() == false) {

				file.createNewFile();

			}
			// the second parameter is 'true' means add contents at the end of
			// the file
			FileWriter writer = new FileWriter(fileName);
			writer.write(content);
			writer.close();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

	}
	
public static String callEliIE(String file){
	InputStream in = null;  
    try { 
    	long startTime = System.currentTimeMillis();
        Process pro = Runtime.getRuntime().exec(new String[]{"sh",  
                                 "/Users/yuanchi/Documents/git/EliIE/yccall.sh","/Users/yuanchi/Documents/git/EliIE/Tempfile",  
                                 "EliIE_input_free_text.txt","/Users/yuanchi/Documents/git/EliIE/Tempfile"});  
        pro.waitFor();  
        long endTime=System.currentTimeMillis();
        System.out.println("Call API time (unit:millisecond)：" + (endTime - startTime));
        in = pro.getInputStream();  
        BufferedReader read = new BufferedReader(new InputStreamReader(in)); 
        String line;  	
        StringBuffer result=new StringBuffer();
        boolean start=false;
        boolean end=false;
        while ((line = read.readLine()) != null) {
			if (line.equals("<root>")) {
				start = true;
				result.append(line);
				continue;
			}
			else if(line.equals("</root>")) {
				end = true;
				result.append(line);
				continue;
			}
			if (start == true&&end==false) {
				result.append(line);
			}
        }
        //System.out.println("result="+result.toString());
        //XMLUtil.parseXML(result.toString());
       return result.toString();
    } catch (Exception e) {  
        e.printStackTrace();  
        return null;
    }  
}
}
