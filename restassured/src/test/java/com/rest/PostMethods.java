package com.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.AccessProperties;

public class PostMethods extends AccessProperties{
	
	@BeforeClass
	public void beforeClass() {
		System.out.println("------------------------------------------------------");
		System.out.println("Executing beforeclass method");
		System.out.println("------------------------------------------------------");
	}
	
	@Test(groups = {"NegativieScenario"})
	public void register_ExistingAPI_Client(@Optional Method method) {
		System.out.println("------------------------------------------------------");
		System.out.println("--------Register Existing API-----------<<" + method.getName()  + ">>");
		System.out.println("------------------------------------------------------");
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
	

	@Test(groups = {"Background"}, priority = 4)
	public void register_newAPI_Client(@Optional Method method) {
		System.out.println("------------------------------------------------------");
		System.out.println("Register NEW API Client--Invoking Method---<<"  + method.getName()+">>" );
		System.out.println("------------------------------------------------------");
		
		Response res = given().
			log().all().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		 	contentType(ContentType.JSON).
		 	body("{\r\n"
		 			+ "   \"clientName\": \""+clientName +"\",\r\n"
		 			+ "   \"clientEmail\":\""+ clientEmail+"\"\r\n"
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
	public void create_cart(@Optional Method method) {
		
		System.out.println("------------------------------------------------------");
		System.out.println("Create Cart---<<"  + method.getName()+">>" );
		System.out.println("------------------------------------------------------");
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
	public void Add_ItemTo_cart(@Optional Method method) {
		
		System.out.println("------------------------------------------------------");
		System.out.println("Add new Item to the cart---<<"  + method.getName()+">>" );
		System.out.println("------------------------------------------------------");
		
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
	
	
	@Test(groups = {"CreateOrder"}, priority = 2)
	public void create_new_order(@Optional Method method) {
		
		System.out.println("------------------------------------------------------");
		System.out.println("Create New Order---<<"  + method.getName()+">>" );
		System.out.println("------------------------------------------------------");
		
		Response res = given().
			log().all().
			header("Authorization", "Bearer "+ accessToken).
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		 	
		when().
		    body("{\r\n"
		    		+ "    \"cartId\":"+ "\"" + cartId +"\""+",\r\n"
		    		+ "    \"customerName\":" +"\"" +clientName+ "\"" + "\r\n"
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
	public void replace_item_inCart(int quantity, @Optional Method method) {
		
		System.out.println("------------------------------------------------------");
		System.out.println("Replace item in cart---<<"  + method.getName()+">>" );
		System.out.println("------------------------------------------------------");
		
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