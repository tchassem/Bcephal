/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Security Project Browser Data
 * 
 * @author
 *
 */
@Entity(name = "SecurityProjectBrowserData")
@Table(name = "BCP_SEC_PROJECT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectBrowserData {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_project_seq")
	@SequenceGenerator(name = "sec_project_seq", sequenceName = "sec_project_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private Long subscriptionId;

	@NotNull(message = "{project.code.validation.null.message}")
	@Size(min = 3, max = 50, message = "{project.code.validation.size.message}")
	private String code;

	@NotNull(message = "{project.name.validation.null.message}")
	@Size(min = 3, max = 100, message = "{project.name.validation.size.message}")
	private String name;

	private boolean defaultProject;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp creationDate;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Timestamp modificationDate;

}
