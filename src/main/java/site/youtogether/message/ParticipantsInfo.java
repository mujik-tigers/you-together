package site.youtogether.message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import site.youtogether.user.User;

@Getter
public class ParticipantsInfo {

	private final MessageType messageType = MessageType.PARTICIPANTS_INFO;

	private List<String> nicknames;

	public ParticipantsInfo(Map<String, User> participants) {
		this.nicknames = participants.values()
			.stream()
			.map(User::getNickname)
			.collect(Collectors.toList());
	}

}
