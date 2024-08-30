/**
 * 
 */
package com.moriset.bcephal.security.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "ClientBase")
@Table(name = "BCP_SEC_CLIENT")
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class ClientBase extends ClientBaseObject {/**
	 * 
	 */
	private static final long serialVersionUID = 7502748476294948849L;

}
