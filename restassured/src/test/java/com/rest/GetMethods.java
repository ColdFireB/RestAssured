package com.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import java.lang.reflect.Method;
import java.util.Random;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import utils.AccessProperties;

public class GetMethods extends AccessProperties{

	@BeforeClass
	public void Beforeclass(){
		System.out.println("Executing before class method" + getClass());
	}
	
	@Test(groups = {"Background"},priority = 1)
	public void get_status(@Optional Method method) {
		System.out.println("------------------------------------------------------");
		System.out.println("-----Get status of the application-------<<" + method.getName()  + ">>");
		System.out.println("------------------------------------------------------");
		
		String status = given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
			get("/status").
		then().
		log().all().
		assertThat().
		statusCode(200).
		extract().response().path("status");
			//body("status", is(equalTo("UP")));
		//System.out.println("Get status of the API:" res.path("status"));
		
		assertEquals(status, "UP");
		
		System.out.println("Get status of the API:" + status);
	}	
	
	
	@Test(groups = {"Background", "AddCart", "ReplaceItem"}, priority = 2)
	public void get_ListOfProducts(@Optional Method method) {
		
		System.out.println("------------------------------------------------------");
		System.out.println("------Get list of products---------<<" + method.getName()  + ">>");
		System.out.println("------------------------------------------------------");
		
		productIDs = given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
			get("/products").
		then().
		log().all().
		assertThat().
		statusCode(200).
		body("category", hasItem("meat-seafood"),
				"name", hasItem("Fresh Spinach Organic"),
				"id", hasItems(4643, 4646, 4641, 1225, 3674, 2585, 5851, 8739, 2177, 1709, 1709, 7395, 8554, 6483, 5774, 8753, 9482, 5477, 5478, 4875)).
		extract().response().path("id");
		
		System.out.println(productIDs);
		
		//set any one productID
		writeProperties("productID", productIDs.get(1).toString());
	}	
	
	@Test(groups = {"Background"}, priority = 3)
	public void get_Product(@Optional Method method) {
		System.out.println("------------------------------------------------------");
		System.out.println("--------Get Product details-----------<<" + method.getName()  + ">>");
		System.out.println("------------------------------------------------------");
		given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
			get("/products/"+productID).
		then().
		log().all().
		assertThat().
		statusCode(200).
			body("id", is(equalTo(Integer.parseInt(productID))));
	}
	
	@Test(groups = {"Background"}, priority = 4 )
	public void store_ProductID_StockValue(@Optional Method method) {
		System.out.println("------------------------------------------------------");
		System.out.println("--------Stroring Product ID and Stock in HashMap---------<<" + method.getName()  + ">>");
		System.out.println("------------------------------------------------------");
		
		for(int id: productIDs) {
			Response res = given().
				 	baseUri("https://simple-grocery-store-api.glitch.me").
				when().
					get("/products/"+ id ).
				then().
				log().all().
				assertThat().
				statusCode(200).
					body("id", is(id)).extract().response();
			
			products.put(id, (Integer) res.path("current-stock"));
		}		
	}

	public String getProductStock(int index) {
		return products.get(productIDs.get(index)).toString();
	}
	
	public int randomproductIndex() {
		Random rand = new Random();
		return rand.nextInt(productIDs.size());		
	}

	@Test(groups = {"ReplaceItem"})
	public void get_Product_Stock(@Optional Method method) {
		System.out.println("------------------------------------------------------");
		System.out.println("--------Get Product's current stock---------<<" + method.getName()  + ">>");
		System.out.println("------------------------------------------------------");
		writeProperties("ReplacedproductID",  productIDs.get(randomproductIndex()).toString());
		given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
			get("/products/"+ ReplacedproductID).
		then().
			log().all().
			assertThat().
			statusCode(200).
			body("id", is(equalTo(Integer.parseInt(ReplacedproductID))));
		
		writeProperties("ReplacedProduct_Stock", getProductStock(randomproductIndex()).toString());
	}
	
	@Test(groups= {"AddCart"}, priority = 3, dependsOnMethods = {"com.rest.PostMethods.Add_ItemTo_cart","com.rest.PostMethods.create_cart"})
	public void get_Cart_items(@Optional Method method) {
		System.out.println("------------------------------------------------------");
		System.out.println("--------get cart items-----------<<" + method.getName()  + ">>");
		System.out.println("------------------------------------------------------");
		
		Response res = given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
			get("/carts/"+ cartId +"/items").
		then().
		log().all().
		assertThat().
		statusCode(200).extract().response();
		
		System.out.print(res.path("productId"));
		System.out.println(res.path("id"));
	}
	
	
	@Parameters({"size"})
	@Test(groups= {"AddCart"}, priority = 4, dependsOnMethods = {"com.rest.PostMethods.Add_ItemTo_cart","com.rest.PostMethods.create_cart"})
	public void get_Cart_using_CartID(int size, @Optional Method method) {
		System.out.println("------------------------------------------------------");
		System.out.println("--------Get cart using CartID-----------<<" + method.getName()  + ">>");
		System.out.println("------------------------------------------------------");
		
		Response res = given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
			get("/carts/"+ cartId).
		then().
		log().all().
		assertThat().
		statusCode(200).
		body("items", hasSize(size)). 
		extract().response();
		
		System.out.println(res.path("items.size()"));
	}
	
	@Test(groups = {"CreateOrder"}, priority = 1)
	public void get_Orders_First() {
		given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		 	headers("Authorization", "Bearer " + accessToken).
		when().
			get("/Orders").
		then().
		log().all().
		assertThat().
		statusCode(200);
	}
	
	@Parameters({"replaced_product"})
	@Test(dependsOnMethods = "com.rest.PostMethods.create_new_order", groups = {"CreateOrder"}, priority = 3)
	public void get_Orders_After_ItemAdded(boolean replaced_product) {
		int id;
		if(replaced_product) {
			System.out.println("product is replaced");
			id = Integer.parseInt(ReplacedproductID);
		}
		else {
			System.out.println("product is not replaced yet");
			id = Integer.parseInt(productID);	
		}
		
		Response res = given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		 	headers("Authorization", "Bearer " + accessToken).
		when().
			get("/Orders").
		then().
		log().all().
		assertThat().
		statusCode(200).
		body("customerName", hasItem(clientName),
				"items[0].productId[0]", is(equalTo(id))).extract().response();
		
		System.out.println(res.path("items[0].productId[0]"));
	}
}