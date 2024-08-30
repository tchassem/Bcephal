package com.moriset.bcephal.project.converters;

import java.sql.Timestamp;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class TimestampToNumberConverter implements Converter<Timestamp, Long> {

	@Override
	public Long convert(Timestamp source) {
		if (source == null) {
			return null;
		}
		return source.getTime();
	}

}
