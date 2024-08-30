/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;
/**
 * 
 * @author MORISET-004
 *
 */
public class BookingModelScheduledJob extends BrowserData {

	public String projectName;
	public String projectCode;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	public Date nextFireTime;

	public boolean currentlyExecuting = false;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	public Date previousFireTime;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	public Date currentlyExecutionFireTime;

	public Long runTime;
}
