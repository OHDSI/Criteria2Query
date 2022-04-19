package edu.columbia.dbmi.ohdsims.util;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HttpUtil {
	//使用HttpClient发送请求、接收响应
	public static String doPost(String url, String content) {
		try {
			//创建POST方式请求对象
			HttpPost httppost = new HttpPost(url);
			//设置header信息
			httppost.setHeader("Content-Type", "application/json");
			//Creates a StringEntity with the specified content.
			StringEntity se = new StringEntity(content);
			//设置请求参数
			httppost.setEntity(se);
			//HttpResponse httpresponse = new DefaultHttpClient().execute(proxy,httppost);
			HttpResponse httpresponse = new DefaultHttpClient().execute(httppost);
			System.out.println(httpresponse.getStatusLine().getStatusCode());
			if (httpresponse.getStatusLine().getStatusCode() == 200) {
				//HttpResponse的getEntity(): 可获取HttpEntity对象，该对象包装了服务器的响应内容
				//按指定编码转换结果实体为String类型
				String strResult = EntityUtils.toString(httpresponse.getEntity());
				return strResult;
			} else {
				return null;
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
	}

	//Use HttpClient to make a Get request and get repsonse
	public static String doGet(String url) {
		try {
			
			HttpGet httpget = new HttpGet(url);
			//HttpResponse httpresponse = new DefaultHttpClient().execute(proxy,httpget);
			HttpResponse httpresponse = new DefaultHttpClient().execute(httpget);
			if (httpresponse.getStatusLine().getStatusCode() == 200) {
				//HttpResponse的getEntity(): 可获取HttpEntity对象，该对象包装了服务器的响应内容
				//按指定编码转换结果实体为String类型
				String strResult = EntityUtils.toString(httpresponse.getEntity());
				return strResult;
			} else {
				return null;
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}

	}

	public static void doPut(String urlstr,String json) {
		try {
	        URL url = new URL(urlstr);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("PUT");
	        connection.setDoOutput(true);
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty("Accept", "application/json");
	        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
	        osw.write(json);
	        osw.flush();
	        osw.close();
	        System.err.println(connection.getResponseCode());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());

		}
	}
	
	public static void doDelete(String urlstr,String json) {
		try {
			
	        URL url = new URL(urlstr);
	        System.out.println("delete!!!");
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("DELETE");
	        connection.setDoOutput(true);
//	        connection.setRequestProperty("Content-Type", "application/json");
//	        connection.setRequestProperty("Accept", "application/json");
	        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
	        osw.write(json);
	        osw.flush();
	        osw.close();
	        System.out.println("code="+connection.getResponseCode());
	        System.err.println(connection.getResponseCode());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());

		}
	}
	//
	
//	public static void doPost(String urlstr,String json) {
//		try {
//	        URL url = new URL(urlstr);
//	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//	        connection.setRequestMethod("POST");
//	        connection.setDoOutput(true);
//	        connection.setRequestProperty("Content-Type", "application/json");
//	        connection.setRequestProperty("Accept", "application/json");
//	        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
//	        osw.write(json);
//	        osw.flush();
//	        osw.close();
//	        System.err.println(connection.getResponseCode());
//	        System.out.println(connection.getContent());
//		} catch (Exception ex) {
//			System.out.println(ex.getMessage());
//
//		}
//	}
	
	
}
