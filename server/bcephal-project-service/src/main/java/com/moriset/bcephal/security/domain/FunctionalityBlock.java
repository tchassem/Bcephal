package com.moriset.bcephal.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.IPersistent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@SuppressWarnings("serial")
@Entity(name = "FunctionalityBlock")
@Table(name = "BCP_SEC_FUNCTIONALITY_BLOCK")
@Data
public class FunctionalityBlock implements IPersistent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_functionality_block_seq")
	@SequenceGenerator(name = "sec_functionality_block_seq", sequenceName = "sec_functionality_block_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@NotNull
	private Long projectId;

	@JsonIgnore
	@ManyToOne
	private FunctionalityBlockGroup groupId;

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
