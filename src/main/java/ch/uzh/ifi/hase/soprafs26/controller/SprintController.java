package ch.uzh.ifi.hase.soprafs26.controller;


import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SprintController {

	private final SprintController sprintController;

	SprintController(SprintController sprintController) {
		this.sprintController = sprintController;
	}

	@GetMapping("/sprints")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<Sprint> getAllSprints() {
		return null;
	}
}
