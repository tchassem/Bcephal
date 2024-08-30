package com.moriset.bcephal.security.converters;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class DateToLongConverter implements Converter<Date, Long> {

	@Override
	public Long convert(Date source) {
		if (source == null) {
			return null;
		}
		return source.getTime();
	}

}
