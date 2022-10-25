package com.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.AccessProperties;

public class PostMethods extends AccessProperties{
	
	@BeforeClass
	public void beforeClass() {
		System.out.println("Executing before class method" + getClass());
	}
	
	@Test(groups = {"NegativieScenario"})
	public void register_ExistingAPI_Client() {
		given().
			log().all().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		 	contentType(ContentType.JSON).
		 	body("{\r\n"
		 			+ "   \"clientName\": \"Bala\",\r\n"
		 			+ "   \"clientEmail\": \"balaece1991@gmail.com\"\r\n"
		 			+ "}").
		when().
			post("/api-clients").
		then().
		log().all().
		assertThat().
		statusCode(409).
		body("error", is(equalTo("API client already registered. Try a different email.")));
	}
	
	@Parameters({"clientname", "clientmail"})
	@Test(groups = {"Background"}, priority = 4)
	public void register_newAPI_Client(String name, String mailid) {
		Response res = given().
			log().all().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		 	contentType(ContentType.JSON).
		 	body("{\r\n"
		 			+ "   \"clientName\": \""+name +"\",\r\n"
		 			+ "   \"clientEmail\":\""+ mailid+"\"\r\n"
		 			+ "}").
		when().
			post("/api-clients").
		then().
		log().all().
		assertThat().
		statusCode(201).extract().response();
		
		accessToken = res.path("accessToken");
		writeProperties("accessToken",accessToken);
	}
	
	@Test(groups= {"AddCart"}, priority = 1)
	public void create_cart() {
		Response res = given().
			log().all().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
			post("/carts").
		then().
			log().all().
			assertThat().
			statusCode(201).
			body("created", equalTo(true)). 
			extract().response();
		
		writeProperties("cartId", (res.path("cartId")).toString());	
	}
	
	@Test(dependsOnMethods = "com.rest.GetMethods.get_ListOfProducts",groups= {"AddCart"}, priority = 2)
	public void Add_ItemTo_cart() {
		writeProperties("productID", (productIDs.get(1)).toString());
		
		Response res = given().
			log().all().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
		    body("{\r\n"
				+ "   \"productId\":" +productIDs.get(1)+"\r\n"
				+ "}").
		    contentType(ContentType.JSON).
			post("/carts/"+ cartId + "/items").
			
		then().
			log().all().
			assertThat().
			statusCode(201).
			body("created", is(true)).
			extract().response();
		
		System.out.println(res.toString());
		writeProperties("itemId", (res.path("itemId")).toString());			
	}
	
	
	@Parameters({"clientname"})
	@Test(groups = {"CreateOrder"}, priority = 2)
	public void create_new_order(String name) {
		Response res = given().
			log().all().
			header("Authorization", "Bearer "+ accessToken).
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		 	
		when().
		    body("{\r\n"
		    		+ "    \"cartId\":"+ "\"" + cartId +"\""+",\r\n"
		    		+ "    \"customerName\":" +"\"" +name+ "\"" + "\r\n"
		    		+ "}").
		    contentType(ContentType.JSON).
			post("/Orders").			
		then().
			log().all().
			assertThat().
			statusCode(201).
			body("created", is(true)).
			extract().response();
		
		System.out.println(res.toString());
		writeProperties("OrderID", (res.path("orderId")).toString());			
	}
	
	@Parameters({"quantity"})
	@Test(groups = {"ReplaceItem"}, dependsOnMethods = {"com.rest.GetMethods.get_ListOfProducts", "com.rest.GetMethods.get_Product_Stock"})
	public void replace_item_inCart(int quantity) {
		
		int quantity_prop = Integer.parseInt(ReplacedProduct_Stock);
		if(quantity<=quantity_prop) {
			writeProperties("ReplacedQuanitity", Integer.toString(quantity));
			
			given().
				log().all().
				header("Authorization", "Bearer "+ accessToken).
			 	baseUri("https://simple-grocery-store-api.glitch.me").
			 	
			when().
			    body("    {\r\n"
			    		+ "        \"productId\":"+ ReplacedproductID +",\r\n"
			    		+ "        \"quantity\":"+ quantity + "\r\n"
			    		+ "    }").
			    contentType(ContentType.JSON).
				put("/carts/"+ cartId +"/items/"+ itemId).			
			then().
				log().all().
				assertThat().
				statusCode(204);	
		}
		else {
			System.out.println("PRODUCT IS OUT OF STOCK, HENCE NOT REPLACED");
		}
	}
}