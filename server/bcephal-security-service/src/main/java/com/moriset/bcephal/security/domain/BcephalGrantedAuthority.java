/**
 * 
 */
package com.moriset.bcephal.security.domain;

//import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import lombok.Data;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Data
@ToString
public class BcephalGrantedAuthority /*implements GrantedAuthority */ {
	
	private Right right;

	public BcephalGrantedAuthority(Right right) {
		Assert.notNull(right, "A granted authority right representation is required");
		this.right = right;
	}
	
//	@Override
//	public String getAuthority() {
//		return right.getRole();
//	}

}
