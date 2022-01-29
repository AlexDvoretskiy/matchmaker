package com.test.matchmaker.pojo;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


@Data
public class User {

	@Size(min = 2)
	private String name;
	@Min(value = 0)
	private double skill;
	@Min(value = 0)
	private double latency;

	private LocalDateTime requestDateTime;

	public User() {
		requestDateTime = LocalDateTime.now();
	}

}
