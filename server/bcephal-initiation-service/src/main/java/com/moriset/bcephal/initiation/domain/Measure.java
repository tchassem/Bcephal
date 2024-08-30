/**
 * 
 */
package com.moriset.bcephal.initiation.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "Measure")
@Table(name = "BCP_MEASURE")
@Data
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class Measure extends Dimension {

	private static final long serialVersionUID = -4711002496379325339L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "measure_seq")
	@SequenceGenerator(name = "measure_seq", sequenceName = "measure_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private Measure parent;

	private BigDecimal defaultValue;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<Measure> childrenListChangeHandler;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parent")
	private List<Measure> children;

	public Measure() {
		children = new ArrayList<Measure>(0);
		childrenListChangeHandler = new ListChangeHandler<>(children);
	}

	public Measure(Long id) {
		this();
		this.setId(id);
	}

	public void setChildren(List<Measure> children) {
		this.children = children;
		childrenListChangeHandler.setOriginalList(children);
	}

	@PostLoad
	public void initListChangeHandler() {
		children.size();
		childrenListChangeHandler.setOriginalList(children);
	}

	@JsonIgnore
	@Override
	public String getUniverseTableColumnName() {
		return "MEASURE_" + getId();
	}

	@JsonIgnore
	@Override
	public String getUniverseTableColumnType() {
		return "DECIMAL (31, 14)";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Measure copy() {
		return null;
	}

}
