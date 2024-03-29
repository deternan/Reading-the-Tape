package updated;

/*
 * Parser Stock value by Date
 * version: October 02, 2019 09:30 PM
 * Last revision: December 06, 2019 10:28 PM
 * 
 * Author : Chao-Hsuan Ke
 * E-mail : phelpske.dev at gmail dot com
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class updatedStockValue_Main 
{
	// Parameters
	private String definedStartDate = "20190101";		// start date
	private static int sleepTime = 5200;				// 5.2 secs
//	private String outputFolder = "";					// output folder
//	private String StockValue = "value.txt";			// output file name
	private String extensionName = ".txt";
	private String ADDate_pattern = "yyyyMMdd";
	private DateFormat df = new SimpleDateFormat(ADDate_pattern, Locale.getDefault());
	
	
	// Parameter (should been changed)
	// My Mac
	private String stockidlistFile = "/Users/phelps/Documents/github/Reading-the-Tape/data/idlist.txt";
	private String stockvalueFolder = "/Users/phelps/Documents/github/PTT_Stock/output/Values/";
	private String twselistFile = "/Users/phelps/Documents/github/PTT_Stock/source/TWSE.txt";
	private String tpexlistFile = "/Users/phelps/Documents/github/PTT_Stock/source/TPEX.txt";
	// Windows
//	private String stockidlistFile = "D:\\Phelps\\GitHub\\Reading-the-Tape\\data\\idlist.txt";		// JFrame choice
//	private String stockvalueFolder = "D:\\Phelps\\GitHub\\PTT_Stock\\output\\Values\\";
		private boolean idlist_check = false;
		private boolean twselist_check = false;
		private boolean tpexlist_check = false;
		// Parameters Array
	 	ArrayList stockidlist = new ArrayList();
	 	ArrayList twseidlist = new ArrayList();
	 	ArrayList tpexidlist = new ArrayList();
	 	// Date
	 	String today_str;
	 	String TWDate;
	 	String lastDate;
	 	String ADlastDate;
	 	String startDate;
	 		boolean updatecheck;
	 		String stockTag = "";
	 		
	// past record data
	private ArrayList oldDate = new ArrayList();
	private ArrayList oldValue = new ArrayList();
	// get new data
	GetValueandProcessingByStockId stockvaluebyId;
	ArrayList dateoutput = new ArrayList();
	ArrayList valueoutput = new ArrayList();
	int dataindex;
	 		
	public updatedStockValue_Main() throws Exception
	{		
		
		// should been done
			// Frame (adjusted target list id)
		
		// Read target list
		Read_definedStockIdList();
		// Read TWSE list
		Read_TWSE();
		// Read TPEX list
		Read_TPEX();
		
		
		if(idlist_check) {
			
			// Read stock value
			Today();
			String stockId;
			for(int i=0; i<stockidlist.size(); i++) 
			{
				stockId = stockidlist.get(i).toString();
				oldDate.clear();
				oldValue.clear();
				lastDate = "";
				ADlastDate = "";
				startDate = "";
				dataindex = 0;
				
				
				// Judge TWSE or TPEX
				stockTag = checkstockId_Tag(stockId);
				
				File valuefile = new File(stockvalueFolder + stockId + extensionName);
				if(valuefile.exists()) {
					Read_specific_stock_value(stockId);			
					
					// Transfer Date from AD to TW
					TWDate = convertTWDate(today_str);			
					updatecheck = DateComparison(lastDate, TWDate);
					
					ADlastDate = convertADDate(lastDate);			
					// data list (get start date)
					datelistShow(ADlastDate, today_str);
					
				}else {
					System.out.println("no value data");
					startDate = definedStartDate;		
				}
				
				// start to parse the stock value by Id
				stockvaluebyId = new GetValueandProcessingByStockId(startDate, sleepTime, stockId, stockTag);
				
				dateoutput = stockvaluebyId.ReturnDate();
				valueoutput = stockvaluebyId.ReturnValue();
				
				// updated data (source file)
				FileWriter writer = new FileWriter(stockvalueFolder + stockId + extensionName, true);
				
				// check date
				checkDateduplication(lastDate);
				
				if(dataindex > 0) {
					
					for (int j=(dataindex+1); j<dateoutput.size(); j++) 
					{
						writer.write(dateoutput.get(j) + "	" + valueoutput.get(j)+"\n");
					}
				}else {
					for (int j=0; j<dateoutput.size(); j++) 
					{
						writer.write(dateoutput.get(j) + "	" + valueoutput.get(j)+"\n");
					}
				}
				
				writer.close();
			}
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
			
			idlist_check = true;
			
		}else {
			System.out.println("No stock id list");
			idlist_check = false;
		}
		
	}
	
	private void Read_TWSE() throws Exception 
	{
		String Line = "";
		String tmp[];
		
		File file = new File(twselistFile);
		if (file.exists()) {
			FileReader fr = new FileReader(twselistFile);
			BufferedReader bfr = new BufferedReader(fr);

			while ((Line = bfr.readLine()) != null) {
				tmp = Line.split("\t");
				twseidlist.add(tmp[0]);
			}

			fr.close();
			bfr.close();

			twselist_check = true;

		} else {
			System.out.println("No TWSE id list");
			twselist_check = false;
		}
	}
	
	private void Read_TPEX() throws Exception
	{
		String Line = "";
		String tmp[];

		File file = new File(tpexlistFile);
		if (file.exists()) {
			FileReader fr = new FileReader(tpexlistFile);
			BufferedReader bfr = new BufferedReader(fr);

			while ((Line = bfr.readLine()) != null) {
				tmp = Line.split("\t");
				tpexidlist.add(tmp[0]);
			}

			fr.close();
			bfr.close();

			tpexlist_check = true;

		} else {
			System.out.println("No TPEX id list");
			tpexlist_check = false;
		}
	}
	
	private String checkstockId_Tag(String id)
	{
		String Tag = "";
		boolean checkTag = false;
		
		for(int i=0; i<twseidlist.size(); i++) {
			if(id.equalsIgnoreCase(twseidlist.get(i).toString())) {
				Tag = "twse";
				checkTag = true;
				break;
			}
		}
		
		if(checkTag == false) {
			for(int i=0; i<tpexidlist.size(); i++) {
				if(id.equalsIgnoreCase(tpexidlist.get(i).toString())) {
					Tag = "tpex";
					break;
				}
			}
		}
		
		return Tag;
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
		
		SimpleDateFormat sdf = new SimpleDateFormat(ADDate_pattern);		
		Date dBegin = sdf.parse(start);
		Date dEnd = sdf.parse(end);
		List<Date> lDate = findDates(dBegin, dEnd);
		
		if(lDate.size() >  1) {
			startDate = sdf.format(lDate.get(1));
		}else {
			startDate = sdf.format(lDate.get(0));
		}
		//System.out.println(lDate.size()+"	"+startDate);
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
	
	private void checkDateduplication(String oldlastDate)
	{
		for(int i=0; i<dateoutput.size(); i++)
		{
			if(oldlastDate.equalsIgnoreCase(dateoutput.get(i).toString())) {
				dataindex = i;
				break;
			}
		}
		
	}
	
	public static void main(String args[])
	{
		try {
			updatedStockValue_Main gsv = new updatedStockValue_Main();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
