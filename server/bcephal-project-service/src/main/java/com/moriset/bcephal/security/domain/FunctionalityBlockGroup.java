package com.moriset.bcephal.security.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity(name = "FunctionalityBlockGroup")
@Table(name = "BCP_SEC_FUNCTIONALITY_BLOCK_GROUP")
@Data
public class FunctionalityBlockGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_functionality_block_group_seq")
	@SequenceGenerator(name = "sec_functionality_block_group_seq", sequenceName = "sec_functionality_block_group_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@NotNull
	private Long projectId;

	private String name;

	@NotNull
	private String username;

	private int position;

	private Integer background;

	private Integer foreground;

	private boolean defaultGroup;

	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "groupId")
	private List<FunctionalityBlock> blocks;

	@Transient
	private ListChangeHandler<FunctionalityBlock> blockListChangeHandler;

	public FunctionalityBlockGroup() {
		this.blockListChangeHandler = new ListChangeHandler<FunctionalityBlock>();
	}

	public void setBlocks(List<FunctionalityBlock> blocks) {
		this.blocks = blocks;
		blockListChangeHandler.setOriginalList(blocks);
	}

	@PostLoad
	public void initListChangeHandler() {
		blocks.forEach(item -> {
		});
		this.blockListChangeHandler.setOriginalList(blocks);
	}

}
