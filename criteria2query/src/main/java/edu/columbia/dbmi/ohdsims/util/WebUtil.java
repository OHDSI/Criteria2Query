package edu.columbia.dbmi.ohdsims.util;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.columbia.dbmi.ohdsims.pojo.Concept;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



public class WebUtil {
	public static final String ADD_URL = "http://api.ohdsi.org/WebAPI/cdmresults/1PCT/conceptRecordCount";
	public static void main(String[] args){
		try {
//			String query="diabetes%20mellitus";
//			//query = URLEncoder.encode(query, "utf-8");
//			doGet("http://api.ohdsi.org/WebAPI/vocabulary/1PCT/search/"+query,null);
			String query2="diabetes mellitus";
			
//			Concept concept=ATLASUtil.getMaxCountConcept(query2, "Condition");
//			System.out.println("CONCEPT_NAME="+concept.getCONCEPT_NAME());
////			doGet("http://api.ohdsi.org/WebAPI/cdmresults/1PCT/conceptRecordCount/diabetes",null);
//			JSONArray jsonArray = new JSONArray();
//	        jsonArray.add(0, 37396752);
////	        jsonArray.add(1, 45446447);
////	        jsonArray.add(2, 45426562);
//	        System.out.println( jsonArray);
////			String result=sendPost("http://api.ohdsi.org/WebAPI/cdmresults/1PCT/conceptRecordCount",jsonArray.toString());
////			System.out.println("result="+result);
////			appadd();
//			JSONObject winnerJSONObject = new JSONObject();
//		    JSONObject loserJSONObject = new JSONObject();
//
//		
//
//
//		    DefaultHttpClient httpClient = new DefaultHttpClient();
//		    HttpPost httpPost = new HttpPost(ADD_URL);
//		    HttpResponse httpResponse = null;
//
//		    try {
//		        httpPost.setHeader("content-type", "application/json");
//		       // httpPost.setHeader("Accept", "application/json");
//		       httpPost.setEntity(new StringEntity(jsonArray.toString()));
//		       // httpPost.setEntity(new StringEntity(jsonArray.toString(), HTTP.UTF_8));
//		    } catch (UnsupportedEncodingException e) {
//		        e.printStackTrace();
//		    }
//
//		    try {
//		        httpResponse = httpClient.execute(httpPost);
//		      //8：通过响应对象获取响应码
//		        int code = httpResponse.getStatusLine().getStatusCode();
//		        //9：如果响应码为200（成功响应码），则获取服务器返回的数据
//		        if(code == 200){
//		        	System.out.println("succeed!");
//		            //9.1：获取HttpEntity对象（通过响应来获取）
//		            HttpEntity entity2 = httpResponse.getEntity();
//		            //10：使用EntityUtils工具类，将获取到的数据（实体）转换为字节数组形式，任何文件都可以以字节的形式保存
//		           
//		            byte[] b = EntityUtils.toByteArray(entity2);
//		            System.out.println(b.length);
//		            //输出内容
//		            System.out.println(new String(b, "utf-8"));
//		        }
//		    } catch (IOException e) {
//		        e.printStackTrace();
//		    }
		    
		   
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	/**
	 * @return 
	 */
	public static String getCTByNctid(String nctid) {
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod("https://clinicaltrials.gov/show/" + nctid + "?displayxml=true");
		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + getMethod.getStatusLine());
			}
			// 读取内容
			byte[] responseBody = getMethod.getResponseBody();
			String str = new String(responseBody);
			return str;
		} catch (HttpException e) {
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
	    return null;

	}

	public static String[] parse(String protocolXML) {

		try {
			String[] arr=new String[7];
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(protocolXML)));
			NodeList list = doc.getElementsByTagName("eligibility");
			for (int i1 = 0; i1 < list.getLength(); i1++) {
				Element element = (Element) list.item(i1);
				String content = element.getElementsByTagName("criteria").item(0).getTextContent();
				String gender;
				if(element.getElementsByTagName("gender").item(0)!=null){
					gender=element.getElementsByTagName("gender").item(0).getTextContent();
				}else{
					gender="";
				}
				String minimum_age;
				if(element.getElementsByTagName("minimum_age").item(0)!=null){
					minimum_age=element.getElementsByTagName("minimum_age").item(0).getTextContent();
				}else{
					minimum_age="";
				}
				String maximum_age;
				if(element.getElementsByTagName("maximum_age").item(0)!=null){
					maximum_age=element.getElementsByTagName("maximum_age").item(0).getTextContent();
				}else{
					maximum_age="";
				}
				String sampling_method;
				if(element.getElementsByTagName("sampling_method").item(0)!=null){
					sampling_method = element.getElementsByTagName("sampling_method").item(0).getTextContent();
				}else{
					sampling_method="";
					}
				String study_pop;
				if(element.getElementsByTagName("study_pop").item(0)!=null){
				 study_pop = element.getElementsByTagName("study_pop").item(0).getTextContent();
				}else{
					study_pop="";
				}
				String healthy_volunteers;
				if(element.getElementsByTagName("healthy_volunteers").item(0)!=null){
					healthy_volunteers=element.getElementsByTagName("healthy_volunteers").item(0).getTextContent();
				}else{
					healthy_volunteers="";
				}
				System.out.println("gender="+gender);
				System.out.println("mini="+minimum_age);
				System.out.println("max="+maximum_age);
				System.out.println("sampling_method="+sampling_method);
				System.out.println("study_pop="+study_pop.replaceAll("\\s+", " "));
				arr[0]=content;
				arr[1]=gender;
				arr[2]=minimum_age;
				arr[3]=maximum_age;
				arr[4]=sampling_method;
				arr[5]=study_pop.replaceAll("\\s+", " ");
				arr[6]=healthy_volunteers;
				//content = content.replaceAll("\\s+", " ");
				return arr;
				// System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public static String post() {

		try {
//            JSONObject  obj = new JSONObject();
//            obj.accumulate("data", 40386357);
			 JSONArray jsonArray = new JSONArray();
		        jsonArray.add(0, 40386357);
		        jsonArray.add(1, 45446447);
		        jsonArray.add(2, 45426562);
		        System.out.println("jsonArray1：" + jsonArray);
            
//                    obj.append("app_name", "全民大讨论");
//                    obj.append("app_ip", "10.21.243.234");
//                    obj.append("app_port", 8080);
//                    obj.append("app_type", "001");in
//                    obj.append("app_area", "asd");

                System.out.println(jsonArray);
            // 创建url资源
            URL url = new URL("http://api.ohdsi.org/WebAPI/cdmresults/1PCT/conceptRecordCount");
            // 建立http连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置文件字符集:
//            conn.setRequestProperty("Charset", "UTF-8");
            //转换为字节数组
            byte[] data = jsonArray.toString().getBytes();//obj.toString()
            // 设置文件长度
            //conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            conn.setRequestProperty("contentType", "application/json");
            // 开始连接请求
            conn.connect();
            OutputStream  out = conn.getOutputStream();     
            // 写入请求的字符串
            out.write((jsonArray.toString()).getBytes());
            out.flush();
            out.close();

            System.out.println(conn.getResponseCode());

            // 请求返回的状态
            if (conn.getResponseCode() == 200) {
                System.out.println("连接成功");
                // 请求返回的数据
                InputStream in = conn.getInputStream();
                String a = null;
                try {
                    byte[] data1 = new byte[in.available()];
                    in.read(data1);
                    // 转成字符串
                    a = new String(data1);
                
                    System.out.println(a);
                    return a;
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return null;
                }
            } else {
                System.out.println("no++");
                return null;
            }

        } catch (Exception e) {
        	return null;
        }
    }
	 public static String doGet(String url, String charset) throws Exception {  
		  System.out.println("url="+url);
	        HttpClient client = new HttpClient();  
	        GetMethod method = new GetMethod(url);  
	  
	        if (null == url || !url.startsWith("http")) {  
	            throw new Exception("请求地址格式不对");  
	        }  
	        // 设置请求的编码方式  
	        if (null != charset) {  
	            method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + charset);  
	        } else {  
	            method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + "utf-8");  
	        }  
	        int statusCode = client.executeMethod(method);  
	  
	        if (statusCode != HttpStatus.SC_OK) {// 打印服务器返回的状态  
	            System.out.println("Method failed: " + method.getStatusLine());  
	        }  
	        
	        byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());  
	        // 在返回响应消息使用编码(utf-8或gb2312)  
	        String response = new String(responseBody, "utf-8"); 
	       
	    
	        
	        System.out.println("------------------response:" + response);  
	        // 释放连接  
	        method.releaseConnection();  
	        return response;  
	    }  
	    public static void appadd() {

	        try {
	            //创建连接
	            URL url = new URL(ADD_URL);
	            HttpURLConnection connection = (HttpURLConnection) url
	                    .openConnection();
	            connection.setDoOutput(true);
	            connection.setDoInput(true);
	            connection.setRequestMethod("POST");
	            connection.setUseCaches(false);
	            connection.setInstanceFollowRedirects(true);
	            connection.setRequestProperty("content-type",
	                    "application/json");

	            connection.connect();

	            //POST请求
	            DataOutputStream out = new DataOutputStream(
	                    connection.getOutputStream());
//	            JSONObject obj = new JSONObject();
//	            obj.element("app_name", "asdf");
//	            obj.element("app_ip", "10.21.243.234");
//	            obj.element("app_port", 8080);
//	            obj.element("app_type", "001");
//	            obj.element("app_area", "asd");
				JSONArray jsonArray = new JSONArray();
		        jsonArray.add(0, 40386357);
		        jsonArray.add(1, 45446447);
		        jsonArray.add(2, 45426562);
		        System.out.println("jsonArray1：" + jsonArray);

	            out.writeBytes(jsonArray.toString());
	            out.flush();
	            out.close();

	            //读取响应
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String lines;
	            StringBuffer sb = new StringBuffer("");
	            while ((lines = reader.readLine()) != null) {
	                lines = new String(lines.getBytes(), "utf-8");
	                sb.append(lines);
	            }
	            System.out.println(sb);
	            reader.close();
	            // 断开连接
	            connection.disconnect();
	        } catch (MalformedURLException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	           System.out.println(e.getMessage());
	        } catch (UnsupportedEncodingException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            System.out.println(e.getMessage());
	         
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            System.out.println(e.getMessage());
	        }

	    }
		
}
