/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 
 * @author MORISET-004
 *
 */

@Entity(name = "BookingModelLog")
@Table(name = "BCP_ACCOUNTING_BOOKING_MODEL_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class BookingModelLog  extends Persistent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5026551535479693013L;


	public enum BookingModelLogStatus{STARTED, ENDED}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_model_log_seq")
	@SequenceGenerator(name = "booking_model_log_seq", sequenceName = "booking_model_log_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private Long modelId;
	
	private String modelName;
		
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp endDate;
	
	@Enumerated(EnumType.STRING) 
	private BookingModelLogStatus status;
	
	@Enumerated(EnumType.STRING) 
	private RunModes mode;
		
	@Column(name = "username")
	private String user;			
	private Long accountCount;	
	private Long postingEntryCount;	
	private Long periodCount;		
	private Long bookingItemCount;
	
	public BookingModelLog(){
		this.accountCount = 0L;
		this.postingEntryCount = 0L;
	}

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}	
	
}

