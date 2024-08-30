/**
 * 
 */
package com.moriset.bcephal.domain;

import java.io.Serializable;

/**
 * @author Joseph Wambo
 *
 */
public interface IPersistent extends Serializable {

	public abstract Object getId();
}
