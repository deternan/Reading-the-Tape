package updated;

/*
 * Parser Stock value by Date
 * version: October 02, 2019 09:30 PM
 * Last revision: November 18, 2019 11:34 PM
 * 
 * Author : Chao-Hsuan Ke
 * E-mail : phelpske.dev at gmail dot com
 * 
 */


public class getStockValue_Main 
{
	String startDate = "20180101";			// start date
	private static int sleepTime = 5200;	// 5.2 secs
	private String outputFolder = "";		// output folder
	private String StockValue = "value.txt";	// output file name
	
	
	public getStockValue_Main() throws Exception
	{
		// Read target list
		
		// check whether exist files
		
		// check exist file date
		
		//
		
		
		GetValueandProcessing_StockValue stockvalue = new GetValueandProcessing_StockValue(startDate, outputFolder, StockValue, sleepTime);
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
