package com.trade.coin.model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuyCoin {
	public void buy() throws NoSuchAlgorithmException, UnsupportedEncodingException, ParseException, org.json.simple.parser.ParseException {
	
		String accessKey = "ACCESS KEY";
		String secretKey = "SECRET KEY";
		String serverUrl = "https://api.upbit.com";

		HashMap<String, String> params = new HashMap<>();
		params.put("market", "KRW-BTC");
		params.put("side", "bid");
		params.put("price", "5000");
		params.put("ord_type", "price");

		ArrayList<String> queryElements = new ArrayList<>();
		for(Map.Entry<String, String> entity : params.entrySet()) {
		    queryElements.add(entity.getKey() + "=" + entity.getValue());
		}

		String queryString = String.join("&", queryElements.toArray(new String[0]));

		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(queryString.getBytes("UTF-8"));

		String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		String jwtToken = JWT.create()
			.withClaim("access_key", accessKey)
			.withClaim("nonce", UUID.randomUUID().toString())
			.withClaim("query_hash", queryHash)
			.withClaim("query_hash_alg", "SHA512")
			.sign(algorithm);

		String authenticationToken = "Bearer " + jwtToken;

		try {
		    HttpClient client = HttpClientBuilder.create().build();
		    HttpPost request = new HttpPost(serverUrl + "/v1/orders");
		    request.setHeader("Content-Type", "application/json");
		    request.addHeader("Authorization", authenticationToken);
		    request.setEntity(new StringEntity(new Gson().toJson(params)));

		    HttpResponse response = client.execute(request);
		    HttpEntity entity = response.getEntity();

		    System.out.println(EntityUtils.toString(entity, "UTF-8"));
		    System.out.println("매수 완료!");
			
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
