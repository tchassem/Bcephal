package com.moriset.bcephal.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "Document")
@Table(name = "BCP_DOCUMENT")
@Data
@EqualsAndHashCode(callSuper = false)
public class Document extends MainObject {

	private static final long serialVersionUID = 8438274240290679650L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_seq")
	@SequenceGenerator(name = "document_seq", sequenceName = "document_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String subjectType;
	
	private Long subjectId;
	
	private String subjectName;
	
	private String code;
	
	private String category;
	
	private String extension;
	
	private String username;
	
	private Boolean sendWithInvoice;
	
	public Document(){
		sendWithInvoice = false;
	}
	
	public boolean getSendWithInvoice() {
		if(sendWithInvoice == null) {
			sendWithInvoice = false;
		}
		return sendWithInvoice;
	}

	@Override
	public Persistent copy() {
		return null;
	}
	
	
	
}
