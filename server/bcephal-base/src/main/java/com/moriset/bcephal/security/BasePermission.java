package com.moriset.bcephal.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
public @interface BasePermission {

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasRole('create')")
	public @interface CreateRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasRole('edite')")
	public @interface EditeRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasRole('create') or hasRole('edite')")
	public @interface CreateOrEditRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasRole('view')")
	public @interface ViewRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasRole('print')")
	public @interface PrintRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasRole('root')")
	public @interface RootRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasRole('view') and hasRole('print')")
	public @interface ViewAndPrintRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasRole('delete')")
	public @interface DeleteRole {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasRole('create') and hasRole('delete')")
	public @interface CreateAndDeleteRole {
	}
}
