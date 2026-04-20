package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.service.UserService;


import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.rest.dto.*;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.TaskDTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TaskController {

	private final TaskService taskService;
    private final UserService userService;

	TaskController(TaskService taskService, UserService userService) {
		this.taskService = taskService;
        this.userService = userService;
	}


    @GetMapping("/tasks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TaskGetDTO> getAllTasks(@RequestHeader(value = "Authorization", required = false) String token) {
        List<Task> tasks = taskService.getTasks();
        List<TaskGetDTO> taskGetDTOS = new ArrayList<>();

        userService.verifyToken(token);


        for (Task task : tasks) {
            taskGetDTOS.add(TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(task));
        }
        return taskGetDTOS;
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TaskGetDTO createTask(@RequestBody TaskPostDTO taskPostDTO, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Task taskInput = TaskDTOMapper.INSTANCE.convertTaskPostDTOtoEntity(taskPostDTO);

        Task createdTask = taskService.createTask(taskInput, token);

        return TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(createdTask);
    }

    @GetMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TaskGetDTO getTask(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Optional<Task> task = taskService.getTaskById(id);

        if (task.isPresent()) {
            return TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(task.get());
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Task with id " + id + " does not exist");
        }
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteTask(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Optional<Task> task = taskService.getTaskById(id);

        if (task.isPresent()) {
            taskService.deleteTaskById(id);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Task with id " + id + " does not exist");
        }
    }

    @PutMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TaskGetDTO updateTask(@RequestBody TaskPutDTO taskPutDTO, @PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        userService.verifyToken(token);
        Optional<Task> task = taskService.getTaskById(id);
        Task taskInput = TaskDTOMapper.INSTANCE.convertTaskPutDTOtoEntity(taskPutDTO);

        if (task.isPresent()) {
            return TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(taskService.updateTask(id, taskInput));
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Task with id " + id + " does not exist");
        }
    }
}
