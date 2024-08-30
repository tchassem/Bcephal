/**
 * 
 */
package com.moriset.bcephal.security.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "ProjectBlock")
@Table(name = "BCP_SEC_PROJECT_BLOCK")
@Data
public class ProjectBlock {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_project_block_seq")
	@SequenceGenerator(name = "sec_project_block_seq", sequenceName = "sec_project_block_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@NotNull
	private Long projectId;

	@NotNull
	private Long subcriptionId;

	@NotNull
	private String code;

	private String name;

	@NotNull
	private String username;

	private int position;

	private Integer background;

	private Integer foreground;

	private boolean flowBreak;

}
