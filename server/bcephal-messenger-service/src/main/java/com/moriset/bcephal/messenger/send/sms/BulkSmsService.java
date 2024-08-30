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
public class BulkSmsService implements ConfigClient {

	private Logger logger = LoggerFactory.getLogger(BulkSmsService.class);

	public void sendSms(Sms sms) throws Exception {
		String res = "";
		OkHttpClient client = getHttpClient();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, sms.getContentSmsForBulksms());
		Request request = new Request.Builder().url(sms.getLienSms()).post(body)
				.addHeader("Authorization", sms.getAuthorization()).addHeader("content-type", "application/json")
				.build();

		Response response = client.newCall(request).execute();
		if (response.body() != null) {
			res = response.body().string();
		}
		if (response.code() == 201 || response.code() == 200) {
			logger.info("SMS sended successfully to {} ", sms.getTo());
			logger.debug("SMS sended successfully: {}", res);
		} else {
			JSONObject jsonObject = new JSONObject(res);
			if (response.code() == 400) {
				logger.error("SMS sending error: {},{}", jsonObject.get("title"), jsonObject.get("detail"));
			} else if (response.code() == 403) {
				logger.error("SMS sending error: {}", jsonObject.get("title"));
			}
			throw new Exception(res);
		}
	}
}
