package updated;

/*
 * Parser Stock value by Date
 * version: October 02, 2019 09:30 PM
 * Last revision: November 25, 2019 08:55 PM
 * 
 * Author : Chao-Hsuan Ke
 * E-mail : phelpske.dev at gmail dot com
 * 
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;


public class GetValueandProcessing_StockValue 
{
	private String ADDate_pattern = "yyyyMMdd";
	DateFormat df = new SimpleDateFormat(ADDate_pattern, Locale.getDefault());	
	
	String today_str;
	String date_str;
	boolean datecheck;
	String value_str;
	
	//https://www.twse.com.tw/exchangeReport/MI_INDEX?response=json&date=20191001&type=IND
	String URLBasic = "https://www.twse.com.tw/exchangeReport/MI_INDEX?response=json&date=";
	String URL;
	// Get JsonResponse
	String sourceLine = "";
	// Storage
//	File file;
//	BufferedWriter writer;
	
	// Return 
	private Vector returnDate;
	private Vector returnValue;
	
	public GetValueandProcessing_StockValue(String startDate, int sleepTime) throws Exception
	{
		// Today
		Today();
						
		String start = startDate;
		String end = today_str;
		SimpleDateFormat sdf = new SimpleDateFormat(ADDate_pattern);
		Date dBegin = sdf.parse(start);
		Date dEnd = sdf.parse(end);
		List<Date> lDate = findDates(dBegin, dEnd);
		for (Date date : lDate) 
		{
			date_str = "";
			datecheck = false;
			value_str = "";
			date_str = sdf.format(date).toString();
			
//			FileWriter writer = new FileWriter(sourceFolder + StockValue, true); 
			
			URL = URLBasic;
			URL += date_str + "&type=IND";
			//System.out.println(URL);
			getValue(URL, date_str);
			//System.out.println(date_str+"	"+value_str);
			
			// write file
//			if(datecheck) {
//				writer.write(date_str+"	"+value_str+"\n");
//			}else{
//				writer.write(date_str+"	"+"false"+"\n");
//			}
//			
//			writer.close();
			
			System.out.println(date_str+"	"+datecheck+"	"+value_str);
			URL = "";
			
			Thread.sleep((int) sleepTime);
		}
		
	}
	
	private void Today() 
	{
		Date today = Calendar.getInstance().getTime();
		today_str = df.format(today.getTime());
	}
	
	private static List<Date> findDates(Date dBegin, Date dEnd) 
	{
		List lDate = new ArrayList();
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();

		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();

		calEnd.setTime(dEnd);

		while (dEnd.after(calBegin.getTime())) {

			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(calBegin.getTime());
		}
		
		return lDate;
	}
	
	private void getValue(String url, String date_str)
	{
		try {
			HttpsGet https = new HttpsGet();
			
			if(https.responseJSON(URL).isEmpty() == false) {
				sourceLine = https.responseJSON(URL);
				
				// Parsing
				Pasring(sourceLine, date_str);
			}
								
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void Pasring(String Line, String date_str) throws Exception
	{
		JSONObject obj = new JSONObject(Line);
		
		// Value
		if(obj.has("params")) {
			JSONObject objPara = new JSONObject(obj.get("params").toString());
			if(objPara.has("date")) {
				//System.out.println(objPara.get("date"));
				datecheck = true;
			}
		}
		
		if(datecheck) {
			if(obj.has("data3")) {
				JSONArray jsonarray = new JSONArray(obj.get("data1").toString());
				for(int i=0; i<jsonarray.length(); i++){
					JSONArray arrayData = new JSONArray(jsonarray.get(i).toString());
					if(arrayData.get(0).toString().equalsIgnoreCase("發行量加權股價指數")) {
						//System.out.println(arrayData.get(i));
						value_str = arrayData.get(i).toString();
					}
				}
			}
		}
		
	}
	
}
