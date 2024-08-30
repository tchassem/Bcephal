/**
 * 
 */
package com.moriset.bcephal.domain.condition;

import java.util.ArrayList;
import java.util.List;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "ConditionalExpression")
@Table(name = "BCP_CONDITIONAL_EXPRESSION")
@Data
@EqualsAndHashCode(callSuper = false)
public class ConditionalExpression extends Persistent {

	private static final long serialVersionUID = 5063117001651301639L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conditional_expression_seq")
	@SequenceGenerator(name = "conditional_expression_seq", sequenceName = "conditional_expression_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	
	
	@Transient
	private ListChangeHandler<ConditionalExpressionItem> itemListChangeHandler;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(cascade = jakarta.persistence.CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "expression")
	private List<ConditionalExpressionItem> items;

	
	public ConditionalExpression() {
		items = new ArrayList<>(0);
		itemListChangeHandler = new ListChangeHandler<>();
	}
	
	public void setItems(List<ConditionalExpressionItem> items) {
		this.items = items;
		itemListChangeHandler.setOriginalList(items);
	}

	@PostLoad
	public void initListChangeHandler() {
		items.size();
		itemListChangeHandler.setOriginalList(items);
	}
	
	
	@Override
	public Persistent copy() {
		return null;
	}
	
	
}
