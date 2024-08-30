package com.moriset.bcephal.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BLabels implements IPersistent {

	private static final long serialVersionUID = 5924918272646808886L;

	private String lang;
    
	private ListChangeHandler<BLabel> labelItemListChangeHandler;

	@Override
	public Object getId() {
		return null;
	}	
	
	public BLabels(){
		this.labelItemListChangeHandler = new ListChangeHandler<BLabel>();
	}

}
