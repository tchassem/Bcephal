package com.moriset.bcephal.etl.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.MeasureOperator;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;

import jakarta.persistence.Column;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name = "EmailFilter")
@Table(name = "bcp_email_filter")
@Data
@EqualsAndHashCode(callSuper = false)
public class EmailFilter extends Persistent {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_filter_seq")
	@SequenceGenerator(name = "email_filter_seq", sequenceName = "email_filter_seq", initialValue = 1, allocationSize = 1)
	Long id;
	
	@Enumerated(EnumType.STRING)
	private FilterVerb filterVerb;

	private String openBrackets;

	private String closeBrackets;

	private int position;
	
	String attributeValue;
	
	@Enumerated(EnumType.STRING)
	EmailAttributeFilter  attributeFilter;
	
	@Enumerated(EnumType.STRING)
	EmailFilterOperator attributeOperator;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emailAccount")
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	EmailAccount emailAccount;
	
	
	@Enumerated(EnumType.STRING)
	private PeriodOperator sendDateOperator;
	
	private String comparator;
	
	private String sign;
	
	private Integer number;
	
	@Column(name = "send_Date")
	Date sendDate;
	
	@Enumerated(EnumType.STRING)
	private PeriodGranularity granularity;
	
	public EmailFilter() {
		this.comparator = MeasureOperator.EQUALS;
		this.sign = "+";
		this.sendDateOperator = PeriodOperator.SPECIFIC;
		this.granularity = PeriodGranularity.DAY;
	}
	
	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}

	public String subjectPattern() {
		if(attributeOperator != null) {
			if(attributeFilter != null && attributeFilter.isSubject()) {
				if(attributeOperator.isEquals()) {
					return attributeValue;
				}else
					if(attributeOperator.isStartsWith()) {
						return "^(" + attributeValue + ")";
					}else
						if(attributeOperator.isEndsWith()) {
							return "(" + attributeValue + ")$";
						}else
							if(attributeOperator.isContains()) {
								return ".*" + attributeValue + ".*";
							}else
								if(attributeOperator.isNotContains()) {
									return "[^(" + attributeValue + ")]";
								}else
									if(attributeOperator.isNotEquals()) {
										return "[^(" + attributeValue + ")]";
									}
			}
		}
		return null;
	}
}
