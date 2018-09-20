package com.example.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	RestClient restClient = RestClient.builder(
		       new HttpHost("localhost", 9200, "http"),
		       new HttpHost("localhost", 9205, "http")).build();
    
    @RequestMapping("/elasticsearch")
    public String retrive() throws ClientProtocolException, IOException {
    	 HttpClient httpClient = HttpClientBuilder.create().build();         
    	    String url = "http://localhost:9200/company/employee/_search";       
    	     HttpGet getRequest = new HttpGet(url);         
    	     HttpResponse response = httpClient.execute(getRequest);
    	  //if(response.getStatusLine().getStatusCode() == HttpStatus.OK) {
    	  return "success";	  
    	  //} else {
    		//  return "failure";
    	  //}
        
    }
    
    @RequestMapping("/employee")
    public String getEmployee() throws JsonGenerationException, JsonMappingException, IOException {
    	Person person = new Person();
    	person.setFirstName("harish");
    	person.setLastName("challa");
    	person.setDob("challa");
    	person.setCountry("India");
        return OBJECT_MAPPER.writeValueAsString(person);
    }
    
    @RequestMapping("/elk")
    public String getIndex() throws IOException {
    	
    	Map<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("q", "last_name:steve");
    	paramMap.put("pretty", "true");
    	Response response = restClient.performRequest("GET", "cs_person/_search", paramMap);
    	String s = EntityUtils.toString(response.getEntity());
    	System.out.println(s);
    	System.out.println("Host -" + response.getHost() );
    	System.out.println("RequestLine -"+ response.getRequestLine() );
        return s;
    }
    
	@RequestMapping(value = "/ProductId", method = RequestMethod.GET)
	public ResponseEntity<String> getProductId(@RequestParam("search") String id) {
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
	    	paramMap.put("q", "last_name:" + id);
	    	paramMap.put("pretty", "true");
			Response response = restClient.performRequest("GET", "cs_person/_search", paramMap);
	    	String s = EntityUtils.toString(response.getEntity());
	    	System.out.println(s);
				System.out.println("Id from angular" + id);

	//		checkout.setPurchaseId(purchaseId);
	//		checkout = shoppingService.saveShoppingCart(checkout);

			return new ResponseEntity<>( s , HttpStatus.CREATED);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getLocalizedMessage());
		}

	}
	
	@RequestMapping(value = "/VehicleId", method = RequestMethod.GET)
	public ResponseEntity<String> getVehicleId(@RequestParam("search") String id) {
		try {
			
			Map<String, String> paramMap = new HashMap<String, String>();
	    	paramMap.put("q", "_vehicle_id_:" + id);
	    	paramMap.put("pretty", "true");
	    	long milliStart = System.currentTimeMillis();
	    	Response response = restClient.performRequest("GET", "cs_vehicle/_search", paramMap);
	    	String s = EntityUtils.toString(response.getEntity());
	    	System.out.println(s);
				System.out.println("Id from angular" + id);
				long milliEnd = System.currentTimeMillis();
				System.out.println("Response Time in milli seconds" + ( milliEnd - milliStart));

	//		checkout.setPurchaseId(purchaseId);
	//		checkout = shoppingService.saveShoppingCart(checkout);

			return new ResponseEntity<>( s , HttpStatus.CREATED);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getLocalizedMessage());
		}

	}
	
	@RequestMapping(value = "/VehicleIdSQL", method = RequestMethod.GET)
	public ResponseEntity<String> getVehicleIdbySQL(@RequestParam("search") String id) {
		try {
		//	Map<String, String> paramMap = new HashMap<String, String>();
	    //	paramMap.put("q", "_vehicle_id_:" + id);
	    //	paramMap.put("pretty", "true");
		//	Response response = restClient.performRequest("GET", "cs_vehicle/_search", paramMap);
			long milliStart = System.currentTimeMillis();
			SqlJdbcConnection SQLcon = new SqlJdbcConnection();
	    	String s = SQLcon.getVehicleDetails(id);
	    	System.out.println(s);
	    	long milliEnd = System.currentTimeMillis();
			System.out.println("Response Time in milli seconds" + ( milliEnd - milliStart));

	//		checkout.setPurchaseId(purchaseId);
	//		checkout = shoppingService.saveShoppingCart(checkout);

			return new ResponseEntity<>( "{ \"PurchaseId\" : \"" + s + "\"}" , HttpStatus.CREATED);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getLocalizedMessage());
		}

	}
}