package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class AccessProperties {

	public static Properties prop;
	public static FileInputStream file;
	public static ArrayList<Integer> productIDs;
	public static HashMap<String, String> reg;
	public static final String filepath = "D:\\Eclipse_restassured\\restassured\\src\\test\\resources\\accessTokens.properties";
	
	public static String accessToken;
	public static String itemId;
	public static String productID;
	public static String cartId;
	public static String ReplacedProduct_Stock;
	public static String ReplacedQuanitity;
	public static String OrderID;
	public static String ReplacedproductID;
	public static String clientName;
	public static String clientEmail;

	public static void writeProperties(String key, String value) {
		prop = new Properties();

		try {
			file = new FileInputStream(filepath);
			prop.load(file);
			prop.setProperty(key, value);
			System.out.println("------------------------------------------------------");
			System.out.println("Writing to the properties file:  " + key + "--" + value);
			System.out.println("------------------------------------------------------");
			prop.store(new FileOutputStream(filepath), "accessTokens");
			storePropertiesValues(key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@BeforeSuite(groups = {"ValuesInitialization"})
	public static void loadProperties() {
		prop = new Properties();
		try {
			file = new FileInputStream(filepath);
			prop.load(file);
			System.out.println("Properties file successfully loaded");
			
			init_UserDetails();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void storePropertiesValues(String key) {
		switch (key) {
		case "itemId":
			itemId = prop.getProperty(key);
			System.out.println("Key  --" + key +"--Assigned to variable");
			break;
		
		case "productID":
			productID = prop.getProperty(key);
			System.out.println("Key  --" + key +"--Assigned to variable");
			break;
			
		case "cartId":
			cartId = prop.getProperty(key);
			System.out.println("Key  --" + key +"--Assigned to variable");
			break;
		
		case "ReplacedProduct_Stock":
			ReplacedProduct_Stock = prop.getProperty(key);
			System.out.println("Key  --" + key +"--Assigned to variable");
			break;
			
		case "ReplacedQuanitity":
			ReplacedQuanitity = prop.getProperty(key);
			System.out.println("Key  --" + key +"--Assigned to variable");
			break;
			
		case "accessToken":
			accessToken = prop.getProperty(key);
			System.out.println("Key  --" + key +"--Assigned to variable");
			break;
		
		case "OrderID":
			OrderID = prop.getProperty(key);
			System.out.println("Key  --" + key +"--Assigned to variable");
			break;
		
		case "ReplacedproductID":
			ReplacedproductID = prop.getProperty(key);
			System.out.println("Key  --" + key +"--Assigned to variable");
			break;
			
		default:
			System.out.println("Key does not match");
		}		
	}

	
	public static void init_UserDetails() {
		
		Random ran = new Random();
		int random_num = ran.nextInt(30, 1000);
		
		clientName = "User"+random_num;
		clientEmail = clientName + "@gmail.com";
		
		System.out.println("------------------------------------------------------");
		System.out.println("Name of the client name:    "+ clientName );
		System.out.println("Name of the client email:    "+ clientEmail );
		System.out.println("------------------------------------------------------");
		System.out.println("Successfully Initialize user details for register API");
		System.out.println("------------------------------------------------------");
	}

}
