package com.moriset.bcephal.loader.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.FilterVerb;

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
import lombok.ToString;

@Entity(name = "UserLoaderCondition")
@Table(name = "BCP_USER_LOADER_CONDITION")
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserLoaderCondition extends Persistent {

	private static final long serialVersionUID = -5683766625716296818L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_loader_condition_seq")
	@SequenceGenerator(name = "user_loader_condition_seq", sequenceName = "user_loader_condition_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loaderId")
	private UserLoader loader;
	
	@Enumerated(EnumType.STRING)
	private UserLoaderItemType type;
	
	private int position;
	
	private boolean active;
		
	@Enumerated(EnumType.STRING)
	private FilterVerb verb;
    private String openingBracket;
    private String closingBracket;
    private String comparator;
    
    private String errorMessage;
    private Long longValue;
    private String stringValue;
    private BigDecimal decimalValue;
	
	
	public UserLoaderCondition() {
		type = UserLoaderItemType.FILE_EXTENSION;
		active = true;
	}

	@Override
	public UserLoaderCondition copy() {
		UserLoaderCondition copy = UserLoaderCondition.builder() 
				.position(position)
				.active(active)
				.type(type)
				.verb(verb)
				.openingBracket(openingBracket)
				.closingBracket(closingBracket)
				.comparator(comparator)
				.errorMessage(errorMessage)
				.longValue(longValue)
				.stringValue(stringValue)
				.build();
		return copy;
	}
	
}
