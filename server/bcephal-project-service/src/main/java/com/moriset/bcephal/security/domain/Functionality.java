package com.moriset.bcephal.security.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 
 * @author MORISET-004
 *
 */
//@Entity(name = "FunctionalityProject")
//@Table(name = "BCP_SEC_FUNCTIONALITY")
@Data
public class Functionality {

//	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_functionality_seq")
//	@SequenceGenerator(name = "sec_functionality_seq", sequenceName = "sec_functionality_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@NotNull(message = "{functionality.code.validation.null.message}")
	@Size(min = 3, max = 50, message = "{functionality.code.validation.size.message}")
	private String code;

	// @Size(min = 3, max = 50, message =
	// "{functionality.parentcode.validation.size.message}")
	// private String parentcode;

	private String name;

	private Functionality parent;
	private boolean showInDashboard;
	// private SubjectType subjectType;
	private List<Functionality> children;

	// private List<RightType> RightTypes;

	// private RightField field;

	private boolean canView;

	private boolean canEdit;

	@JsonIgnore
	public Functionality getParent() {
		return parent;
	}
}
