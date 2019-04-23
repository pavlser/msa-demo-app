package com.homework.testing;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.common.rest.AccountInfo;
import com.homework.testing.rest.BankAccountInfo;

public class BankIntegrationTest {

	String gateUtl = "http://localhost:9002/api";

	@Test
	public void runAll() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		HttpUriRequest request;
		HttpResponse response;
		String jwtToken = null;
		String userName = "user";
		String password = "user";
		BankAccountInfo dto = null;
		
		System.out.println("Start tests...");
		
		System.out.println("\nPerform login with basic auth");
		
		// Basic login credentials
		String auth = userName + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		
		// Request JWT token
		request = new HttpGet(gateUtl + "/login");
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		response = HttpClientBuilder.create().build().execute(request);
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			if (header.getName().equals("Authorization")) {
				jwtToken = header.getValue().replace("Bearer ", "");
				break;
			}
		}
		System.out.println("> jwtToken: " + jwtToken);
		
		// Try get without auth
		System.out.println("\n>>> Try request without jwt auth '/bank/1' ----------------------------------------------");
		request = new HttpGet(gateUtl + "/bank/1");
		response = HttpClientBuilder.create().build().execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("> statusCode should be 401 (UNAUTHORIZED): " + statusCode);
		assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		
		// Get Bank info for Account 1
		System.out.println("\n>>> Read account 1 ----------------------------------------------");
		request = new HttpGet(gateUtl + "/bank/1");
		request.setHeader(HttpHeaders.AUTHORIZATION, jwtToken);
		response = HttpClientBuilder.create().build().execute(request);
		dto = mapper.readValue(EntityUtils.toString(response.getEntity()), BankAccountInfo.class);
		System.out.println("Bank Data Info 1: " + dto);
		assertThat(1L, Matchers.is(dto.id));
		assertThat("Max", Matchers.is(dto.firstName));
		assertThat("Mustermann", Matchers.is(dto.lastName));

		// Get Bank info for Account 2
		System.out.println("\n>>> Read account 2 ----------------------------------------------");
		request = new HttpGet(gateUtl + "/bank/2");
		request.setHeader(HttpHeaders.AUTHORIZATION, jwtToken);
		response = HttpClientBuilder.create().build().execute(request);
		dto = mapper.readValue(EntityUtils.toString(response.getEntity()), BankAccountInfo.class);
		System.out.println("Bank Data Info 2: " + dto);
		assertThat(2L, Matchers.is(dto.id));
		assertThat("Darth", Matchers.is(dto.firstName));
		assertThat("Vader", Matchers.is(dto.lastName));
		
		// Post a new Account
		System.out.println("\n>>> Create new account ----------------------------------------------");
		String json = "{ \"id\":3, \"firstName\":\"Luke\", \"lastName\":\"Skywalker\" }";
		StringEntity entity = new StringEntity(json);
		CloseableHttpClient client = HttpClients.createDefault();
	    HttpPost httpPost = new HttpPost(gateUtl + "/account/3");
	    httpPost.setHeader(HttpHeaders.AUTHORIZATION, jwtToken);
	    httpPost.setEntity(entity);
	    httpPost.setHeader("Accept", "application/json");
	    httpPost.setHeader("Content-type", "application/json");
	    CloseableHttpResponse resp = client.execute(httpPost);
	    HttpEntity respEnt = resp.getEntity();
	    System.out.println(EntityUtils.toString(respEnt));
	    assertThat(resp.getStatusLine().getStatusCode(), equalTo(201));
	    client.close();
	    
	    // Read back created entity
	    System.out.println("\n>>> Read account 3 ----------------------------------------------");
 		request = new HttpGet(gateUtl + "/bank/3");
 		request.setHeader(HttpHeaders.AUTHORIZATION, jwtToken);
 		response = HttpClientBuilder.create().build().execute(request);
 		dto = mapper.readValue(EntityUtils.toString(response.getEntity()), BankAccountInfo.class);
 		System.out.println("Bank Data Info 3: " + dto);
 		assertThat(3L, Matchers.is(dto.id));
 		assertThat("Luke", Matchers.is(dto.firstName));
 		assertThat("Skywalker", Matchers.is(dto.lastName));
 		
 		// Read all accounts
	    System.out.println("\n>>> Read all accounts ----------------------------------------------");
 		request = new HttpGet(gateUtl + "/accounts"); // 'accounts' uri not protected by auth
 		response = HttpClientBuilder.create().build().execute(request);
 		List<AccountInfo> accounts = mapper.readValue(
 				EntityUtils.toString(response.getEntity()),
 				new TypeReference<List<AccountInfo>>(){} );
 		System.out.println("Accounts: " + accounts);
 		assertThat(3, Matchers.is(accounts.size()));

		System.out.println("\nTests are OK");
	}

}
