package com.moriset.bcephal.security.converters;

import java.sql.Timestamp;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public  class NumberToTimestampConverter implements Converter<Number, Timestamp> {

	@Override
	public Timestamp convert(Number source) {
		if (source == null) {
            return null;
        }
		return new Timestamp(Long.valueOf(String.valueOf(source)));
	}
}