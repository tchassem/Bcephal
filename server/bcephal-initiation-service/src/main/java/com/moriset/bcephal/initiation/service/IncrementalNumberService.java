/**
 * 
 */
package com.moriset.bcephal.initiation.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.initiation.repository.IncrementalNumberRepository;
import com.moriset.bcephal.service.PersistentService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
public class IncrementalNumberService extends PersistentService<IncrementalNumber, BrowserData> {

	@Autowired
	IncrementalNumberRepository  incrementalNumberRepository;
	
	
	@Override
	public IncrementalNumberRepository getRepository() {
		return incrementalNumberRepository;
	}
	
	public EditorData<IncrementalNumber> getEditorData(EditorDataFilter filter, Locale locale) {
		EditorData<IncrementalNumber> data = new EditorData<>();
		if(filter.isNewData()) {
			data.setItem(getNewItem());
		}
		else {
			data.setItem(getById(filter.getId()));
		}
		return data;
	}
	
	protected IncrementalNumber getNewItem() {
		IncrementalNumber sequence = new IncrementalNumber();
		String baseName = "sequence ";
		int i = 1;
		sequence.setName(baseName + i);
		while(getByName(sequence.getName()) != null) {
			i++;
			sequence.setName(baseName + i);
		}
		return sequence;
	}
	
	protected IncrementalNumber getByName(String name) {
		log.debug("Try to  get IncrementalNumber by name : {}", name);
		if(getRepository() == null) {
			return null;
		}
		return getRepository().findByName(name);
	}
	
}
