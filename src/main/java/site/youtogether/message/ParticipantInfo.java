package site.youtogether.message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import site.youtogether.user.User;

@Getter
public class ParticipantInfo {

	private final MessageType messageType = MessageType.PARTICIPANT_INFO;

	private List<String> participantNames;

	public ParticipantInfo(Map<String, User> participants) {
		this.participantNames = participants.values()
			.stream()
			.map(User::getNickname)
			.collect(Collectors.toList());
	}

}
