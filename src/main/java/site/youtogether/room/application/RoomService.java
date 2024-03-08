package site.youtogether.room.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.youtogether.participant.Participant;
import site.youtogether.participant.Role;
import site.youtogether.participant.User;
import site.youtogether.participant.infrastructure.ParticipantRepository;
import site.youtogether.room.Room;
import site.youtogether.room.infrastructure.RoomRepository;

@Service
@RequiredArgsConstructor
public class RoomService {

	private final RoomRepository roomRepository;
	private final ParticipantRepository participantRepository;

	public String createRoom(String roomName, String roomPassword, int totalCapacity, String creatorIp) {
		if (participantRepository.existsById(creatorIp)) {
			throw new RuntimeException("유저 있음 수고요");
		}

		User creator = User.builder()
			.userIp(creatorIp)
			.userRole(Role.CREATOR)
			.build();

		Room room = Room.builder()
			.name(roomName)
			.password(roomPassword)
			.totalCapacity(totalCapacity)
			.creator(creator)
			.build();
		roomRepository.save(room);

		Participant participant = Participant.builder()
			.ip(creatorIp)
			.user(creator)
			.build();
		participantRepository.save(participant);

		return room.getId();
	}

}
