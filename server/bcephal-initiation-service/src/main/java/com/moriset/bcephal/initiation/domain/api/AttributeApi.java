/**
 * 
 */
package com.moriset.bcephal.initiation.domain.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Moriset
 *
 */
@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AttributeApi extends DimensionApi {

	private boolean declared;

	private Long entity;

}
