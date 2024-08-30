/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.representations.idm.RoleRepresentation;

/**
 * @author Moriset
 *
 */
public class RoleBuilder {
	
	public static final String PROJECT = "project";
	public static final String PROJECT_CREATE = "project.create";
	public static final String PROJECT_DELETE = "project.delete";
	
	public static final String INITIALIZATION = "initialization";
	
	public static final String SOURCING = "sourcing";
	
	public static final String TRANSFORMATION = "transformation";
	
	public static final String REPORTING = "reporting";
	
	public static final String RECONCILIATION = "reconciliation";

	public List<RoleRepresentation> buildRoleRepresentations() {
		List<RoleRepresentation> roles = new ArrayList<>();
		roles.add(buildProject());
		roles.add(buildInitialization());
		roles.add(buildSourcing());
		roles.add(buildTransformation());
		roles.add(buildReconciliation());
		roles.add(buildReporting());		
//		roles.add(buildBilling());
//		roles.add(buildAccounting());
//		roles.add(buildDataManagement());
//		roles.add(buildAdministration());
//		roles.add(buildSettings());
		
		return roles;
	}
	
	protected RoleRepresentation buildProject() {
		RoleRepresentation role = buildRole(PROJECT, "Project role");
		role.setComposite(true);		
		return role;
	}
	
	protected RoleRepresentation buildInitialization() {
		RoleRepresentation role = buildRole(INITIALIZATION, "Initialization module");
		role.setComposite(true);
		return role;
	}
	
	protected RoleRepresentation buildSourcing() {
		RoleRepresentation role = buildRole(SOURCING, "Sourcing role");
		role.setComposite(true);
		return role;
	}
	
	protected RoleRepresentation buildTransformation() {
		RoleRepresentation role = buildRole(TRANSFORMATION, "Transformation module");
		role.setComposite(true);
		return role;
	}
	
	protected RoleRepresentation buildReconciliation() {
		RoleRepresentation role = buildRole(RECONCILIATION, "Reconciliation module");
		role.setComposite(true);
		return role;
	}
	
	protected RoleRepresentation buildReporting() {
		RoleRepresentation role = buildRole(REPORTING, "Reporting module");
		role.setComposite(true);
		return role;
	}
	
	
	
	
	private RoleRepresentation buildRole(String name, String description) {
		RoleRepresentation role = new RoleRepresentation();
		role.setId(name);
		role.setName(name);
		role.setDescription(description);
		role.setComposite(false);
		return role;
	}
	
}
