/**
 * 
 */
package com.moriset.bcephal.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * CustomDateSerializer
 *
 * @author B-Cephal Team
 * @date 21 mars 2014
 *
 */
public class JsonDateDeserializer extends JsonDeserializer<Date> {

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

	public void serialize(Object object, JsonGenerator gen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		String formattedDate = object.toString();
		if (object instanceof Date) {
			formattedDate = dateFormat.format((Date) object);
		} else if (object instanceof Timestamp) {
			formattedDate = dateFormat.format((Timestamp) object);
		}

		gen.writeString(formattedDate);
	}

	public static Date deserialize(String text) {
		try {
			if (text != null) {
				Date date = dateFormat.parse(text);
				return date;
			}
		} catch (ParseException e) {
			try {
				Date date = dateFormat2.parse(text);
				return date;
			} catch (ParseException ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		try {
			if (jsonParser.getCurrentToken() != null) {
				String text = jsonParser.getText();
				Date date = dateFormat.parse(text);
				return date;
			}
		} catch (ParseException e) {
			try {
				String text = jsonParser.getText();
				Date date = dateFormat2.parse(text);
				return date;
			} catch (ParseException ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
