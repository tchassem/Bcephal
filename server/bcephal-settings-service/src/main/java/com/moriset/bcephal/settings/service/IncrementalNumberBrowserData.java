package com.moriset.bcephal.settings.service;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class IncrementalNumberBrowserData extends BrowserData {

	private long initialValue;

	/**
	 * Value by which sequence will increment itself. Increment value can be
	 * positive or negative.
	 */
	private long incrementValue;

	/**
	 * Minimum value of the sequence.
	 */
	private long minimumValue;

	/**
	 * Maximum value of the sequence.
	 */
	private long maximumValue;

	/**
	 * Current value of the sequence.
	 */
	private long currentValue;

	/**
	 * If TRUE : When sequence reaches its set_limit it starts from beginning. IF
	 * FALSE : An exception will be thrown if sequence exceeds its max_value.
	 */
	private boolean cycle;

	/**
	 * Sequence size.
	 */
	private int size;

	/**
	 * Sequence prefix.
	 */
	private String prefix;

	/**
	 * Sequence suffix.
	 */
	private String suffix;
	
	public IncrementalNumberBrowserData(IncrementalNumber incrementalNumber ){
		super(incrementalNumber.getId(), incrementalNumber.getName(),
				incrementalNumber.getCreationDate(), incrementalNumber.getModificationDate());
		setCycle(incrementalNumber.isCycle());
		setIncrementValue(incrementalNumber.getIncrementValue());
		setInitialValue(incrementalNumber.getInitialValue());
		setMaximumValue(incrementalNumber.getMaximumValue());
		setMinimumValue(incrementalNumber.getMinimumValue());
		setSize(incrementalNumber.getSize());
		setPrefix(incrementalNumber.getPrefix());
		setSuffix(incrementalNumber.getSuffix());
		setCurrentValue(incrementalNumber.getCurrentValue());
		setVisibleInShortcut(incrementalNumber.isVisibleInShortcut());
	}
}
