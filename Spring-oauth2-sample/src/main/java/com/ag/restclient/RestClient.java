package com.ag.restclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * The Class RestClient.
 */
public class RestClient {

	/** The Constant OAUTH2_URL. */
	public static final String OAUTH2_URL = "http://localhost:8080/Spring-oauth2-sample/oauth/token";

	/**
	 * Gets token.
	 *
	 * @param map map
	 * @return token
	 * @throws Exception  exception
	 */
	public String getToken(final Map<String, String> map) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpost = new HttpPost(OAUTH2_URL);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("grant_type", map.get("grant_type")));
			nvps.add(new BasicNameValuePair("client_id", map.get("client_id")));
			nvps.add(new BasicNameValuePair("client_secret", map.get("client_secret")));
			httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			CloseableHttpResponse response = httpclient.execute(httpost);
			try {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}

}
