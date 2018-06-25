/*
 * HPPCOIN License
 * 
 * Copyright (c) 2017-2018, HPPCOIN Developers.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.hppcoin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class RestClient {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static String getJSON(String urlStr) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			StringBuffer json = new StringBuffer(1024);
			String tmp = "";
			while ((tmp = reader.readLine()) != null) {
				json.append(tmp).append("\n");

			}

			return json.toString();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			return null;
		}
	}

	public static String postData(String url, Map<String, String> data) {
		int size = 0;
		String strResult = null;
		String rpcuser = "";
		String rpcpassword = "";
		if (data != null)
			size = data.size();
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(rpcuser, rpcpassword);
		AuthScope authscp = new AuthScope("127.0.0.1", 7777);
		provider.setCredentials(authscp, credentials);
		HttpClient httpclient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
		HttpPost httppost = new HttpPost(url);

		try {

			String json = new GsonBuilder().create().toJson(data, Map.class);
			HttpResponse response = makeRequest(url, json);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				strResult = EntityUtils.toString(responseEntity);
			}

		} catch (Exception e) {
			LOGGER.severe(e.getMessage());

			httppost.abort();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return strResult;
	}

	public static HttpResponse makeRequest(String uri, String json) {
		try {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new StringEntity(json));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "text/plain");
			return new DefaultHttpClient().execute(httpPost);
		} catch (UnsupportedEncodingException e) {
			LOGGER.severe(e.getMessage());
		} catch (ClientProtocolException e) {
			LOGGER.severe(e.getMessage());
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
		return null;
	}

	public static String postDataSimple(String url, Map<String, String> data) {
		int size = 0;
		JsonObject respJson = null;
		String result = null;
		if (data != null)
			size = data.size();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {
			if (size > 0) {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(size);
				for (String key : data.keySet()) {
					nameValuePairs.add(new BasicNameValuePair(key, data.get(key)));
				}
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				result = EntityUtils.toString(responseEntity);
			}

		} catch (Exception e) {
			httppost.abort();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return result;
	}
}