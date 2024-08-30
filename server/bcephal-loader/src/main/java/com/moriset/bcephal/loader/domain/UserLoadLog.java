package com.moriset.bcephal.loader.domain;

import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "UserLoadLog")
@Table(name = "BCP_USER_LOAD_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class UserLoadLog extends Persistent {
	
	private static final long serialVersionUID = -1285941241171766869L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_load_log_seq")
	@SequenceGenerator(name = "user_load_log_seq", sequenceName = "user_load_log_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	private Long loadId;
	
	private String file;
	
	private String message;
	
	private int lineCount;

	private boolean empty;

	private boolean loaded;

	private boolean error;
	
	private int position;

	@Override
	public Persistent copy() {
		return null;
	}
	
}
