/**
 * 
 */
package com.moriset.bcephal.initiation.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "CalendarCategory")
@Table(name = "BCP_CALENDAR_CATEGORY")
@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CalendarCategory extends Persistent {

	private static final long serialVersionUID = -5263331164484291343L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendar_category_seq")
	@SequenceGenerator(name = "calendar_category_seq", sequenceName = "calendar_category_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	public String name;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "category")
	private List<CalendarDay> days;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<CalendarDay> dayListChangeHandler;

	@ToString.Exclude @EqualsAndHashCode.Exclude
	@Transient
	private ListChangeHandler<CalendarCategory> children;

	public CalendarCategory() {
		days = new ArrayList<CalendarDay>();
		dayListChangeHandler = new ListChangeHandler<CalendarDay>();
		children = new ListChangeHandler<CalendarCategory>();
	}

	public void setDays(List<CalendarDay> days) {
		this.days = days;
		dayListChangeHandler.setOriginalList(days);
	}

	@PostLoad
	public void initListChangeHandler() {
		dayListChangeHandler.setOriginalList(days);
	}

	@Override
	public Persistent copy() {
		return null;
	}

}
