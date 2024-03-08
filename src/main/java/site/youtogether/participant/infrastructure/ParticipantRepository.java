package site.youtogether.participant.infrastructure;

import org.springframework.data.repository.CrudRepository;

import site.youtogether.participant.Participant;

public interface ParticipantRepository extends CrudRepository<Participant, String> {
}
