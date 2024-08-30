package com.moriset.bcephal.repository;

import java.util.List;

import com.moriset.bcephal.domain.Document;

public interface DocumentRepository extends MainObjectRepository<Document> {

	int countBySubjectTypeAndSubjectId(String functionalityCode, Long subjectId);
	
	List<Document> findBySubjectTypeAndSubjectIdAndName(String subjectType, Long subjectId, String name);
	
	List<Document> findBySubjectTypeAndSubjectId(String subjectType, Long subjectId);
	
}
