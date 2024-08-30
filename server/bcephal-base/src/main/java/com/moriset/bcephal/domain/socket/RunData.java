package com.moriset.bcephal.domain.socket;

import lombok.Data;

@Data
public class RunData {
	private boolean start;
	private boolean end;
	private Object data;
}
