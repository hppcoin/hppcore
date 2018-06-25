package org.hppcoin.util;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpRetreiver {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public String get(String url) throws Exception {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler = response -> {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			};
			String responseBody = null;
			try {
				responseBody = httpclient.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				LOGGER.severe(e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.severe(e.getMessage());
			}
			return responseBody;
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println(new HttpRetreiver().get("http://denver.hppcoin.org:17777/json/122.118.47.126"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.severe(e.getMessage());
		}
	}
}
