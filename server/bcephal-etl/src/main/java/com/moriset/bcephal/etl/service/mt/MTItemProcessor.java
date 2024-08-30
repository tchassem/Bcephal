package com.moriset.bcephal.etl.service.mt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.etl.domain.ParameterCode;
import com.moriset.bcephal.etl.domain.TransactionItem;
import com.prowidesoftware.swift.model.mt.AbstractMT;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public abstract class MTItemProcessor<T extends AbstractMT, P extends TransactionItem> implements ItemProcessor<T, List<P>> {
	
	String runId;
	boolean continueWhenError;
	
	String decimalInSeparator;
	Long decimalInCount;
	String dateInFormat;
	
	String decimalOutSeparator;
	Long decimalOutCount;
	String dateOutFormat;
	
	StepExecution stepExecution;
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	    readParameters();
	}
	
	protected void readParameters() {		
		runId = stepExecution.getExecutionContext().getString(ParameterCode.RUN_ID);
		String param = stepExecution.getExecutionContext().getString(ParameterCode.CONTINUE_WHEN_ERROR);
		if(param != null) {
	    	continueWhenError = Boolean.valueOf(param);
	    }
		
		JobParameters jobParameters = stepExecution.getJobParameters();	
		
		dateInFormat = jobParameters.getString(ParameterCode.DATE_IN_FORMAT, ParameterCode.DATE_IN_FORMAT_VALUE);	    
		log.trace("Date in format : {}", dateInFormat);
		
		dateOutFormat = jobParameters.getString(ParameterCode.DATE_OUT_FORMAT, ParameterCode.DATE_OUT_FORMAT_VALUE);	    
		log.trace("Date out format : {}", dateOutFormat);
		
		decimalInSeparator = jobParameters.getString(ParameterCode.DECIMAL_IN_SEPARATOR, ParameterCode.DECIMAL_IN_SEPARATOR_VALUE);	    
		log.trace("Decimal in separator : {}", decimalInSeparator);
		
		decimalInCount = jobParameters.getLong(ParameterCode.DECIMAL_IN_COUNT, ParameterCode.DECIMAL_IN_COUNT_VALUE);	    
		log.trace("Decimal in count : {}", decimalInCount);
		
		decimalOutSeparator = jobParameters.getString(ParameterCode.DECIMAL_OUT_SEPARATOR, ParameterCode.DECIMAL_OUT_SEPARATOR_VALUE);	    
		log.trace("Decimal out separator : {}", decimalOutSeparator);
		
		decimalOutCount = jobParameters.getLong(ParameterCode.DECIMAL_OUT_COUNT, ParameterCode.DECIMAL_OUT_COUNT_VALUE);	    
		log.trace("Decimal out count : {}", decimalOutCount);
		
	}
	
	public String getCurrentFileName() {
		return stepExecution.getExecutionContext().getString(ParameterCode.CURRENT_FILE_NAME, "");
	}
	
	
	
	protected String formatInToOutDate(String dateString, String format) throws ParseException  {
		Date date = formatInDate(dateString, format);
		return formatDate(date);
	}
	
	protected String formatInToOutDecimal(String decimalString) throws ParseException  {
		Number number = formatInDecimal(decimalString);
		return formatDecimal(number);
	}
	
	
	
	protected Date formatInDate(String dateString, String format) throws ParseException  {
		if(dateString != null && StringUtils.hasText(dateString.trim())) {
			dateString = dateString.trim();
			return new SimpleDateFormat(format).parse(dateString);
		}
		return null;
	}
	
	protected Number formatInDecimal(String decimalString) throws ParseException  {
		if(decimalString != null && StringUtils.hasText(decimalString.trim())) {
			decimalString = decimalString.trim();
			if(decimalInSeparator != null && decimalString.endsWith(decimalInSeparator)) {
				decimalString = decimalString.substring(0, decimalString.length() - 1);
			}
			String pattern = "#0";
			if(StringUtils.hasText(decimalInSeparator)) {
				pattern += decimalInSeparator;
			}
			int count = decimalInCount != null ? decimalInCount.intValue() : 0;			
			for (int i = 0; i < count; i++) {
				pattern += "0";
			}
			return new DecimalFormat(pattern).parse(decimalString);
		}
		return null;
	}
	
	protected String formatDate(Date date) {
		if(date != null) {
			return new SimpleDateFormat(dateOutFormat).format(date);
		}
		return "";
	}
	
	protected String formatDecimal(Number number) {
		if(number != null) {
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
			String pattern = "#0";
			if(StringUtils.hasText(decimalOutSeparator)) {
				pattern += decimalOutSeparator;
				symbols.setDecimalSeparator(decimalOutSeparator.charAt(0));
			}
			int count = decimalOutCount != null ? decimalOutCount.intValue() : 0;			
			for (int i = 0; i < count; i++) {
				pattern += "0";
			}
			
			DecimalFormat formatter = new DecimalFormat(pattern);
			formatter.setDecimalFormatSymbols(symbols);
			
			return formatter.format(number);
		}
		return "";
	}

}
