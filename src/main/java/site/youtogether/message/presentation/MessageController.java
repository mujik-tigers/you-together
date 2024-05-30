package site.youtogether.message.presentation;

import static site.youtogether.util.AppConstants.*;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.youtogether.exception.user.ChatMessageSendDeniedException;
import site.youtogether.exception.user.UserNoExistenceException;
import site.youtogether.exception.user.VideoEditDeniedException;
import site.youtogether.message.ChatMessage;
import site.youtogether.message.VideoSyncInfoMessage;
import site.youtogether.message.application.MessageService;
import site.youtogether.playlist.application.PlayingVideoService;
import site.youtogether.user.User;
import site.youtogether.user.infrastructure.UserStorage;
import site.youtogether.util.RandomUtil;

@RestController
@RequiredArgsConstructor
public class MessageController {

	private final UserStorage userStorage;
	private final MessageService messageService;
	private final PlayingVideoService playingVideoService;

	@MessageMapping("/messages/chat")
	public void handleChatMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		Long userId = (Long)headerAccessor.getSessionAttributes().get(USER_ID);
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		if (user.isViewer()) {
			throw new ChatMessageSendDeniedException();
		}

		chatMessage.setChatId(RandomUtil.generateChatId());
		chatMessage.setUserId(user.getId());

		messageService.sendChat(chatMessage);
	}

	@MessageMapping("/messages/video")
	public void handleVideoSyncMessage(VideoSyncInfoMessage videoSyncInfoMessage, SimpMessageHeaderAccessor headerAccessor) {
		Long userId = (Long)headerAccessor.getSessionAttributes().get(USER_ID);
		User user = userStorage.findById(userId)
			.orElseThrow(UserNoExistenceException::new);

		if (user.isNotEditable()) {
			throw new VideoEditDeniedException();
		}

		videoSyncInfoMessage.setRoomCode(user.getCurrentRoomCode());
		playingVideoService.manageVideo(videoSyncInfoMessage);
	}

}
