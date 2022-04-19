package edu.columbia.dbmi.ohdsims.util;

import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;

public class FileUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub


		System.out.println(Readfile("edu/columbia/dbmi/ohdsims/model/phraseNormalization.txt"));
		
	}
	
	public static String Readfile(String path){
		try {
			Resource fileRource = new ClassPathResource(path);
			StringBuffer readsb = new StringBuffer();
			//InputStream in = new FileInputStream(new File(path));
			InputStream in = new FileInputStream(fileRource.getFile());
			ArrayList<String> alist = new ArrayList<String>();

			int count;
			byte[] b = new byte[1024 * 1024];
			while ((count = in.read(b)) != -1) {
				if (count != b.length) {
					byte[] t = new byte[count];
					for (int i = 0; i < count; ++i)
						t[i] = b[i];
					readsb.append(new String(t));
				} else
					readsb.append(new String(b));
			}
			in.close();
			return readsb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static int write2File(String fileName, String content) {
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
	
	public static int add2File(String fileName, String content) {
		File file = new File(fileName);
		try {
			// if the file is not exist, create it!
			if (file.exists() == false) {
				file.createNewFile();

			}
			// the second parameter is 'true' means add contents at the end of
			// the file
			FileWriter writer = new FileWriter(fileName,true);
			writer.write(content);
			writer.close();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

	}


}
