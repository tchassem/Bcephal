/**
 * 
 */
package com.moriset.bcephal.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.moriset.bcephal.chat.domain.ChatItemUser;
import com.moriset.bcephal.repository.PersistentRepository;


public interface ChatItemUserRepository extends PersistentRepository<ChatItemUser> {

	List<ChatItemUser> findAllByChatIdAndChannelId(Long chatId, int channelId);

	ChatItemUser findByChatIdAndReceiverIdAndItemReceiverIdAndChannelId(Long chatId, Long receiverId, Long itemReceiverId, Long channelId);

	@Query(value = "UPDATE ChatItemUser ciu " +
			"SET ciu.lastItemId = ?5 " +
			"WHERE ciu.chatId = ?1 AND ciu.receiverId = ?2 AND ciu.itemReceiverId = ?3 AND ciu.channelId = ?4 AND ciu.lastItemId IS null")
	void updateItemsUser(Long chatId, Long receiverId, Long itemReceiverId, Long channelId, Long chatItemId);

}
