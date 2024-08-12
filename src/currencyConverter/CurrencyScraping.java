package currencyConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CurrencyScraping {
	public static void main(String[] args) throws Exception {
		
		final String url = "https://www.xe.com/currencytables/?from=USD";
		String fileLocation = "C:\\Users\\Vilius\\eclipse-workspace\\currencyConverter\\currencyData";
		
        if(wasNotEditedWithinLast24Hours(fileLocation)) {
        
        	try {
        		JSONObject currencyObj = new JSONObject();
        		
        		Document document = Jsoup.connect(url).get();
        		Elements table = document.select(".foLGOz");
        		Elements tbody = table.select("tbody"); 
        		String extractedTextName = "";
        		String extractedTextValue = "";
        		
        		int i = 1;
        		for(Element tr : tbody) {
        			
        			for(Element td : tr.select("td")) {	
        				if(i % 3 == 1){
        					extractedTextName = td.text().trim();
        					//System.out.println("name in if " + extractedTextName);
        				}
        				if(i % 3 == 2) {
        					extractedTextValue = td.text().trim();
        					//System.out.println("value in if " + extractedTextValue);
        					currencyObj.put(extractedTextName, extractedTextValue);
        				}
        				i = i + 1;
        			}
        			//System.out.println("outside loop " + extractedTextName + " " + extractedTextValue);
				
        		}
        		try (FileWriter fw = new FileWriter(new File(fileLocation), StandardCharsets.UTF_8);
        	             BufferedWriter writer = new BufferedWriter(fw)) {

        	            writer.write(currencyObj.toJSONString());
        	            writer.flush();
        	            writer.close();
        	            System.out.println("file has been updated");
        	        }
        	} catch(IOException e) {
        		e.printStackTrace();
        	}
        } else {
        	System.out.println("24 hours have not passed since last update");
        }
	}
	
	 public static boolean wasNotEditedWithinLast24Hours(String filePath) throws Exception {
		 	File file = new File(filePath);
	        long lastModifiedTime = file.lastModified();
	        LocalDateTime currentDate = LocalDateTime.now();
	        ZonedDateTime zonedCurrentDate = ZonedDateTime.of(currentDate, ZoneId.systemDefault());
	        long currentDateInMillis = zonedCurrentDate.toInstant().toEpochMilli();
	        
	        if(currentDateInMillis - lastModifiedTime > 86400000) {
	        	return true;
	        }
	        return false;
	    }
}