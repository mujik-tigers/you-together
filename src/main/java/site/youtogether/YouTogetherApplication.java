package site.youtogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class YouTogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(YouTogetherApplication.class, args);
	}

}
