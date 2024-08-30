/**
 * 
 */
package com.moriset.bcephal.domain.condition;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.FilterVerb;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString.Exclude;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "ConditionalExpressionItem")
@Table(name = "BCP_CONDITIONAL_EXPRESSION_ITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class ConditionalExpressionItem extends Persistent {

	private static final long serialVersionUID = -486996300179306333L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conditional_expression_item_seq")
	@SequenceGenerator(name = "conditional_expression_item_seq", sequenceName = "conditional_expression_item_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	@Exclude
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "expression")
	private ConditionalExpression expression;

	@Enumerated(EnumType.STRING)
	private FilterVerb filterVerb;

	private String openBrackets;

	private String closeBrackets;

	private int position;

	private String operator;
	
	private BigDecimal value;
	
	private Long spotId1;
	
	private Long spotId2;
	
	@Enumerated(EnumType.STRING)
	private DimensionType value2Type;

	
	public ConditionalExpressionItem() {
		this.filterVerb = FilterVerb.AND;
	}
	
	
	@Override
	public Persistent copy() {
		return null;
	}
	
}
