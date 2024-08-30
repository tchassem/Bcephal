/**
 * 
 */
package com.moriset.bcephal.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * BGroup
 *
 * @author B-Cephal Team
 * @date 10 mars 2014
 *
 */

@Entity(name = "BGroup")
@Table(name = "BCP_GROUP")
@Data
@EqualsAndHashCode(callSuper = false)
public class BGroup extends MainObject {

	private static final long serialVersionUID = 24458154123516593L;
	public static String DEFAULT_GROUP_NAME = "DEFAULT";
	public static String DEFAULT_GRID_ALLOCATION_GROUP_NAME = "Allocation";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_seq")
	@SequenceGenerator(name = "group_seq", sequenceName = "group_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String subjectType;

	/** Childs */
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "group")
	private List<BGroup> children;

	@Transient
	private ListChangeHandler<BGroup> childrenListChangeHandler;

	/**
	 * Default Constructor.
	 */
	public BGroup() {
		children = new ArrayList<BGroup>(0);
		childrenListChangeHandler = new ListChangeHandler<BGroup>(children);
	}

	public BGroup(String name) {
		this();
		this.setName(name);
	}

	public BGroup(Long oid, String name) {
		this(name);
		this.setId(oid);
	}

	@JsonIgnore
	public List<BGroup> getChildren() {
		return children;
	}

	/**
	 *
	 * @param Children
	 */
	public void setChildren(List<BGroup> children) {
		this.children = children;
		childrenListChangeHandler.setOriginalList(children);
	}

	public void addGroupChild(BGroup groupChild) {
		childrenListChangeHandler.addNew(groupChild);
	}

	public void updateGroupChild(BGroup groupChild) {
		childrenListChangeHandler.addUpdated(groupChild);
	}

	public void deleteGroupChild(BGroup groupChild) {
		childrenListChangeHandler.addDeleted(groupChild);
	}

	@JsonIgnore
	public List<BGroup> getActualBGroupChild() {
		return childrenListChangeHandler.getItems();
	}

	@PostLoad
	public void initListChangeHandler() {
		this.childrenListChangeHandler.setOriginalList(children);
		children.size();
	}

	@Transient
	@JsonIgnore
	public boolean isDefault() {
		return StringUtils.hasText(getName()) && getName().equals(DEFAULT_GROUP_NAME);
	}

	@Override
	public Persistent copy() {
		return null;
	}

}
