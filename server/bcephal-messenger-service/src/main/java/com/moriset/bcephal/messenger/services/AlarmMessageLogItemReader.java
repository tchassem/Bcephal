/**
 * 
 */
package com.moriset.bcephal.messenger.services;

import java.util.HashMap;

import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Sort.Direction;

import com.moriset.bcephal.messenger.model.AlarmMessageLogToSend;

/**
 * @author Moriset
 *
 */
public class AlarmMessageLogItemReader extends RepositoryItemReader<AlarmMessageLogToSend> {
	
	public AlarmMessageLogItemReader() {
		setMethodName("findAll");
		HashMap<String, Direction> sorts = new HashMap<>();
		sorts.put("id", Direction.ASC);
		setSort(sorts);
	}
	
	
}
