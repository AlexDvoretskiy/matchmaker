package com.test.matchmaker.rest;


import com.test.matchmaker.pojo.User;
import com.test.matchmaker.service.MatchMakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/matchmaker")
public class UserController {

	private final MatchMakerService matchMakerService;


	@RequestMapping(value = "/users",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public void addUser(@Valid @RequestBody User user) {
		matchMakerService.addToQueue(user);
	}
}
