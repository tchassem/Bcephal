package com.moriset.bcephal.loader.service;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlToCsvUtils {
		

	public static void generateCsv(String styleFile, String inputFile, String outputFile) {     
		log.debug("Try to transform xml file to csv file");
		if(!StringUtils.hasText(styleFile)) {
			throw new BcephalException("Unable to transform XLM file: style file is NULL!");
		}
		if(!StringUtils.hasText(inputFile)) {
			throw new BcephalException("Unable to transform XLM file: input file is NULL!");
		}
		if(!StringUtils.hasText(outputFile)) {
			throw new BcephalException("Unable to transform XLM file: output file is NULL!");
		}		
		
        try {
        	Source xmlSource = new StreamSource(inputFile);
        	Source xsltSource = new StreamSource(styleFile);

        	TransformerFactory transformerFactory = TransformerFactory.newInstance();
        	Transformer transformer = transformerFactory.newTransformer(xsltSource);

        	StreamResult result = new StreamResult(outputFile);
        	transformer.transform(xmlSource, result);
        	System.out.println("XSLT transformation completed successfully.");
        	
        } 
        catch (TransformerException e) {
            log.error("Unable to transform XML file to CSV file", e);
            throw new BcephalException("Unable to transform XML file to CSV file.", e);
        }
    }
	
}
