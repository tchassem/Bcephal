package com.moriset.bcephal.etl.service.mt;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.etl.domain.ParameterCode;
import com.moriset.bcephal.etl.domain.TransactionItem;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MTItemWriter<T extends TransactionItem> extends FlatFileItemWriter<List<T>> {
	
	
	String runId;
	boolean continueWhenError;
	String baseDir;
	String outputDir;
	StepExecution stepExecution;
	TransactionsFieldExtractor<T> extractor;
	
	
	public MTItemWriter() {
		super();
		this.setExecutionContextName(ClassUtils.getShortName(getClass()));
		setAppendAllowed(true);		
		extractor = new TransactionsFieldExtractor<T>() {
			{
				setNames(getFieldNames());
			}
		};
		
		setLineAggregator(new DelimitedLineAggregator<List<T>>() {
			{
				setDelimiter("\n");
				setFieldExtractor(extractor);
			}
		});
		
	}
	
	protected String[] getFieldNames() {
		return new String[] { 								
				"messageType",
				"reference",
				"sender",
				"receiver",
				"date",
				"amount",
				"currency",
				"dc",
				"entryDate",
				"valueDate",
				"transactionType",
				"identification",
				"referenceAccOwner",
				"description",
				
				"client",
				"project",
				"user",
				"runId",
				"runDate",
				"fileName",				
			};
	}
	
	protected String[] getHeaderItems() throws ItemStreamException {  
        return new String[] {
        		"Message type",
				"Reference",
				"Sender",
				"Receiver",
				"Date",
				"Amount",
				"Currency",
				"D/C",
				"Entry date",
				"Value date",
				"Transaction type",
				"Identification",
				"ReferenceAccOwner",
				"Description",
				
				"Client",
				"Project",
				"User",
				"Run Id",
				"Run date",
				"Datasource"
        };
    }
		
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
    	String separator = executionContext.getString(ParameterCode.CSV_OUT_SEPARATOR, ParameterCode.CSV_SEPARATOR_VALUE);
    	extractor.setDelimiter(separator);    
    	
    	String param = executionContext.getString(ParameterCode.ADD_HEADER, "true");
    	boolean addHeader = Boolean.valueOf(param);
    	if(addHeader) {
	    	FlatFileHeaderCallback headerCallback = new FlatFileHeaderCallback() {			
				@Override
				public void writeHeader(Writer writer) throws IOException {
					String header = StringUtils.arrayToDelimitedString(getHeaderItems(), separator);
					writer.write(header);
				}
			};
			setHeaderCallback(headerCallback);
    	}
    	super.open(executionContext);
    }
	
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	    readParameters();
	}
	
	protected void readParameters() {		
		runId = stepExecution.getExecutionContext().getString(ParameterCode.RUN_ID);
		baseDir = stepExecution.getExecutionContext().getString(ParameterCode.BASE_DIR);
		outputDir = stepExecution.getExecutionContext().getString(ParameterCode.OUTPUT_DIR);
		String param = stepExecution.getExecutionContext().getString(ParameterCode.CONTINUE_WHEN_ERROR);
		if(param != null) {
	    	continueWhenError = Boolean.valueOf(param);
	    }	
		String currentFileName = getCurrentFileName();
		String baseName = "outputData";
		if(StringUtils.hasText(currentFileName)) {
			baseName = Path.of(currentFileName).getFileName().toString();
		}
		String fileName = baseName + "_" + runId + ".csv";
		WritableResource outputResource = new FileSystemResource(Path.of(outputDir, fileName));
		setResource(outputResource);
	}
	
	public String getCurrentFileName() {
		return stepExecution.getExecutionContext().getString(ParameterCode.CURRENT_FILE_NAME, "");
	}

}
