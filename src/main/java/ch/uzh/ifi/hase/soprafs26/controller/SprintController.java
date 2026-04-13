package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.service.UserService;


import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintDTO;
import ch.uzh.ifi.hase.soprafs26.service.SprintService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SprintController {

	private final SprintService sprintService;
    private final UserService userService;


	SprintController(SprintService sprintService, UserService userService) {
		this.sprintService = sprintService;
		this.userService = userService;
	}

	//IMPORTANT: add userService.verifyToken(token); and @RequestHeader(value = "Authorization", required = false) String token once implemented

	/*@GetMapping("/sprints")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<SprintDTO> getAllSprints() {
		return null;
	}*/
}
