package com.moriset.bcephal.security.converters;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;


@WritingConverter
public class TimestampToLocalDateTimeConverter implements Converter<Timestamp, LocalDateTime>{

	@Override
	public LocalDateTime convert(Timestamp source) {
		if (source == null) {
            return null;
        }
		return source.toLocalDateTime();
	}
}
