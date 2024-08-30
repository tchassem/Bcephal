package com.moriset.bcephal.alarm.domain;
///**
// * 
// */
//package com.moriset.bcephal.dashboard.domain;
//
//import java.sql.Timestamp;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.SequenceGenerator;
//import javax.persistence.Table;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import com.moriset.bcephal.domain.Persistent;
//import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
//import com.moriset.bcephal.utils.JsonDateTimeSerializer;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
///**
// * @author Joseph Wambo
// *
// */
//@Entity(name = "AlarmLog")
//@Table(name = "BCP_ALARM_LOG")
//@Data
//@EqualsAndHashCode(callSuper = false)
//public class AlarmLog extends Persistent {
//
//	private static final long serialVersionUID = 6861560725661275906L;
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alarm_log_seq")
//	@SequenceGenerator(name = "alarm_log_seq", sequenceName = "alarm_log_seq", initialValue = 1,  allocationSize = 1)
//	private Long id;
//	
//	public enum AlarmModes{A, M}
//	
//	@JsonIgnore
//	private Alarm alarm;
//	
//	private String condition;
//	
//	private boolean conditionTrue;
//	
//	@Enumerated(EnumType.STRING) 
//	private AlarmModes mode;
//	
//	private String audience;
//	
//	private boolean chatSended;
//	
//	private boolean smsSended;
//	
//	private boolean mailSended;
//	
//	private String comment;
//
//	/**
//	 * <p style="margin-top: 0">
//	 * Creation date time.
//	 * </p>
//	 */
//	@JsonSerialize(using = JsonDateTimeSerializer.class)
//	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
//	@Column(nullable = false)
//	private Timestamp creationDate;
//
//	/**
//	 * <p style="margin-top: 0">
//	 * Last modification date time
//	 * </p>
//	 */
//	//@Version
//	@JsonSerialize(using = JsonDateTimeSerializer.class)
//	@JsonDeserialize(using = JsonDateTimeDeserializer.class)	
//	@Column(nullable = false)
//	private Timestamp modificationDate;
//	
//	@Override
//	public Persistent copy() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	
//	/**
//	 * Default constructor.
//	 */
//	public AlarmLog() {
//		creationDate = new Timestamp(System.currentTimeMillis());
//		modificationDate = new Timestamp(System.currentTimeMillis());
//	}
//
//}
