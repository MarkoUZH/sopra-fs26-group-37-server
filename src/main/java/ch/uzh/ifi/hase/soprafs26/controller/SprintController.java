package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.service.UserService;
import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintGetDTO;
import ch.uzh.ifi.hase.soprafs26.service.SprintService;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.SprintDTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintPutDTO;

import java.util.List;

@RestController
public class SprintController {

	private final SprintService sprintService;
    private final UserService userService;


	SprintController(SprintService sprintService, UserService userService) {
		this.sprintService = sprintService;
		this.userService = userService;
	}

	@PostMapping("/sprints")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public SprintGetDTO createSprint(@RequestBody SprintPostDTO sprintPostDTO, 
                                  @RequestHeader(value = "Authorization", required = false) String token) {
        
        // 1. Verify the token using the userService
        userService.verifyToken(token);

        // 2. Convert the incoming DTO (Data Transfer Object) to a Sprint Entity
        // This keeps your API layer separated from your Database layer
        Sprint sprintInput = SprintDTOMapper.INSTANCE.convertSprintPostDTOtoEntity(sprintPostDTO);

        // 3. Call the service to save the sprint 
        // We pass the projectId from the DTO so the service can find the Project entity
        Sprint createdSprint = sprintService.createSprint(sprintInput, sprintPostDTO.getProjectId());

        // 4. Convert the saved Entity back into a DTO to send to the client
        return SprintDTOMapper.INSTANCE.convertEntityToSprintDTO(createdSprint);
    }

	@GetMapping("/sprints")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SprintGetDTO> getAllSprints(@RequestHeader(value = "Authorization", required = false) String token) {
        // 1. Verify token
        userService.verifyToken(token);

        // 2. Get all sprints from service
        List<Sprint> sprints = sprintService.getSprints();
        
        // 3. Map list of entities to list of DTOs
        return SprintDTOMapper.INSTANCE.convertEntitiesToSprintDTOs(sprints);
    }

    @GetMapping("/sprints/{sprintId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SprintGetDTO getSprintById(@PathVariable Long sprintId,
                                     @RequestHeader(value = "Authorization", required = false) String token) {
        // 1. Verify token
        userService.verifyToken(token);

        // 2. Get specific sprint
        Sprint sprint = sprintService.getSprintById(sprintId);

        // 3. Map to DTO
        return SprintDTOMapper.INSTANCE.convertEntityToSprintDTO(sprint);
    }

    @PutMapping("/sprints/{sprintId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SprintGetDTO updateSprint(@PathVariable Long sprintId,
                                    @RequestBody SprintPutDTO sprintPutDTO,
                                    @RequestHeader(value = "Authorization", required = false) String token) {
        // 1. Verify token

        // 2. Convert DTO to Entity
        Sprint sprintInput = SprintDTOMapper.INSTANCE.convertSprintPutDTOtoEntity(sprintPutDTO);
        
        // 3. Update sprint
        Sprint updatedSprint = sprintService.updateSprint(sprintId, sprintInput);

        // 4. Return the result mapped to a GetDTO
        return SprintDTOMapper.INSTANCE.convertEntityToSprintDTO(updatedSprint);
    }

    @DeleteMapping("/sprints/{sprintId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSprint(@PathVariable Long sprintId,
                             @RequestHeader(value = "Authorization", required = false) String token) {
       
        // 2. Call service to delete sprint
        sprintService.deleteSprint(sprintId);
    }


}
