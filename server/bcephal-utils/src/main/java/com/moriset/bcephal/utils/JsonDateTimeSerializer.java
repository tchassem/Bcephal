/**
 * 
 */
package com.moriset.bcephal.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 
 * JsonDateTimeSerializer
 *
 * @author B-Cephal Team
 * @date 21 mars 2014
 *
 */
public class JsonDateTimeSerializer extends JsonSerializer<Object> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	@Override
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

}
