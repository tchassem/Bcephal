package com.moriset.bcephal.grid.domain;

public enum VariableIntervalRanking {

	OLDEST_TO_SOONEST,
	SOONEST_TO_OLDEST;

	public boolean isOldestToSoonest() {
		return this == OLDEST_TO_SOONEST;
	}

	public boolean isSoonestToOldest() {
		return this == SOONEST_TO_OLDEST;
	}
	
}
