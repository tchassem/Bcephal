/**
 * 
 */
package com.moriset.bcephal.loader.service;

import lombok.Data;

/**
 * @author MORISET-6
 *
 */
@Data
public class SheetData {

	int index ;
	String name;
	
   public  SheetData(int index_, String name_) {
	   this.index = index_;
	   this.name = name_;
   }
}

