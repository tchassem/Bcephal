/**
 * 
 */
package com.moriset.bcephal.grid.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.utils.JsonDateDeserializer;
import com.moriset.bcephal.utils.JsonDateSerializer;

import lombok.Data;

/**
 * @author Moriset
 *
 */
@Data
public class MaterializedGridEditedElement {

	private Long id;

	private MaterializedGridColumn column;

	private String stringValue;

	private BigDecimal decimalValue;

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateValue;

	private MaterializedGrid grid;
	
}
