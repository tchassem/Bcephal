package com.moriset.bcephal.integration.domain;

import java.time.Instant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "PontoSynchronization")
@Table(name = "BCP_SEC_PONTO_SYNCHRONIZED")
@Data
@EqualsAndHashCode(callSuper = false)
public class PontoSynchronization extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4226229673515282829L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ponto_entity_sync_seq")
	@SequenceGenerator(name = "ponto_entity_sync_seq", sequenceName = "ponto_entity_sync_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	private Instant createdAt;
	
	private Long connectEntityId;
	private String resourceId;
	private String synchronizationId;
	

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
