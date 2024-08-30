/**
 * 
 */
package com.moriset.bcephal.loader.domain;

/**
 * @author Joseph Wambo
 *
 */
public enum FileLoaderMethod {

	DIRECT_TO_GRID, 
	NEW_GRID, 
	VIA_AUTOMATIC_SOURCING, 
	VIA_INPUT_TABLE,
	DIRECT_TO_MATERIALIZED_GRID, 
	NEW_MATERIALIZED_GRID, 
}
