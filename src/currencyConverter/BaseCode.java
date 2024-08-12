package currencyConverter;

import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class BaseCode {	

	public static void main(String[] args) throws Exception {
		
		CurrencyScraping.main(args);

		try {
			File file = new File("C:\\Users\\Vilius\\eclipse-workspace\\currencyConverter\\currencyData");
			
	        ObjectMapper objectMapper = new ObjectMapper();

	        // Read JSON file and parse it into a Map
	        Map<String, Object> mapFromJson = objectMapper.readValue(file, Map. class);

	        // Convert the Map to a HashMap
	        HashMap<String, Object> usdRates = new HashMap<>(mapFromJson);
	        
	        
			Scanner in = new Scanner(System.in);
			System.out.println("type in the amount you want converted: ");
			BigDecimal amount = in.nextBigDecimal();
			in.nextLine();
			System.out.println("type in your currency: ");
			String givenType = in.nextLine();
			System.out.println("finally, type in your wanted currency: ");
			String toConvertTo = in.nextLine();
			

			BigDecimal givenTypeRate = new BigDecimal((String) usdRates.get(givenType));
			BigDecimal toConvertToRate = new BigDecimal((String) usdRates.get(toConvertTo));

			BigDecimal amountInUsd = amount.divide(givenTypeRate, 6, RoundingMode.HALF_UP); // not great to be rounding up here
			BigDecimal finalAmount = amountInUsd.multiply(toConvertToRate);

			finalAmount = finalAmount.setScale(2, RoundingMode.HALF_UP);
			System.out.println("Your " + amount + " " + givenType + " is " + finalAmount + " " + toConvertTo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


// database / interface / change website scraping to using an API