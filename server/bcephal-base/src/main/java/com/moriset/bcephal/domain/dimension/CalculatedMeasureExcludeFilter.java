package com.moriset.bcephal.domain.dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "CalculatedMeasureExcludeFilter")
@Table(name = "BCP_CALCULATED_MEASURE_EXCLUDE_FILTER")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CalculatedMeasureExcludeFilter extends Persistent {

	private static final long serialVersionUID = -8384258308864575351L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calculated_measure_ex_fil_seq")
	@SequenceGenerator(name = "calculated_measure_ex_fil_seq", sequenceName = "calculated_measure_ex_fil_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item")
	private CalculatedMeasureItem item;
	
	private Long dimensionId;

	@Enumerated(EnumType.STRING)
	private DimensionType type;
	
	private int position;
	
	private boolean active;

	@Override
	public CalculatedMeasureExcludeFilter copy() {
		CalculatedMeasureExcludeFilter copy = CalculatedMeasureExcludeFilter.builder()
				.active(active)
				.position(position)
				.dimensionId(dimensionId)
				.type(type)
				.build(); 
		return copy;
	}
	
}
