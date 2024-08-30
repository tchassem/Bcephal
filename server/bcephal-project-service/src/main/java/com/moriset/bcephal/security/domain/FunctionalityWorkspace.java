/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.util.List;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
public class FunctionalityWorkspace {

	private List<Functionality> availableFunctionalities;
	private List<FunctionalityBlockGroup> functionalityBlockGroups;

}
