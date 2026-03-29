package ch.uzh.ifi.hase.soprafs26.controller;


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

	SprintController(SprintService sprintService) {
		this.sprintService = sprintService;
	}

	/*@GetMapping("/sprints")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<SprintDTO> getAllSprints() {
		return null;
	}*/
}
