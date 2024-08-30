/**
 * 
 */
package com.moriset.bcephal.initiation.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "PeriodName")
@Table(name = "BCP_PERIOD_NAME")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class PeriodName extends Dimension {

	private static final long serialVersionUID = -2590489548342882751L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "period_name_seq")
	@SequenceGenerator(name = "period_name_seq", sequenceName = "period_name_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private Date defaultValue;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	private PeriodName parent;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<PeriodName> childrenListChangeHandler;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "parent")
	private List<PeriodName> children;

	public PeriodName() {
		children = new ArrayList<PeriodName>(0);
		childrenListChangeHandler = new ListChangeHandler<>(children);
	}

	public void setChildren(List<PeriodName> children) {
		this.children = children;
		childrenListChangeHandler.setOriginalList(children);
	}

	@PostLoad
	public void initListChangeHandler() {
		for (PeriodName period : children) {
			period.getName();
		}
		childrenListChangeHandler.setOriginalList(children);
	}

	@JsonIgnore
	@Override
	public String getUniverseTableColumnName() {
		return "PERIOD_" + getId();
	}

	@JsonIgnore
	@Override
	public String getUniverseTableColumnType() {
		return "DATE";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Persistent copy() {
		return null;
	}

}
