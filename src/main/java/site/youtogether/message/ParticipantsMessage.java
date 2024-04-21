package site.youtogether.message;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.youtogether.room.Participant;

@RequiredArgsConstructor
@Getter
public class ParticipantsMessage {

	private final MessageType messageType = MessageType.PARTICIPANTS;

	private final List<Participant> participants;

}
