package com.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.AccessProperties;

public class Replace_Item_Cart extends AccessProperties{

	@Test(dependsOnMethods = "com.rest.GetMethods.get_ListOfProducts")
	public void create_new_order() {
		
		Response res = given().
			log().all().
			//header("Authorization", "Bearer "+ prop.getProperty("accessToken")).
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		 	
		when().
		    body("    {\r\n"
		    		+ "        \"productId\": 8739,\r\n"
		    		+ "        \"quantity\": 8\r\n"
		    		+ "    }").
		    contentType(ContentType.JSON).
			put("/carts/"+prop.getProperty("cartId")+"/items/"+149565337).			
		then().
			log().all().
			assertThat().
			statusCode(201).
			body("created", is(true)).
			extract().response();
		
		System.out.println(res.toString());
		writeProperties("OrderID1", (res.path("orderId")).toString());			
	}
	
}
