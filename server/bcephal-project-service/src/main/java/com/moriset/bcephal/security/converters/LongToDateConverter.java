package com.moriset.bcephal.security.converters;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class LongToDateConverter implements Converter<Long, Date> {

	@Override
	public Date convert(Long source) {
		if (source == null) {
			return null;
		}
		return new Timestamp(Long.valueOf(String.valueOf(source)));
	}
}