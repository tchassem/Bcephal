package com.moriset.bcephal.etl.service.mt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.etl.domain.TransactionItem;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TransactionsFieldExtractor<T extends TransactionItem>  implements FieldExtractor<List<T>>, InitializingBean {

	private String[] names;

	private String delimiter = ";";
	
	@Override
	public Object[] extract(List<T> items) {
		List<String> values = new ArrayList<>();		
		for (T item : items) {
			String value = extract(item);
			if(StringUtils.hasText(value)) {
				value = value.replace("\r\n", " ").replace("\n", " ");
				values.add(value);
			}
		}
		return values.toArray();
	}
	
	protected String extract(T item) {
		List<Object> values = new ArrayList<>();
		BeanWrapper bw = new BeanWrapperImpl(item);
		for (String propertyName : this.names) {
			values.add(bw.getPropertyValue(propertyName));
		}
		Object[] fields = values.toArray();		
		return StringUtils.arrayToDelimitedString(fields, this.delimiter);
	}
	
	public void setNames(String[] names) {
		Assert.notNull(names, "Names must be non-null");
		this.names = Arrays.asList(names).toArray(new String[names.length]);
	}
	
	@Override
	public void afterPropertiesSet() {
		Assert.state(names != null, "The 'names' property must be set.");
	}
	
	
}
