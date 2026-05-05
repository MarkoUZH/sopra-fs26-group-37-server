package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TaskGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TaskMessage;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.TaskDTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.TaskService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TaskWebSocketController {

    private final TaskService taskService;

    public TaskWebSocketController(TaskService taskService) {
        this.taskService = taskService;
    }

    @MessageMapping("/subscribe_tasks")
    @SendToUser("/topic/tasks")
    public TaskMessage onSubscribe() {
        List<Task> tasks = taskService.getTasks();
        System.out.println("WS subscribe_tasks called, found: " + tasks.size() + " tasks");
        List<TaskGetDTO> snapshot = tasks.stream()
                .map(TaskDTOMapper.INSTANCE::convertEntityToTaskGetDTO)
                .toList();
        return new TaskMessage("tasks_snapshot", snapshot);
    }
}