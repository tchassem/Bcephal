/**
 * 
 */
package com.moriset.bcephal.archive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author MORISET-004
 *
 */
@Entity(name = "ArchiveLog")
@Table(name = "BCP_ARCHIVE_LOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class ArchiveLog extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6831185982978289336L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "archive_log_seq")
	@SequenceGenerator(name = "archive_log_seq", sequenceName = "archive_log_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@JsonIgnore
	private Long archiveId;

	private String name;

	private String userName;

	private String message;

	private long lineCount;

	@Enumerated(EnumType.STRING)
	private ArchiveLogAction action;

	@Enumerated(EnumType.STRING)
	private ArchiveLogStatus status;

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
}
