package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectMessage;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.ProjectDTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.ProjectService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProjectWebSocketController {

    private final ProjectService projectService;

    public ProjectWebSocketController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @MessageMapping("/subscribe_projects")
    @SendToUser("/topic/projects")
    public ProjectMessage onSubscribe() {
        List<ProjectGetDTO> snapshot = projectService.getProjects().stream()
                .map(ProjectDTOMapper.INSTANCE::convertEntityToProjectGetDTO)
                .toList();
        return new ProjectMessage("projects_snapshot", snapshot);
    }
}