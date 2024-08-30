/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.moriset.bcephal.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 
 * PersistentListChangeHandler.
 * 
 * The class tracks changes such as update, insert, delete of items of a pist of
 * persistent objects.
 *
 * Objects in the list will be compared based on their db id or based on object
 * equality
 *
 * @author B-Cephal Team
 * @date 14 mars 2014
 *
 * @param <P>
 */
@SuppressWarnings("serial")
@Data
public class ListChangeHandler<P extends IPersistent> implements Serializable {

	/** List to manage */
	private List<P> originalList;

	/** List of new items */
	private List<P> newItems;

	/** List of deleted items */
	private List<P> deletedItems;

	/** List of updated items */
	private List<P> updatedItems;

	/**
	 * Initialize with an original list
	 *
	 * @param list original list
	 */
	public ListChangeHandler(List<P> list) {
		originalList = list;
		reset();
	}

	/**
	 * Initialize with no original list
	 *
	 */
	public ListChangeHandler() {
		originalList = new ArrayList<P>();
		reset();
	}

	/**
	 * Set originalList to 'list tand then empty new, update and deleted lists
	 * 
	 * @param list the originalList to set
	 */
	public void setOriginalList(List<P> list) {
		this.originalList = list;
		reset();
	}

	/**
	 * List of items that are currenlty in the list it is equal to: original -
	 * deleted + newItems
	 */
	@JsonIgnore
	public List<P> getItems() {
		ArrayList<P> result = new ArrayList<P>();

		if (getOriginalList() != null) {
			for (P item : getOriginalList()) {
				boolean found = false;
				for (P deleted : getDeletedItems()) {
					if (sameItems(item, deleted)) {
						found = true;
						break;
					}
				}
				if (!found) {
					result.add(item);
				}
			}
		}
		result.addAll(getNewItems());
		return result;
	}

	/**
	 * Add an item to the list of deleted items
	 *
	 * @param deleted Deleted item
	 *
	 * @pre deleted must be in getItems
	 */
	@JsonIgnore
	public void addDeleted(P deleted) {
		// if item is new we just remove it from the list of new and there is no
		// need to keep it in the list of deleted
		boolean found = false;
		for (P item : getNewItems()) {
			if (sameItems(item, deleted)) {
				found = true;
				break;
			}
		}
		if (!found) {
			deletedItems.add(deleted);
			updatedItems.remove(deleted);
		} else {
			deletedItems.add(deleted);
			newItems.remove(deleted);
		}
	}

	/**
	 * Add an item to the list of new items
	 *
	 * @param newItem New item
	 *
	 * @pre newItem must be in getItems
	 */
	@JsonIgnore
	public void addNew(P newItem) {
		newItems.add(newItem);
	}

	/**
	 * Add an item to the list of updated items
	 *
	 * @param updated Updated item
	 *
	 * @pre updated must be in getItems
	 */
	@JsonIgnore
	public void addUpdated(P updated) {
		// if item is new we just do nothing as it will any how be persisted
		// with the list of new items
		boolean found = false;
		for (P item : getNewItems()) {
			if (sameItems(item, updated)) {
				found = true;
				break;
			}
		}
		// if item is was alway updated we just do nothing
		for (P item : getUpdatedItems()) {
			if (sameItems(item, updated)) {
				found = true;
				break;
			}
		}
		if (!found) {
			updatedItems.add(updated);
		}
	}

	/**
	 * Remove item for list of new, deleted, or updated
	 * 
	 * @param item item to forget
	 */
	@JsonIgnore
	public void forget(P item) {
		boolean found = getNewItems().remove(item);
		if (!found) {
			found = getDeletedItems().remove(item);
		}
		if (!found) {
			found = getUpdatedItems().remove(item);
		}
		if (!found) {
			found = getOriginalList().remove(item);
		}
	}

	/**
	 * Do item and other have the same id? or do other and item refer to the same
	 * object?
	 * 
	 * @param item
	 * @param other
	 * @return True if both ids are non null and are equal
	 */
	@JsonIgnore
	private boolean sameItems(P item, P other) {
		boolean result = false;
		if (item != null) {
			if (other != null) {
				if (item == other) {
					result = true;
				} else {
					if (item.getId() != null && item.getId().equals(other.getId())) {
						result = true;
					} else {
						result = false;
					}
				}
			}
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * Set all internal lists to empty lists
	 */
	@JsonIgnore
	private void reset() {
		newItems = new ArrayList<P>();
		deletedItems = new ArrayList<P>();
		updatedItems = new ArrayList<P>();
	}

}
