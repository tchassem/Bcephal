package com.moriset.bcephal.domain;

public enum FilterItemType
{

		FREE, VARIABLE;

		public boolean isFree() {
			return this == FREE;
		}

		public boolean isVariable() {
			return this == VARIABLE;
		}


}
