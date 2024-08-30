/**
 * 
 */
package com.moriset.bcephal.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
//@Slf4j
public class CsvGenerator {

	public static char DEFAULT_SEPARATOR = ';';
	
	
	private CSVFormat format;
	private CSVPrinter printer;
	private String path;
	
	public CsvGenerator(String path) {
		this.path = path;
		//log.trace("CSV file : {}", path);
	}
	
	public void start() throws IOException {
		//log.trace("CSV generator starting...");
		File file = new File(path);
		if(!file.exists()) {
			if(!file.createNewFile()) {
				
			}
		}
		FileWriter writer = new FileWriter(file);
		format = CSVFormat.DEFAULT.withDelimiter(DEFAULT_SEPARATOR);
		printer = new CSVPrinter(writer, format);
		//log.trace("CSV generator started");
	}
	
	public void end() throws IOException {
		//log.trace("CSV generator ending...");
		if(printer != null) {
			printer.close();
		}
		//log.trace("CSV generator ended");
	}
	
	public void deleteFile() throws IOException {
		//log.trace("Try to delete file: {}", path);
		File file = new File(path);
		if(file.exists()) {
			FileUtils.forceDelete(file);
			//log.trace("File deleted : {}", path);
		}
	}
	
	public void dispose() throws IOException {
		deleteFile();
	}

	public void printRecord(Object... values) throws IOException {
		//log.trace("Print line: {}", values);
		printer.printRecord(values);
		printer.flush();
	}
		
}
