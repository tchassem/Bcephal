/**
 * 
 */
package com.moriset.bcephal.dashboard.domain;

import com.moriset.bcephal.domain.Persistent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author MORISET-004
 *
 */
@SuppressWarnings("serial")
@jakarta.persistence.MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class DashboardReportProperties extends Persistent {

	private String webLayoutData;

	private String title;
}
