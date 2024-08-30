/**
 * 
 */
package com.moriset.bcephal.security.domain;

import org.keycloak.representations.idm.ClientRepresentation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @author MORISET-004
 *
 */
@Entity(name = "Client")
@Table(name = "BCP_SEC_CLIENT")
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Client extends ClientBaseObject {


	private static final long serialVersionUID = 2270604580221074671L;

	@JsonIgnore
	public ClientRepresentation getClientRepresentation() {
		ClientRepresentation representation = new ClientRepresentation();
		representation.setName(this.getName());
		representation.setClientId(buildClientId());
		representation.setDescription(getDescription());
		representation.setEnabled(isEnabled());
		representation.setSecret(getSecret());
		representation.setPublicClient(false);
		representation.setProtocol("openid-connect");
		representation.setServiceAccountsEnabled(true);
		representation.setAuthorizationServicesEnabled(true);		
		return representation;
	}
	
	@JsonIgnore
	public ClientRepresentation updateClientRepresentation(ClientRepresentation representation) {
		representation.setName(this.getName());
		representation.setDescription(getDescription());
		representation.setEnabled(isEnabled());
		//representation.setSecret(getSecret());
		return representation;
	}
	
	private String buildClientId() {
		if(this.getName() != null) {
			return getName();
		}
		return null;
	}
}
