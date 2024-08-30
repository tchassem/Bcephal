package com.moriset.bcephal.chat.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.moriset.bcephal.chat.domain.ChatBrowserData;
import com.moriset.bcephal.chat.domain.ChatItem;
import com.moriset.bcephal.repository.PersistentRepository;

public interface ChatItemRepository extends PersistentRepository<ChatItem> {
	
	@Query(value = "SELECT * FROM BCP_CHAT_ITEM ci WHERE ci.chatid = ?1 ORDER BY ci.channelid DESC LIMIT 10", nativeQuery = true)
	List<ChatItem> findByChatIdAndChannelDesc(Long chatId);

	@Query(value = "SELECT * FROM BCP_CHAT_ITEM ci WHERE ci.chatid = ?1 AND ci.senderid = ?2 AND ci.receiverid = ?3 AND ci.receivertype = ?4 ORDER BY ci.id DESC LIMIT 10", nativeQuery = true)
	List<ChatItem> findByChatIdAndSenderIdAndReceiverIdAndReceiverType(Long chatId, Long senderid, Long receiverId, String receiverType);
	
	@Query(value = "SELECT * FROM BCP_CHAT_ITEM ci WHERE ci.chatid = ?1 AND ci.receiverid = ?2 AND ci.receivertype = ?3 AND ci.channelid = ?4 ORDER BY ci.id DESC LIMIT 10", nativeQuery = true)
	List<ChatItem> findByChatIdAndReceiverIdAndReceiverTypeAndChannelId(Long chatId, Long receiverId, String receiverType, Long channelId);
	
	@Query(value = "SELECT ci " +
	"FROM ChatItem ci " +
	"LEFT JOIN ChatItemUser ciu ON " +
	"ci.chatId = ciu.chatId AND (ci.id > ciu.lastItemId OR ciu.lastItemId IS null) " +
	"WHERE ciu.itemReceiverId = ?1 AND ci.receiverId = ciu.receiverId AND ci.channelId = ciu.channelId")
	List<ChatItem> findUserDashboardMsgNotifs(Long userId);
	
	@Query(value = "SELECT new com.moriset.bcephal.chat.domain.ChatBrowserData(ci.id, c.subjectId, c.subjectType, ci.channelId, c.subjectName, ci.senderName, c.name, ci.receiverId, ci.receiverType) " +
	"FROM ChatItemUser ciu " +
	"INNER JOIN Chat c ON " +
	"c.id = ciu.chatId " +
	"LEFT JOIN ChatItem ci ON " +
	"ci.chatId = ciu.chatId AND (ci.id > ciu.lastItemId OR ciu.lastItemId IS null) " +
	"WHERE ciu.itemReceiverId = ?1 AND ci.receiverId = ciu.receiverId AND ci.channelId = ciu.channelId")
	Page<ChatBrowserData> searchNotificationsItems(Long userId, Pageable pageable);
}
