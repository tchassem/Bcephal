/**
 * 
 */
package com.moriset.bcephal.accounting.domain;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.EditorData;


/**
 * 
 * @author MORISET-004
 *
 */
public class PostingEditorData extends EditorData<Posting>{
	
	public List<Account> accounts = new ArrayList<Account>();
	
}
