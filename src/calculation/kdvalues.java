package calculation;

/*
 * Parser Stock value by Date
 * version: December 07, 2019 00:01 AM
 * Last revision: December 07, 2019 01:00 AM
 * 
 * Author : Chao-Hsuan Ke
 * E-mail : phelpske.dev at gmail dot com
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class kdvalues 
{
	private String extensionName = ".txt";
	
	// Parameter (should been changed)
	// My Mac
	private String stockvalueFolder = "/Users/phelps/Documents/github/PTT_Stock/output/Values/";
	// Windows
	
	private boolean valuefile_check = false;
	
	ArrayList dateoutput = new ArrayList();
	ArrayList valueoutput = new ArrayList();
	
	String endDate;
	String endValue;
	
	double RSV;
	//double lastValue;
	ArrayList dateoutputValue = new ArrayList();
	
	double lastRSV;
	ArrayList lastdateoutputValue = new ArrayList();
	
	public kdvalues(String startDate, String stockID) throws Exception
	{
		valuefile_check = false;
		dateoutput.clear();
		valueoutput.clear();
		
		// Read stock value
		Read_stockvalues(stockID);
		System.out.println(endDate);
		if(valuefile_check) {
			// RSV
			RSV();
			lastRSV();
		}else {
			
		}
		
	}

	private void Read_stockvalues(String stockID) throws Exception
	{
		String Line = "";
		String tmp[];
		
		File file = new File(stockvalueFolder + stockID + extensionName);
		if(file.exists()) {
			FileReader fr = new FileReader(stockvalueFolder + stockID + extensionName);
			BufferedReader bfr = new BufferedReader(fr);
					
			while((Line = bfr.readLine())!=null){									
				//stockidlist.add(Line);
				tmp = Line.split("\\t");
				dateoutput.add(tmp[0]);
				valueoutput.add(tmp[1]);
				endDate = tmp[0];
				endValue = tmp[1];
			}
			
			fr.close();
			bfr.close();
			
			valuefile_check = true;
			
		}else {
			System.out.println("No stock value file");
			valuefile_check = false;
		}
	}
	
	private void RSV()
	{
		for(int i=valueoutput.size()-1; i>=valueoutput.size()-9; i--)
		{
			dateoutputValue.add(Double.parseDouble(valueoutput.get(i).toString()));
		}
		
		// Max
		double maxValue = (double) Collections.max(dateoutputValue);
		double minValue = (double) Collections.min(dateoutputValue);
		
		RSV = (Double.parseDouble(endValue) - minValue) / (maxValue - minValue);
		System.out.println(maxValue+"	"+minValue+"	"+endValue+"	"+RSV);
	}
	
	private void lastRSV()
	{
		for(int i=valueoutput.size()-2; i>=valueoutput.size()-10; i--)
		{
			//System.out.println(i+"	"+valueoutput.get(i));
			lastdateoutputValue.add(Double.parseDouble(valueoutput.get(i).toString()));
		}
		
		// Max
		double maxValue = (double) Collections.max(lastdateoutputValue);
		double minValue = (double) Collections.min(lastdateoutputValue);
		
		lastRSV = (Double.parseDouble(valueoutput.get(valueoutput.size()-2).toString()) - minValue) / (maxValue - minValue);
		System.out.println(maxValue+"	"+minValue+"	"+valueoutput.get(valueoutput.size()-2)+"	"+lastRSV);
	}
	
	public static void main(String args[])
	{
		String startDate = "20191206";
		String stockID = "5880";
		
		try {
			kdvalues kd = new kdvalues(startDate, stockID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
