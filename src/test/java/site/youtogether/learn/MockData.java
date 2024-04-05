package site.youtogether.learn;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Document("mockdata")
public class MockData {

	@Id
	private String id;

	@Indexed
	private String title;

	@Indexed
	@Searchable
	private String content;

	private String name;

}
