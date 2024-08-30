package com.moriset.bcephal.etl.domain;

public enum EmailAttributeFilter {SEND_DATE,
	EXPEDITOR, SUBJECT, ATTACHMENT, 
	EXPEDITOR_SUBJECT,
	EXPEDITOR_ATTACHMENT, 
	SUBJECT_ATTACHMENT,
	EXPEDITOR_SUBJECT_ATTACHMENT;

	public boolean isSendDate() {
		return this == SEND_DATE;
	}
	
	public boolean isExpeditor() {
		return this == EXPEDITOR;
	}

	public boolean isSubject() {
		return this == SUBJECT;
	}

	public boolean isAttachment() {
		return this == ATTACHMENT;
	}

	public boolean isExpeditorAndSubject() {
		return this == EXPEDITOR_SUBJECT;
	}

	public boolean isExpeditorAndAttachment() {
		return this == EXPEDITOR_ATTACHMENT;
	}

	public boolean isSubjectAndAttachment() {
		return this == SUBJECT_ATTACHMENT;
	}

	public boolean isExpeditorAndSubjectAndAttachment() {
		return this == EXPEDITOR_SUBJECT_ATTACHMENT;
	}
}
