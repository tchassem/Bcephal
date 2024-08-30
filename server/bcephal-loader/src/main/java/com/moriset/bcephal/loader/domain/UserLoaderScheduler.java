package com.moriset.bcephal.loader.domain;

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
import lombok.ToString;

@Entity(name = "UserLoaderScheduler")
@Table(name = "BCP_USER_LOADER_SCHEDULER")
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserLoaderScheduler extends Persistent {

	private static final long serialVersionUID = 760057022897240953L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_loader_scheduler_seq")
	@SequenceGenerator(name = "user_loader_scheduler_seq", sequenceName = "user_loader_scheduler_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@ToString.Exclude @EqualsAndHashCode.Exclude	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loaderId")
	private UserLoader loader;
	
	@Enumerated(EnumType.STRING)
	private UserLoaderSchedulerType type;
		
	private Long schedulerId;
	
	private int position;
	
	private boolean active;
	
	public UserLoaderScheduler() {
		type = UserLoaderSchedulerType.AFTER;
		active = true;
	}

	@Override
	public UserLoaderScheduler copy() {
		UserLoaderScheduler copy = UserLoaderScheduler.builder() 
				.position(position)
				.schedulerId(schedulerId)
				.type(type)
				.active(active)
				.build();
		return copy;
	}
	
}
