package com.moriset.bcephal.project.converters;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class LocalDateTimeToTimestampConverter implements Converter<LocalDateTime, Timestamp> {

	@Override
	public Timestamp convert(LocalDateTime source) {
		if (source == null) {
			return null;
		}
		LocalDateTime source_ = (LocalDateTime) source;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(source_.getYear(), source_.getMonthValue(), source_.getDayOfYear(), source_.getHour(),
				source_.getMinute(), source_.getSecond());
		return new Timestamp(cal.getTimeInMillis());
	}
}