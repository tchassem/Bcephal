package com.moriset.bcephal.security.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "UniverseLocker")
@Table(name = "BCP_UNIVERSE_LOCKER")
@Data
public class UniverseLocker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	String projectCode;
	String user;
	String sessionId;
}
