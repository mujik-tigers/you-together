package site.youtogether.message;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.youtogether.user.dto.UserInfo;

@RequiredArgsConstructor
@Getter
public class ParticipantsInfoMessage {

	private final MessageType messageType = MessageType.PARTICIPANTS_INFO;

	private final List<UserInfo> participants;

}
