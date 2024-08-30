/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Security Project
 * 
 * @author Joseph Wambo
 *
 */
@Entity(name = "SecurityProject")
@Table(name = "BCP_SEC_PROJECT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_project_seq")
	@SequenceGenerator(name = "sec_project_seq", sequenceName = "sec_project_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@JsonIgnore
	@NotNull(message = "{project.subscription.validation.null.message}")
	@Column(name = "subscriptionId")
	private Long subscriptionId;
	
	@JsonIgnore
	private String client;

	@NotNull(message = "{project.code.validation.null.message}")
	@Size(min = 3, max = 255, message = "{project.code.validation.size.message}")
	private String code;

	@NotNull(message = "{project.name.validation.null.message}")
	@Size(min = 3, max = 100, message = "{project.name.validation.size.message}")
	private String name;

	@Size(max = 255, message = "{project.description.validation.size.message}")
	private String description;

	@Size(max = 255, message = "{project.path.validation.size.message}")
	private String path;

	@Size(max = 100, message = "{project.dbname.validation.size.message}")
	private String dbname;

	@Size(max = 50, message = "{project.user.name.validation.size.message}")
	private String username;

	@Column(name = "defaultProject")
	private boolean defaultProject;

	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(name = "creationDate")
	private Timestamp creationDate;
	
	@Builder.Default
	@Transient
	private boolean allowCodeBuilder = true;

}
