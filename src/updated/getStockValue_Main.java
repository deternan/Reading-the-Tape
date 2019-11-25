package updated;

/*
 * Parser Stock value by Date
 * version: October 02, 2019 09:30 PM
 * Last revision: November 25, 2019 08:40 PM
 * 
 * Author : Chao-Hsuan Ke
 * E-mail : phelpske.dev at gmail dot com
 * 
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;


public class getStockValue_Main 
{
	private String definedStartDate = "20190101";		// start date
	private static int sleepTime = 5200;				// 5.2 secs
//	private String outputFolder = "";					// output folder
//	private String StockValue = "value.txt";			// output file name
	private String extensionName = ".txt";
	private String ADDate_pattern = "yyyyMMdd";
	private DateFormat df = new SimpleDateFormat(ADDate_pattern, Locale.getDefault());
	
	
	// Parameter (should been changed)
	private String stockidlistFile = "D:\\Phelps\\GitHub\\Reading-the-Tape\\data\\idlist.txt";		// JFrame choice
	private String stockvalueFolder = "D:\\Phelps\\GitHub\\PTT_Stock\\output\\Values\\";
		// Parameters Array
	 	ArrayList stockidlist = new ArrayList();
	 	// Date
	 	String today_str;
	 	String TWDate;
	 	String lastDate;
	 	String ADlastDate;
	 	String startDate;
	 		boolean updatecheck;

	//
	private Vector oldDate = new Vector();
	private Vector oldValue = new Vector();
//	private Vector newDate = new Vector();
	 		
	public getStockValue_Main() throws Exception
	{		
		
		// should been done
			// Frame (adjusted target list id)
		
		// Read target list
		Read_definedStockIdList();
		// Read stock value
		Today();
		
//		for(int i=0; i<stockidlist.size(); i++) 
		{
			oldDate.clear();
			oldValue.clear();
			lastDate = "";
			ADlastDate = "";
			startDate = "";
			
			//File valuefile = new File(stockvalueFolder + stockidlist.get(i));
			File valuefile = new File(stockvalueFolder + stockidlist.get(0) + extensionName);
			if(valuefile.exists()) {
				Read_specific_stock_value(stockidlist.get(0).toString());			
				
				// Transfer Date from AD to TW
				TWDate = convertTWDate(today_str);			
				updatecheck = DateComparison(lastDate, TWDate);
				//System.out.println(updatecheck+"	"+lastDate +"	"+ today_str+"	"+TWDate);
				
				ADlastDate = convertADDate(lastDate);			
				// data list (get start date)
				datelistShow(ADlastDate, today_str);
				
			}else {
				//System.out.println("no value data");
				
				startDate = definedStartDate;				
			}
			
			// start to parse the stock value
			GetValueandProcessing_StockValue stockvalue = new GetValueandProcessing_StockValue(startDate, sleepTime);
		}
		
	}
	
	private void Read_definedStockIdList() throws Exception
	{
		String Line = "";
		
		File file = new File(stockidlistFile);
		if(file.exists()) {
			FileReader fr = new FileReader(stockidlistFile);
			BufferedReader bfr = new BufferedReader(fr);
					
			while((Line = bfr.readLine())!=null){									
				stockidlist.add(Line);
			}
			
			fr.close();
			bfr.close();
		}else {
			System.out.println("No stock id list");
		}
		
	}
	
	private void Read_specific_stock_value(String stockId) throws Exception
	{
		String Line = "";
		
		File file = new File(stockvalueFolder + stockId + extensionName);
		
		// check whether file exist?
		if(file.exists()) {
			FileReader fr = new FileReader(stockvalueFolder + stockId + extensionName);
			BufferedReader bfr = new BufferedReader(fr);
			String tmp[];	
			while((Line = bfr.readLine())!=null)
			{									
				//System.out.println(Line);
				tmp = Line.split("\\t");
				
				oldDate.add(tmp[0]);
				oldValue.add(tmp[1]);
				
				lastDate = tmp[0];			
			}
			
			fr.close();
			bfr.close();
		}else {
			System.out.println("No "+ stockId + "value");
		}
		
		//System.out.println(lastDate);
	}
	
	private void Today() 
	{
		Date today = Calendar.getInstance().getTime();
		today_str = df.format(today.getTime());
	}
	
	private String convertTWDate(String AD) 
	{
	    SimpleDateFormat df4 = new SimpleDateFormat(ADDate_pattern);
	    SimpleDateFormat df2 = new SimpleDateFormat("MMdd");
	    Calendar cal = Calendar.getInstance();
	    String returnDate;
	    try {
	        cal.setTime(df4.parse(AD));
	        cal.add(Calendar.YEAR, -1911);
	        returnDate = Integer.toString(cal.get(Calendar.YEAR)) + df2.format(cal.getTime());
	        
	        return returnDate;	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	private boolean DateComparison(String specificDate, String todayDate) throws Exception
	{
		SimpleDateFormat sdf=new SimpleDateFormat(ADDate_pattern);
		Date a = sdf.parse(specificDate);
		Date b = sdf.parse(todayDate);
		
		boolean returnBoolean;
		if(a.before(b)) {
			returnBoolean = true;
		}else if(a.equals(b)){
			returnBoolean = false;
		}else {
			returnBoolean = false;
		}
		
		return returnBoolean;
	}
	
	private String convertADDate(String TW)
	{
		// 1080101
		// 990101
		String yearTmp;
		String monthdayTmp = "";
		int yearInt = 0;
		String yearStr;
		String returnDate = "";
		if(TW.trim().length() == 7) {
			yearTmp = TW.substring(0, 3);
			monthdayTmp = TW.substring(3, TW.length());
			yearInt = Integer.parseInt(yearTmp);
			yearInt += 1911;
		}else if(TW.trim().length() == 6) {
			yearTmp = TW.substring(0, 2);
			monthdayTmp = TW.substring(2, TW.length());
			yearInt = Integer.parseInt(yearTmp);
			yearInt += 1911;
		}
		
		yearStr = String.valueOf(yearInt);
		returnDate = yearStr + monthdayTmp;
		
		return returnDate; 
	}
	
	private void datelistShow(String specificDate, String todayDate) throws Exception
	{		
		String start = specificDate;
		String end = todayDate;
		//System.out.println(start+"	"+end);
		
		SimpleDateFormat sdf = new SimpleDateFormat(ADDate_pattern);		
		Date dBegin = sdf.parse(start);
		Date dEnd = sdf.parse(end);
		List<Date> lDate = findDates(dBegin, dEnd);
		//for (Date date : lDate) 
		
		startDate = sdf.format(lDate.get(1));
		
//		for(int i=1; i<lDate.size(); i++)
//		{
//			String dateString = sdf.format(lDate.get(i));			 
//			newDate.add(dateString);
//			System.out.println(date+"	"+dateString);			
//		}
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
	
	public static void main(String args[])
	{
		try {
			getStockValue_Main gsv = new getStockValue_Main();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
