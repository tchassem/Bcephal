package com.moriset.bcephal.chat.repository;

import com.moriset.bcephal.chat.domain.Chat;
import com.moriset.bcephal.repository.MainObjectRepository;

public interface ChatRepository extends MainObjectRepository<Chat> {

	Chat findBySubjectTypeAndSubjectId(String subjectType, Long subjectId);
}
