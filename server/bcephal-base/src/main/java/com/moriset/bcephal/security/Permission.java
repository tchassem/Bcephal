package com.moriset.bcephal.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasAuthority('admin')")
	public @interface AdminRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasAuthority('superUser')")
	public @interface SuperUserRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasAuthority('admin') || hasAuthority('superUser')")
	public @interface AdminORSuperUserRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasAuthority('admin') && hasAuthority('superUser')")
	public @interface AdminAndSuperUserRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasAuthority('user')")
	public @interface UserRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasRole('checkAlive')")
	public @interface checkRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasAuthority('admin')")
	public @interface HasAdminAuth {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasAuthority('ROLE_checkAlive')")
	public @interface HasSheckAuth {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasResourcePermission('Project resource')")
	public @interface ProjectPermission {
	}

}
