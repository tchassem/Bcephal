/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author MORISET-004
 *
 */

@Entity(name = "BookingModelPivot")
@Table(name = "BCP_ACCOUNTING_BOOKING_PIVOT")
@Data
@EqualsAndHashCode(callSuper = false)
public class BookingModelPivot extends Persistent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5807854085927356668L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_model_pivot_seq")
	@SequenceGenerator(name = "booking_model_pivot_seq", sequenceName = "booking_model_pivot_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String name;
	
	private int position;
		
	private Long attributeId;
	
	private Boolean show;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking")
	public BookingModel booking;
	
	/**
	 * getACopy
	 * 
	 * @return copy of this tree
	 */
	@JsonIgnore
	public BookingModelPivot copy() {
		BookingModelPivot copy = new BookingModelPivot();
		copy.setName(this.getName());
		copy.setShow(this.getShow());
		copy.setPosition(this.getPosition());
		copy.setAttributeId(this.getAttributeId());	
		return copy;
	}
	
	@Override
	public String toString() {
		if (!StringUtils.isBlank(name))
        {
            return name;
        }
        return super.toString();
	}
		
}
