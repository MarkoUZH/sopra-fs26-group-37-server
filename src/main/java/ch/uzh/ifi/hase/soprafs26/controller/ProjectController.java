package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.rest.dto.*;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.TaskDTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.UserService;


import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.ProjectDTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ProjectController {

	private final ProjectService projectService;
    private final UserService userService;

	ProjectController(ProjectService projectService, UserService userService) {
		this.projectService = projectService;
        this.userService = userService;
	}

	@GetMapping("/projects")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<ProjectGetDTO> getAllProjects(@RequestHeader(value = "Authorization", required = false) String token) {
		List<Project> projects = projectService.getProjects();
		List<ProjectGetDTO> projectGetDTOS = new ArrayList<>();

        userService.verifyToken(token);


		for (Project project : projects) {
            projectGetDTOS.add(ProjectDTOMapper.INSTANCE.convertEntityToProjectGetDTO(project));
		}
		return projectGetDTOS;
	}

    @GetMapping("/projects/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ProjectGetDTO> getProjectsByUserId(@PathVariable Long userId, @RequestHeader(value = "Authorization", required = false) String token) {
        
        userService.verifyToken(token);

        
        List<Project> projects = projectService.getProjectsByUserId(userId);
        List<ProjectGetDTO> projectGetDTOS = new ArrayList<>();

        for (Project project : projects) {
            projectGetDTOS.add(ProjectDTOMapper.INSTANCE.convertEntityToProjectGetDTO(project));
        }
        return projectGetDTOS;
    }



    @PostMapping("/projects")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ProjectGetDTO createProject(@RequestBody ProjectPostDTO projectPostDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Project projectInput = ProjectDTOMapper.INSTANCE.convertProjectPostDTOtoEntity(projectPostDTO);
        List<Long> memberIds = projectPostDTO.getMemberIds();
            Long ownerId = projectPostDTO.getOwnerId();
        Project createdProject = projectService.createProject(projectInput, memberIds, ownerId);

        return ProjectDTOMapper.INSTANCE.convertEntityToProjectGetDTO(createdProject);
    }

    @GetMapping("/projects/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ProjectGetDTO getProject(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Optional<Project> project = projectService.getProjectById(id);

        if (project.isPresent()) {
            return ProjectDTOMapper.INSTANCE.convertEntityToProjectGetDTO(project.get());
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Project with id " + id + " does not exist");
        }
    }

    @DeleteMapping("/projects/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteProject(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Optional<Project> project = projectService.getProjectById(id);

        if (project.isPresent()) {
            projectService.deleteProjectById(id);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Project with id " + id + " does not exist");
        }
    }

    @PutMapping("/projects/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ProjectGetDTO updateProject(@RequestBody ProjectPostDTO projectPostDTO, @PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Optional<Project> project = projectService.getProjectById(id);
        Project projectInput = ProjectDTOMapper.INSTANCE.convertProjectPostDTOtoEntity(projectPostDTO);

        if (project.isPresent()) {
            return ProjectDTOMapper.INSTANCE.convertEntityToProjectGetDTO(projectService.updateProject(id, projectInput));
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Project with id " + id + " does not exist");
        }
    }

    @GetMapping("/projects/{id}/tasks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TaskGetDTO> getTasksByProject(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Optional<Project> project = projectService.getProjectById(id);
        List<TaskGetDTO> taskGetDTOS = new ArrayList<>();

        if (project.isPresent()) {
            List<Task> tasks = project.get().getTasks();
            for (Task task : tasks) {
                taskGetDTOS.add(TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(task));
            }
            return taskGetDTOS;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Project with id " + id + " does not exist");
        }
    }

    @GetMapping("/projects/{id}/sprints")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SprintDTO> getSprintsByProject(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Optional<Project> project = projectService.getProjectById(id);
        List<SprintDTO> sprintDTOS = new ArrayList<>();

        if (project.isPresent()) {
            List<Sprint> sprints = project.get().getSprints();
            for (Sprint sprint : sprints) {
                // TODO add mapping
                //.add(TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(task));
            }
            return sprintDTOS;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Project with id " + id + " does not exist");
        }
    }

    @GetMapping("/projects/{id}/tags")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TagDTO> getTagsByProject(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Optional<Project> project = projectService.getProjectById(id);
        List<TagDTO> tagDTOS = new ArrayList<>();

        if (project.isPresent()) {
            List<Tag> tags = project.get().getTags();
            for (Tag tag : tags) {
                // TODO add mapping
                //.add(TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(task));
            }
            return tagDTOS;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Project with id " + id + " does not exist");
        }
    }
}
