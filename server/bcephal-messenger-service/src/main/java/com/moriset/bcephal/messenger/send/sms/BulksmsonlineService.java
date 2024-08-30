package com.moriset.bcephal.messenger.send.sms;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

@Component
public class BulksmsonlineService implements ConfigClient {

	private Logger logger = LoggerFactory.getLogger(BulksmsonlineService.class);

	public void sendSms(Sms sms) throws Exception {
		logger.info("Try to send sms to {}", sms.getTo());
		Object token = getToken(sms.getLienToken(), sms.getUserSms().getName(), sms.getUserSms().getPassword());
		if (token instanceof String) {
			apiSms(sms, (String) token);
		}
	}

	public Object getToken(String lienToken, String user, String password) throws Exception {

		String res = "";
		OkHttpClient client = getHttpClient();

		Request request = new Request.Builder().url(lienToken + "/username/" + user + "/password/" + password).get()
				.build();
		Response response = client.newCall(request).execute();
		if (response.code() == 200) {
			res = response.body().string();
			String resp = res.substring(1, res.length() - 1);
			JSONObject jsonObject = new JSONObject("{" + resp + "}");
			logger.debug("Token successfully obtained: {} ", jsonObject);
			return jsonObject.getString("token");
		}

		if (response.code() == 400) {
			logger.error("SMS sending error: Invalid Parameters");
		} else if (response.code() == 401) {
			logger.error("SMS sending error: Invalid Token");
		} else if (response.code() == 402) {
			logger.error("SMS sending error: {}", response.body());
		} else if (response.code() == 404) {
			logger.error("SMS sending error: invalid path");
		} else if (response.code() == 405) {
			logger.error("SMS sending error: Method Not Allowed");
		} else if (response.code() == 500) {
			logger.error("SMS sending error: Internal Server Error");
		} else {
			logger.error("SMS sending error: Service Unavailable");
		}
		throw new Exception("Error to get token");
	}

	public void apiSms(Sms sms, String token) throws Exception {

		String res = "";
		OkHttpClient client = getHttpClient();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, sms.getContentSmsForBulksmsonline());
		Request request = new Request.Builder().url(sms.getLienSms()).post(body).addHeader("token", token)
				.addHeader("content-type", "application/json").build();
		Response response = client.newCall(request).execute();
		res = response.body().string();
		JSONObject jsonObject = null;
		if (res != null && !res.isEmpty()) {
			String resp = res.substring(1, res.length() - 1);
			jsonObject = new JSONObject("{" + resp + "}");
		}
		String messgae = "message";
		if (response.code() == 200 && jsonObject != null) {
			logger.debug("message successfully sended: {} ", jsonObject);
		}
		String Responsebody = "";
		if (jsonObject != null && !jsonObject.isNull(messgae)) {
			Responsebody = jsonObject.getString(messgae);
		}
		logger.info("Response : {}", Responsebody);
		if (response.code() == 400) {
			if (!Responsebody.isEmpty()) {
				logger.error(ResponseAnalyse(Responsebody));
			} else {
				logger.error("Invalid Parameters");
			}
		} else if (response.code() == 401) {
			if (!Responsebody.isEmpty()) {
				logger.error(ResponseAnalyse(Responsebody));
			} else {
				logger.error("Invalid Token");
			}
		} else if (response.code() == 404) {
			if (!Responsebody.isEmpty()) {
				logger.error(ResponseAnalyse(Responsebody));
			} else {
				logger.error("invalid path");
			}
		} else if (response.code() == 405) {
			if (!Responsebody.isEmpty()) {
				logger.error(ResponseAnalyse(Responsebody));
			} else {
				logger.error("Method Not Allowed");
			}
		} else if (response.code() == 500) {
			if (!Responsebody.isEmpty()) {
				logger.error(ResponseAnalyse(Responsebody));
			} else {
				logger.error("Internal Server Error");
			}
		} else {
			if (!Responsebody.isEmpty()) {
				logger.error(ResponseAnalyse(Responsebody));
			} else {
				logger.error("Service Unavailable");
			}
		}
		throw new Exception(Responsebody);
	}

	public String ResponseAnalyse(String result) {
		result = result.trim().toUpperCase();
		if (result.contains("E0002")) {
			return "Invalid URL. This means that one of the parameters was not provided or left blank.";
		} else if (result.contains("E0003")) {
			return "Invalid username or password parameter.";
		} else if (result.contains("E0004")) {
			return "Invalid type parameter.";
		} else if (result.contains("E0005")) {
			return "Invalid message.";
		} else if (result.contains("E0006")) {
			return "Invalid TO number.";
		} else if (result.contains("E0007")) {
			return "Invalid source (Sender name).";
		} else if (result.contains("E0008")) {
			return "Authentication failed.";
		} else if (result.contains("E0010")) {
			return "Internal server error.";
		} else if (result.contains("E0022")) {
			return "Insufficient credit.";
		} else if (result.contains("E0033")) {
			return "If more than 30 API request per second throughput restriction by default";
		} else if (result.contains("E0044")) {
			return "mobile network not supported";
		}
		return "";
	}
}
