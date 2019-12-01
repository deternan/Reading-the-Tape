package updated;

/*
 * Parser values
 * version: November 30, 2019 09:59 PM
 * Last revision: December 01, 2019 01:26 PM
 * 
 * Author : Chao-Hsuan Ke
 * E-mail : phelpske.dev at gmail dot com
 * 
 */

/*
 * "fields":[
"日期",
"成交股數",
"成交金額",
"開盤價",
"最高價",
"最低價",
"收盤價",
"漲跌價差",
"成交筆數"
]
 * 
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GetValueandProcessingByStockId 
{
	// Values source
	private String TWSEvalueUrl = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=";
	private String TPEXvalueUrl = "https://www.tpex.org.tw/web/stock/aftertrading/daily_trading_info/st43_result.php?d=";
	private String ADDate_pattern = "yyyyMMdd";
	DateFormat df = new SimpleDateFormat(ADDate_pattern, Locale.getDefault());	
	
	String today_str;
	String date_str;
	
	private String ID;
	private String tag;
	private String startYear;
	private String startMonth;
	private String startDay;
	// Get JsonResponse
	private String sourceLine = "";
	
	// Date
	private int monthGap = 0;
	// storage
	ArrayList dateoutput = new ArrayList();
	ArrayList valueoutput = new ArrayList();
	
	public GetValueandProcessingByStockId(String startDate, int sleepTime, String ID, String tag) throws Exception 
	{
		
		this.ID = ID;
		this.tag = tag;
//		dataData_check = true;
		
		startYear = startDate.substring(0, 4);
		startMonth = startDate.substring(4, 6);
		startDay = startDate.substring(6, 8);
		
		// Date
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat sdf  = new SimpleDateFormat(ADDate_pattern);
		String todayStr = sdf.format(today);
		String specificDateStr = startYear + startMonth + startDay;
		
		monthGap = getMonthGap(todayStr, specificDateStr);
		
		List<String> monthList = MonthIncrement(startYear + startMonth, monthGap);
		
		for(int i=0; i<monthList.size(); i++)
		{
			sourceLine = "";
			
			// Processing and Storage
			// get data from URL
			if ("twse".equalsIgnoreCase(this.tag)) {
				GetValues(monthList.get(i) + startDay, this.tag);
				if (isJSONValid(sourceLine)) {
					Processing_TWSE(sourceLine);
				}
			} else if ("tpex".equalsIgnoreCase(this.tag)) {
				// GetValues(existDate.substring(0, 3) + "/"+ existDate.substring(3, 5),
				// this.tag);
				GetValues(startYear + "/" + startMonth, this.tag);
				if (isJSONValid(sourceLine)) {
					Processing_TPEX(sourceLine);
				}
			}		
			
			// Thread sleep
			Thread.sleep((int) sleepTime);
	
		}
		
		// Message
		System.out.println(this.ID+"	Finished");
	}

	
	private List<String> MonthIncrement(String startDate, int addMonths) throws Exception
	{
		List<String> monthList = new ArrayList();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
        
        for(int i=0; i<=addMonths; i++){
        	Date dt=sdf.parse(startDate);
        	Calendar rightNow = Calendar.getInstance();
        	rightNow.setTime(dt);
    		if(i != 0){
    			rightNow.add(Calendar.MONTH,1);
    		}
        	Date dt1=rightNow.getTime();
        	String reStr = sdf.format(dt1);
        	
        	startDate = reStr;
        	monthList.add(reStr);
        }
        
        return monthList;
	}

	// Date Processing
	private int getMonthGap(String d1, String d2) throws Exception 
	{
		SimpleDateFormat sdf = new SimpleDateFormat(ADDate_pattern);
		Calendar c = Calendar.getInstance();
		c.setTime(sdf.parse(d1));
		int year1 = c.get(Calendar.YEAR);
		int month1 = c.get(Calendar.MONTH);

		c.setTime(sdf.parse(d2));
		int year2 = c.get(Calendar.YEAR);
		int month2 = c.get(Calendar.MONTH);

		int result;
		if (year1 == year2) {
			result = month1 - month2;
		} else {
			result = 12 * (year1 - year2) + month1 - month2;
		}

		return result;
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
	
	private void GetValues(String DateStr, String tag)
	{
		sourceLine = "";
		String URL = "";
		if("twse".equalsIgnoreCase(tag)) {
			// TWSE
			// https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=20190101&stockNo=2388
			URL = TWSEvalueUrl + DateStr + "&stockNo=" + ID;
		}else if("tpex".equalsIgnoreCase(tag)) {
			// TPEX
			// https://www.tpex.org.tw/web/stock/aftertrading/daily_trading_info/st43_result.php?d=107/08&stkno=3105
			URL = TPEXvalueUrl + DateStr + "&stkno=" + ID;
		}
		
		
		try {
			HttpsGet https = new HttpsGet();
			
			if(https.responseJSON(URL).isEmpty() == false) {
				sourceLine = https.responseJSON(URL);
			}
								
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isJSONValid(String jsonInString) 
	{

		JsonParser parser = new JsonParser();
		JsonElement jsonele = parser.parse(jsonInString);
		boolean check;
		check = jsonele.isJsonObject();
		
		return check;
	}
	
	private void Processing_TWSE(String jsonStr) throws Exception
	{
		String dateStr;
		JSONObject obj = new JSONObject(jsonStr);
		if(obj.has("data")) {
			JSONArray jsonarray = new JSONArray(obj.get("data").toString());
			for(int i=0; i<jsonarray.length(); i++)
			{
				JSONArray arrayData = new JSONArray(jsonarray.get(i).toString());
				dateStr = arrayData.get(0).toString().replace("/", "");
				//System.out.println(arrayData.get(0)+"	"+arrayData.get(6)+"	"+dateStr);
				dateoutput.add(dateStr);
				valueoutput.add(arrayData.get(6).toString());
			}
		}
	}
	
	private void Processing_TPEX(String jsonStr) throws Exception
	{
		String dateStr;
		JSONObject obj = new JSONObject(jsonStr);
		if(obj.has("aaData")) {
			JSONArray jsonarray = new JSONArray(obj.get("aaData").toString());
			for(int i=0; i<jsonarray.length(); i++)
			{
				JSONArray arrayData = new JSONArray(jsonarray.get(i).toString());
				//Storage(arrayData.get(0).toString(), arrayData.get(6).toString());
				dateStr = arrayData.get(0).toString().replace("/", "");
				dateoutput.add(dateStr);
				valueoutput.add(arrayData.get(6).toString());
			}
		}
	}
	
	public ArrayList ReturnDate()
	{
		return dateoutput;
	}
	
	public ArrayList ReturnValue()
	{
		return valueoutput;
	}
	
}
