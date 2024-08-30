package com.moriset.bcephal.etl.service.mt;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;

import com.moriset.bcephal.etl.domain.ParameterCode;
import com.prowidesoftware.swift.model.mt.AbstractMT;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public abstract class MTItemReader<T extends AbstractMT> extends AbstractItemCountingItemStreamItemReader<T>{

	String runId;
	boolean continueWhenError;
	String baseDir;
	List<String> fileNames;
	StepExecution stepExecution;
	
	public MTItemReader() {
		continueWhenError = true;
		fileNames = new ArrayList<>();
	}
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	    readParameters();
	}
	
	protected void readParameters() {
		JobParameters jobParameters = stepExecution.getJobParameters();	    
		runId = jobParameters.getString(ParameterCode.RUN_ID);
	    if(runId == null) {
	    	throw new IllegalArgumentException("Parameter not found: " + ParameterCode.RUN_ID);
	    }
		log.trace("Run ID : {}", runId);
		
		String param = jobParameters.getString(ParameterCode.CONTINUE_WHEN_ERROR);
	    if(param != null) {
	    	continueWhenError = Boolean.valueOf(param);
	    }
		log.trace("Continue when error : {}", continueWhenError);		
		
	    baseDir = jobParameters.getString(ParameterCode.BASE_DIR);
	    if(baseDir == null) {
	    	throw new IllegalArgumentException("Parameter not found: " + ParameterCode.BASE_DIR);
	    }
		log.trace("Base dir : {}", baseDir);
		
		fileNames.clear();
		String files = jobParameters.getString(ParameterCode.FILE_NAMES);
		if(files == null) {
	    	throw new IllegalArgumentException("Parameter not found: " + ParameterCode.FILE_NAMES);
	    }
		var datas = files.split(ParameterCode.FILE_SEPARATOR);		
		if(datas.length == 0) {
			throw new IllegalArgumentException("Wrong parameter : " + ParameterCode.FILE_NAMES);
		}
		for(String file : datas) {
			fileNames.add(file);
		}
		
		String outputDir = jobParameters.getString(ParameterCode.OUTPUT_DIR);
	    if(outputDir == null) {
	    	throw new IllegalArgumentException("Parameter not found: " + ParameterCode.OUTPUT_DIR);
	    }
		log.trace("Output dir : {}", outputDir);
		
		stepExecution.getExecutionContext().putString(ParameterCode.RUN_ID, runId);
		stepExecution.getExecutionContext().putString(ParameterCode.BASE_DIR, baseDir);
		stepExecution.getExecutionContext().putString(ParameterCode.OUTPUT_DIR, outputDir);
		stepExecution.getExecutionContext().putString(ParameterCode.FILE_NAMES, files);
		stepExecution.getExecutionContext().putString(ParameterCode.CONTINUE_WHEN_ERROR, param);
		stepExecution.getExecutionContext().putLong(ParameterCode.FILES_COUNT, fileNames.size());
		stepExecution.getExecutionContext().putLong(ParameterCode.FILES_READ_COUNT, 0L);
		stepExecution.getExecutionContext().putString(ParameterCode.ADD_HEADER, jobParameters.getString(ParameterCode.ADD_HEADER, "true"));
		log.trace("File count : {}", fileNames.size());
	}
		
	protected abstract T doRead(File file) throws Exception;
	
	@Override
	protected T doRead() throws Exception {
		log.debug("Try to read next item : {}", getCurrentItemCount());
		String fileName = fileNames.get(getCurrentItemCount() - 1);
		log.trace("File name : {}", fileName);
		File file = Path.of(baseDir, fileName).toFile();
		stepExecution.getExecutionContext().putString(ParameterCode.CURRENT_FILE_NAME, fileName);
		stepExecution.getExecutionContext().putLong(ParameterCode.FILES_READ_COUNT, 
				stepExecution.getExecutionContext().getLong(ParameterCode.FILES_READ_COUNT) + 1);
		if(!file.exists()) {
			handleError(new FileNotFoundException("File not found : " + file.getPath()));			
			int fileCount = fileNames.size();
			setCurrentItemCount(getCurrentItemCount() + 1) ;
			if(fileCount > 0 && getCurrentItemCount() <= fileCount) {
				return doRead();				
			}
			else {
				return null;
			}
		}
		T item = doRead(file);
		log.debug("Item readed: {}", item != null ? item.getMessageType() : null);
		return item;
	}

	private void handleError(Exception exception) throws Exception {
		if(continueWhenError) {
			log.error("Error", exception);
			log.debug("Continue");
		}
		else {
			throw exception;
		}
	}

	@Override
	protected void doOpen() throws Exception {
		setName(getClass().getName());
		
		log.trace("Reader opened...");
	}

	@Override
	protected void doClose() throws Exception {
		//baseDir = null;
		//fileNames = new ArrayList<>();
		log.trace("Reader closed...");
	}
	
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		log.trace("Context updating...");
		int fileCount = fileNames.size();
		if(fileCount == 0) {
			throw new IllegalArgumentException("There is no file to load!");
		}
		setMaxItemCount(fileCount);
		log.trace("Max item count setted to : {}", fileNames.size());
		super.update(executionContext);
	}

}
