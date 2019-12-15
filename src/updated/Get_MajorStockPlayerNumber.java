package updated;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Get_MajorStockPlayerNumber 
{
	private String stockId;
	private String majorplayerUrl = "https://www.wantgoo.com/stock/astock/chips?stockno=";
	
	// Get JsonResponse
	private String sourceLine = "";
	
	public Get_MajorStockPlayerNumber(String ID)
	{
		stockId = ID;
		GetValues(ID);
		//if (isJSONValid(sourceLine)) 
		{
			System.out.println(sourceLine);
			//Processing_Data(sourceLine);
		}
	}
	
	private void GetValues(String ID)
	{
		sourceLine = "";
		String URL = "";
		
		// https://goodinfo.tw/StockInfo/EquityDistributionClassHis.asp?STOCK_ID=4943
		// https://www.wantgoo.com/stock/astock/chips?stockno=4943
			URL = majorplayerUrl + ID;
		
		//System.out.println(URL);
		
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
	
	public static void main(String args[])
	{
		String ID = "4943";
		Get_MajorStockPlayerNumber mpl = new Get_MajorStockPlayerNumber(ID);
	}
	
}
