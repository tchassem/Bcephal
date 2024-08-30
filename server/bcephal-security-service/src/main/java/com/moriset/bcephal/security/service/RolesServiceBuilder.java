/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.Nameable;

/**
 * @author MORISET-004
 *
 */
public class RolesServiceBuilder {

	
	
	public List<Nameable> getRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.addAll(getProjectRoles());
		roles.addAll(getInitiationRoles());
		roles.addAll(getSourcingRoles());
		roles.addAll(getTransformationRoles());
		roles.addAll(getReportsRoles());
		roles.addAll(getDashboardRoles());
		roles.addAll(getReconciliationRoles());
		roles.addAll(getInvoiceRoles());
		roles.addAll(getAccountingRoles());
		roles.addAll(getArchiveRoles());
		roles.addAll(getAdministrationRoles());
		roles.addAll(getParametersRoles());
		
		return roles;
	}
	
	
	private  List<Nameable> getProjectRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "project"));
		roles.add(new Nameable(null, "project.backup"));
		return roles;
	}
	
	private List<Nameable> getInitiationRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "initiation"));
		roles.add(new Nameable(null, "initiation.model"));
		roles.add(new Nameable(null, "initiation.measure"));
		roles.add(new Nameable(null, "initiation.period"));
		return roles;
	}
	
	private List<Nameable> getSourcingRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "sourcing"));
		roles.add(new Nameable(null, "sourcing.spreadsheet"));
		roles.add(new Nameable(null, "sourcing.grid"));
		roles.add(new Nameable(null, "sourcing.spot"));
		roles.add(new Nameable(null, "sourcing.accses.right"));
		return roles;
	}
	
	private List<Nameable> getTransformationRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "transformation"));
		roles.add(new Nameable(null, "transformation.tree"));
		return roles;
	}
	
	private List<Nameable> getReportsRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "reports"));
		roles.add(new Nameable(null, "reports.report"));
		roles.add(new Nameable(null, "reports.spreadsheet"));
		roles.add(new Nameable(null, "reports.grid"));
		roles.add(new Nameable(null, "reports.pivot.table"));		
		roles.add(new Nameable(null, "reports.chart"));
		return roles;
	}
	
	private List<Nameable> getDashboardRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "dashboard"));
		roles.add(new Nameable(null, "dashboard.dashboard"));
		roles.add(new Nameable(null, "dashboard.Alarm"));
		return roles;
	}
	
	private List<Nameable> getReconciliationRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "reconciliation"));
		roles.add(new Nameable(null, "reconciliation.filter"));
		roles.add(new Nameable(null, "reconciliation.automatic.reconciliation"));
		roles.add(new Nameable(null, "reconciliation.automatic.reconciliation.scheduling"));
		return roles;
	}
	
	private List<Nameable> getInvoiceRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "invoice"));
		roles.add(new Nameable(null, "invoice.repository"));
		roles.add(new Nameable(null, "invoice.model"));
		roles.add(new Nameable(null, "invoice.run"));
		return roles;
	}
	
	private List<Nameable> getAccountingRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "accounting"));
		roles.add(new Nameable(null, "accounting.repository"));
		roles.add(new Nameable(null, "accounting.posting"));
		roles.add(new Nameable(null, "accounting.posting.log"));
		roles.add(new Nameable(null, "accounting.booking"));
		roles.add(new Nameable(null, "accounting.booking.log"));
		return roles;
	}
	
	
	private List<Nameable> getArchiveRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "archive"));
		roles.add(new Nameable(null, "archive.repository"));
		roles.add(new Nameable(null, "archive.log"));
		roles.add(new Nameable(null, "archive.configuration"));
		return roles;
	}
	
	private List<Nameable> getAdministrationRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "administration"));
		roles.add(new Nameable(null, "administration.client"));
		roles.add(new Nameable(null, "administration.user"));
		roles.add(new Nameable(null, "administration.profil"));
		return roles;
	}
	
	private List<Nameable> getParametersRoles(){
		List<Nameable> roles = new ArrayList<>();
		roles.add(new Nameable(null, "parameters"));
		roles.add(new Nameable(null, "parameters.groups"));
		roles.add(new Nameable(null, "parameters.incremental.number"));
		return roles;
	}

}
