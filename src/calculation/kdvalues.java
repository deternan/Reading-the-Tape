package calculation;

/*
 * Parser Stock value by Date
 * version: December 07, 2019 00:01 AM
 * Last revision: December 07, 2019 01:02 PM
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
	ArrayList<Double> valueoutput = new ArrayList<Double>();
	
	String endDate;
	String endValue;
	
	double RSV;
	ArrayList dateoutputValue = new ArrayList();
	
	double lastRSV;
	ArrayList lastdateoutputValue = new ArrayList();
	
	// Fisrt
	double firstRSV;
	double firstK;
	ArrayList<Double> circleKvalue = new ArrayList<Double>();
	
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
			
			circle_RSV_K();
			cirsle_K();
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
				valueoutput.add(Double.parseDouble(tmp[1]));
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
			dateoutputValue.add(valueoutput.get(i));
		}
		
		// Max
		double maxValue = (double) Collections.max(dateoutputValue);
		double minValue = (double) Collections.min(dateoutputValue);
		
		RSV = (Double.parseDouble(endValue) - minValue) / (maxValue - minValue);
		//System.out.println(maxValue+"	"+minValue+"	"+endValue+"	"+RSV);
	}
	
	private void lastRSV()
	{
		for(int i=valueoutput.size()-2; i>=valueoutput.size()-10; i--)
		{
			//System.out.println(i+"	"+valueoutput.get(i));
			lastdateoutputValue.add(valueoutput.get(i));
		}
		
		// Max
		double maxValue = (double) Collections.max(lastdateoutputValue);
		double minValue = (double) Collections.min(lastdateoutputValue);
		
		lastRSV = (Double.parseDouble(valueoutput.get(valueoutput.size()-2).toString()) - minValue) / (maxValue - minValue);
		//System.out.println(maxValue+"	"+minValue+"	"+valueoutput.get(valueoutput.size()-2)+"	"+lastRSV);
	}
	
	// -------------------------------------------------
	
	private void circle_RSV_K()
	{
		ArrayList<Double> firstdateoutputValue = new ArrayList<Double>();
		for(int i=0; i<9; i++)
		{
			firstdateoutputValue.add(valueoutput.get(i));
		}
		double max = Collections.max(firstdateoutputValue);
		double min = Collections.min(firstdateoutputValue);
		
		firstRSV = (valueoutput.get(9) - min) / (max - min);
		//System.out.println(max+"	"+min+"	"+valueoutput.get(9)+"	"+firstRSV);
		firstK = firstRSV;
		// start K value (RSV)
		circleKvalue.add(firstK);
		//System.out.println(firstK);
	}
	
	private void cirsle_K()
	{
		ArrayList<Double> circledateoutputValue = new ArrayList<Double>();
		double circleTodayValue;
		double todayRSV;
		double maxValue;
		double minValue;
		double lastKvalue;
		double todayKvalue;
		double lastcircleRSV = 0;
		
		String type;
		if(dateoutput.size() > 10) {
			
			for(int i=1; i<dateoutput.size()-9; i++)
			{
				if(i == 1) {
					//lastKvalue = firstK;
					lastKvalue = circleKvalue.get(0);
				}else {
					lastKvalue = circleKvalue.get(i-1);
				}
				circledateoutputValue.clear();
				for(int j=0; j<9; j++)
				{
					circledateoutputValue.add(valueoutput.get(i+j));
					//System.out.println(valueoutput.get(i+j));
				}
				circleTodayValue = valueoutput.get(i+9);
				// RSV
				maxValue = Collections.max(circledateoutputValue);
				minValue = Collections.min(circledateoutputValue);
				
				if((circleTodayValue - minValue) == 0) {
					//todayRSV = lastKvalue;
					todayRSV = lastRSV;
					type = "type 1";
				}else if((maxValue - minValue) == 0) {
					todayRSV = lastKvalue;
					type = "type 2";
				}else {
					todayRSV = (circleTodayValue - minValue) / (maxValue - minValue);
					if(todayRSV < 0) {
						//todayRSV = 0;
						todayRSV = lastcircleRSV;
					}
					type = "type 3";
				}
				lastcircleRSV = todayRSV;
						
				todayKvalue = ((lastKvalue*2)/3) + (todayRSV/3);
				circleKvalue.add(todayKvalue);
				
				//System.out.println("-----------------------	"+circleTodayValue+"	"+lastKvalue+"	"+todayRSV+"	"+todayKvalue+"	"+type);
				System.out.println("-----------------------	"+dateoutput.get(i+9)+"	"+circleTodayValue+"	"+maxValue+"	"+minValue+"	"+lastKvalue+"	"+todayRSV+"	"+todayKvalue+"	"+type);
			}
			
		}else {
			System.out.println("Data is not enough");
		}
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
