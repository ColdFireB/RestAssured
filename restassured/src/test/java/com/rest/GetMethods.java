package com.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.testng.annotations.BeforeClass;
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
	public void get_status() {
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
		
		System.out.println("Get status of the API:" + status);
	}	
	
	
	@Test(groups = {"Background", "AddCart", "ReplaceItem"}, priority = 2)
	public void get_ListOfProducts() {
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
		writeProperties("productID", productIDs.get(3).toString());
		//System.out.println(res.path("id"));
	}	
	
	@Test(groups = {"Background"}, priority = 3)
	public void get_Product() {
		given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
			get("/products/"+prop.getProperty("productID")).
		then().
		log().all().
		assertThat().
		statusCode(200).
			body("id", is(equalTo(Integer.parseInt(productID))));
	}
	

	@Test(groups = {"ReplaceItem"})
	public void get_Product_Stock() {
		writeProperties("ReplacedproductID", (productIDs.get(4)).toString());
		Response res = given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
			get("/products/"+ ReplacedproductID).
		then().
		log().all().
		assertThat().
		statusCode(200).
			body("id", is(equalTo(Integer.parseInt(ReplacedproductID)))).extract().response();
		
		writeProperties("ReplacedProduct_Stock", res.path("current-stock").toString());
	}
	
	@Test(groups= {"AddCart"}, priority = 3, dependsOnMethods = {"com.rest.PostMethods.Add_ItemTo_cart","com.rest.PostMethods.create_cart"})
	public void get_Cart_items() {
		Response res = given().
		 	baseUri("https://simple-grocery-store-api.glitch.me").
		when().
			get("/carts/"+ cartId +"/items").
		then().
		log().all().
		assertThat().
		statusCode(200).extract().response();
		
		System.out.println(res.path("productId"));
		System.out.println(res.path("id"));
	}
	
	
	@Parameters({"size"})
	@Test(groups= {"AddCart"}, priority = 4, dependsOnMethods = {"com.rest.PostMethods.Add_ItemTo_cart","com.rest.PostMethods.create_cart"})
	public void get_Cart_using_CartID(int size) {
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
	
	@Parameters({"clientname", "replaced_product"})
	@Test(dependsOnMethods = "com.rest.PostMethods.create_new_order", groups = {"CreateOrder"}, priority = 3)
	public void get_Orders_After_ItemAdded(String name, boolean replaced_product) {
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
		body("customerName", hasItem(name),
				"items[0].productId[0]", is(equalTo(id))).extract().response();
		
		System.out.println(res.path("items[0].productId[0]"));
	}
}