package com.test.matchmaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class MatchmakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchmakerApplication.class, args);
	}

}
