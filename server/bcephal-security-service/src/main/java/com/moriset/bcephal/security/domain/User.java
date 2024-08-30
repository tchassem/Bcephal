/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.message.MessageFormatMessage;
import org.keycloak.json.StringListMapDeserializer;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.SocialLinkRepresentation;
import org.keycloak.representations.idm.UserConsentRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User encapsulates the data of a person or a company who can log in and use the application.
 * 
 * @author Joseph Wambo
 *
 */
@Entity(name = "User")
@Table(name = "BCP_SEC_USER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class User  extends MainObject {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -8180566440267312023L;

	/**
	 * Unique generated id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String userId;
	
	@JsonIgnore
	private String client;
	
	@JsonIgnore	
	private Long clientId;
	
	private String username;
	private boolean enabled;
    private boolean emailVerified;
    private String firstName;
    private String lastName;
    private String email;	
	private String defaultLanguage;
	
	@Enumerated(EnumType.STRING)
	private ProfileType type;

	@Transient
	private String password;
	
	
	@Transient
	private String self; // link
	@Transient
	private String origin;
	@Transient
	@Deprecated
	private Boolean totp;
	@Transient
	private String federationLink;
	@Transient
	private String serviceAccountClientId; // For rep, it points to clientId (not DB ID)

	@Transient
    @JsonDeserialize(using = StringListMapDeserializer.class)
    private Map<String, List<String>> attributes;
	@Transient
    private List<CredentialRepresentation> credentials;
	@Transient
    private Set<String> disableableCredentialTypes;
	@Transient
    private List<String> requiredActions;
	@Transient
    private List<FederatedIdentityRepresentation> federatedIdentities;
	@Transient
    private List<String> realmRoles;
	@Transient
    private Map<String, List<String>> clientRoles;
	@Transient
    private List<UserConsentRepresentation> clientConsents;
	@Transient
    private Integer notBefore;
    
    @Transient
    @Deprecated
    private List<SocialLinkRepresentation> socialLinks;

    @Transient
    private List<String> groups;
    @Transient
	private Map<String, Boolean> access;
    
    
    
    @Transient 
	private ListChangeHandler<Nameable> profileListChangeHandler = new ListChangeHandler<>();

    
    @JsonIgnore
    public boolean isAdministrator()
    {
    	return getType() != null && getType().isAdministrator();
    }
    
    @JsonIgnore
    public boolean isSuperUser()
    {
    	return getType() != null && getType().isSuperUser();
    }
    
    @JsonIgnore
    public boolean isAdministratorOrSuperUser()
    {
    	return isAdministrator() || isSuperUser();
    }
	    
	@Override
	public Persistent copy() {
		return null;
	}
	
	@JsonIgnore
	public UserRepresentation getUserRepresentation() {
		UserRepresentation representation = new UserRepresentation();
		representation.setUsername(this.getUsername());
		representation.setEnabled(this.isEnabled());
		representation.setEmail(this.getEmail());
		representation.setEmailVerified(this.isEmailVerified());
		representation.setFirstName(this.getFirstName());
		representation.setLastName(this.getLastName());
		representation.setSelf(this.getSelf());
		representation.setOrigin(this.getOrigin());
		representation.setFederationLink(this.getFederationLink());
		representation.setServiceAccountClientId(this.getServiceAccountClientId());
		representation.setAttributes(this.getAttributes());
		representation.setCredentials(this.getCredentials());
		representation.setDisableableCredentialTypes(this.getDisableableCredentialTypes());
		representation.setFederatedIdentities(this.getFederatedIdentities());
		representation.setRealmRoles(this.getRealmRoles());
		representation.setClientRoles(this.getClientRoles());
		representation.setClientConsents(this.getClientConsents());
		representation.setNotBefore(this.getNotBefore());
		representation.setGroups(this.getGroups());		
		representation.setAccess(this.getAccess());	
		
		representation.setRequiredActions(new ArrayList<String>());
		representation.getRequiredActions().add("UPDATE_PASSWORD");
		
		representation.setCredentials(new ArrayList<>());
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setTemporary(true);
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(password);
		representation.getCredentials().add(credential);
		
		return representation;
	}
	
	@JsonIgnore
	public UserRepresentation updateUserRepresentation(UserRepresentation representation) {
		representation.setUsername(this.getUsername());
		representation.setEnabled(this.isEnabled());
		representation.setEmail(this.getEmail());
		representation.setEmailVerified(this.isEmailVerified());
		representation.setFirstName(this.getFirstName());
		representation.setLastName(this.getLastName());
		representation.setSelf(this.getSelf());
		representation.setOrigin(this.getOrigin());
		representation.setFederationLink(this.getFederationLink());
		representation.setServiceAccountClientId(this.getServiceAccountClientId());
		representation.setAttributes(this.getAttributes());
		representation.setCredentials(this.getCredentials());
		representation.setDisableableCredentialTypes(this.getDisableableCredentialTypes());
		
		representation.setFederatedIdentities(this.getFederatedIdentities());
		representation.setRealmRoles(this.getRealmRoles());
		representation.setClientRoles(this.getClientRoles());
		representation.setClientConsents(this.getClientConsents());
		representation.setNotBefore(this.getNotBefore());
		representation.setGroups(this.getGroups());		
		representation.setAccess(this.getAccess());		
		
//		representation.setRequiredActions(new ArrayList<String>());
//		representation.getRequiredActions().add("UPDATE_PASSWORD");
		
//		representation.setCredentials(new ArrayList<>());
//		CredentialRepresentation credential = new CredentialRepresentation();
//		credential.setTemporary(true);
//		credential.setType(CredentialRepresentation.PASSWORD);
//		credential.setValue(password);
//		representation.getCredentials().add(credential);
		
		return representation;
	}
	
	public void buildName() {
		setName(new MessageFormatMessage("{0}{1}{2}{3}", 
				StringUtils.hasText(this.username) ? this.username  : "",
				StringUtils.hasText(this.email) && StringUtils.hasText(this.email) ? " " : "", 
				StringUtils.hasText(this.firstName) ? this.firstName  : "", 
				StringUtils.hasText(this.lastName) ? this.lastName  : ""
			).getFormattedMessage());
		if(StringUtils.hasText(getName()) && getName().length() > 100) {
			setName(getName().substring(0, 99));
		}
	}
	
	public User(UserRepresentation userRepresentation) {
		this.setUsername(userRepresentation.getUsername());
		this.setEnabled(userRepresentation.isEnabled());
		this.setEmail(userRepresentation.getEmail());
		this.setEmailVerified(userRepresentation.isEmailVerified());
		this.setFirstName(userRepresentation.getFirstName());
		this.setLastName(userRepresentation.getLastName());
		this.setSelf(userRepresentation.getSelf());
		this.setOrigin(userRepresentation.getOrigin());
		this.setFederationLink(userRepresentation.getFederationLink());
		this.setServiceAccountClientId(userRepresentation.getServiceAccountClientId());
		this.setAttributes(userRepresentation.getAttributes());
		this.setCredentials(userRepresentation.getCredentials());
		this.setDisableableCredentialTypes(userRepresentation.getDisableableCredentialTypes());
		this.setRequiredActions(userRepresentation.getRequiredActions());
		this.setFederatedIdentities(userRepresentation.getFederatedIdentities());
		this.setRealmRoles(userRepresentation.getRealmRoles());
		this.setClientRoles(userRepresentation.getClientRoles());
		this.setClientConsents(userRepresentation.getClientConsents());
		this.setNotBefore(userRepresentation.getNotBefore());
		this.setSocialLinks(userRepresentation.getSocialLinks());
		this.setGroups(userRepresentation.getGroups());		
		this.setAccess(userRepresentation.getAccess());
	}
	
	@Override
	public String toString(){
		return getName();
	}
		
}
