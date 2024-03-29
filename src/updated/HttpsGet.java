package updated;

/*
 * https GET
 * 
 * version: June 29, 2019 09:30 AM
 * Last revision: June 30, 2019 12:23 PM
 * 
 * Author : Chao-Hsuan Ke
 * Email: phelpske.dev at gmail dot com
 */

/*
 * source from
 * https://blog.xuite.net/ray00000test/blog/63561879-http與https讀取網頁、下載檔案
 * 
 */

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import httpGet.HttpsReader;

public class HttpsGet 
{
	
	private String basic_pattern = "yyyyMMdd";
	DateFormat df = new SimpleDateFormat(basic_pattern, Locale.getDefault());	
	
	public HttpsGet() throws Exception
	{
		System.setProperty("file.encoding", "UTF-8");
    }
	
	public String responseJSON(String url) throws Exception
	{
		// https
		HttpsReader https = new HttpsReader();
		https.charSet = "utf-8";
		https.root = url;
		
		String cookie = ""; 		// cookie字串
		boolean isPost = false; 	// true表示用post送資料，false:get方式傳送

		BufferedReader buf = https.readyBuffer(cookie, isPost);

		String line = null;
		String response = "";
		while ((line = buf.readLine()) != null) {// 每次讀取一行
			response = line;
		}
		
		return response;
	}
	
}
