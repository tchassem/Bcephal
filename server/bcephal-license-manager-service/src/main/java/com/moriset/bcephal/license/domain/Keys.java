package com.moriset.bcephal.license.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.utils.ByteConverter;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Keys {
	
	private String publicKey;
	
	private String privateKey;
	
	
	@JsonIgnore
	public byte[] getPublicKeyAsByte() {
		return ByteConverter.encode(publicKey);
	}
	
	@JsonIgnore
	public byte[] getPrivateKeyAsByte() {
		return ByteConverter.encode(privateKey);
	}
	
	public Keys copy() {
		Keys copy = new Keys();
		copy.setPublicKey(publicKey);
		copy.setPrivateKey(privateKey);
		return copy;
	}
	
}
