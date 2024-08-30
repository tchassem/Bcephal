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
 * CustomDateSerializer
 *
 * @author B-Cephal Team
 * @date 21 mars 2014
 *
 */
public class JsonDateSerializer extends JsonSerializer<Object> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public void serialize(Object object, JsonGenerator gen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		String formattedDate = object.toString();

		try {
			if (object instanceof Date) {
				formattedDate = dateFormat.format((Date) object);
			} else if (object instanceof Timestamp) {
				formattedDate = dateFormat.format((Timestamp) object);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			if (object instanceof Date) {
				formattedDate = dateFormat2.format((Date) object);
			} else if (object instanceof Timestamp) {
				formattedDate = dateFormat2.format((Timestamp) object);
			}
		}

		gen.writeString(formattedDate);
	}

}
