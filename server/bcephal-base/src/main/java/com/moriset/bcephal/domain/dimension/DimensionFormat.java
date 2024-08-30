/**
 * 
 */
package com.moriset.bcephal.domain.dimension;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
@Embeddable
public class DimensionFormat implements Serializable {

	private static final long serialVersionUID = -6252801713510278119L;

	private int nbrOfDecimal;

	private boolean usedSeparator;

	private String defaultFormat;
	
	public DimensionFormat(){
		nbrOfDecimal = 2;
		usedSeparator = true;
	}

	public DimensionFormat copy() {
		DimensionFormat copy = new DimensionFormat();
		copy.setDefaultFormat(defaultFormat);
		copy.setNbrOfDecimal(nbrOfDecimal);
		copy.setUsedSeparator(usedSeparator);		
		return copy;
	}

}
