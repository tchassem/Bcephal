package com.moriset.bcephal.etl.service.mt;

import java.io.File;
import java.io.FileNotFoundException;

import com.prowidesoftware.swift.model.mt.mt9xx.MT940;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class MT940ItemReader extends MTItemReader<MT940>{
	
	@Override
	protected MT940 doRead(File file) throws Exception {
		log.debug("Try to read next MT940 from file : {}", file.getPath());
		if(!file.exists()) {
			throw new FileNotFoundException("File not found : " + file.getPath());
		}
		MT940 mt = new MT940(file);
		log.debug("MT940 readed: {}", mt != null ? mt.getMessageType() : null);
		return mt;
	}

}
