/**
 * 
 */
package com.moriset.bcephal.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * CustomDateSerializer
 *
 * @author B-Cephal Team
 * @date 21 mars 2014
 *
 */
public class JsonDateTimeDeserializer extends JsonDeserializer<Object> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	@Override
	public Object deserialize(JsonParser jsonParser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		try {
			if (jsonParser.getCurrentToken() != null) {
				String text = jsonParser.getText();
				Date date = dateFormat.parse(text);
				return new Timestamp(date.getTime());
			}
		} catch (ParseException e) {
			try {
				String text = jsonParser.getText();
				Date date = dateFormat2.parse(text);
				return new Timestamp(date.getTime());
			} catch (ParseException e1) {
				try {
					String text = jsonParser.getText();
					return new Timestamp(Long.valueOf(text));
				} catch (Exception e2) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
