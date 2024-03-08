package site.youtogether.participant;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RedisHash("participant")
@NoArgsConstructor
@Getter
public class Participant {

	@Id
	private String ip;

	private User user;

	@Builder
	private Participant(String ip, User user) {
		this.ip = ip;
		this.user = user;
	}

}
