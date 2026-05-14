package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.constant.SprintStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.repository.ProjectRepository;
import ch.uzh.ifi.hase.soprafs26.repository.SprintRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectMessage;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.ProjectDTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.service.UserService;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.*;

@Service
@Transactional
public class ProjectService {

	private final Logger log = LoggerFactory.getLogger(ProjectService.class);

	private final ProjectRepository projectRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SprintRepository sprintRepository;

    public ProjectService(@Qualifier("projectRepository") ProjectRepository projectRepository, @Qualifier("userService") UserService UserService, SimpMessagingTemplate messagingTemplate, SprintRepository sprintRepository) {
		this.projectRepository = projectRepository;
        this.userService = UserService;
        this.messagingTemplate = messagingTemplate;
        this.sprintRepository = sprintRepository;
    }

    public List<Project> getProjects()
    {
    	return projectRepository.findAll();
    }

    public Project createProject(Project project, List<Long> memberIds, Long ownerId, String originalLanguage)
        {if (ownerId != null) {
            User owner = userService.getUserById(ownerId);
            project.setOwner(owner);
        }

        // 2. Link the Members
        if (memberIds != null && !memberIds.isEmpty()) {
            List<User> members = new ArrayList<>();
            for (Long id : memberIds) {
                User member = userService.getUserById(id);
                members.add(member);
            }
            project.setMembers(members);
        }
        project.setOriginalLanguage(originalLanguage);

        createInitialSprint(project);

        Project savedProject = projectRepository.save(project);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                broadcast("project_created", ProjectDTOMapper.INSTANCE.convertEntityToProjectGetDTO(savedProject));
            }
        });

        return savedProject;
    }

    public Optional<Project> getProjectById(Long id)
    {
        return projectRepository.findById(id);
    }

    public void deleteProjectById(Long id)
    {
        projectRepository.deleteById(id);
        broadcast("project_deleted", Map.of("id", id));
    }

    public Project updateProject(Long id, Project projectInput, List<Long> memberIds) {
    Project existingProject = projectRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

    // Update basic fields
    existingProject.setName(projectInput.getName());
    existingProject.setDescription(projectInput.getDescription());

    // Update members only if a list was provided (even an empty one if you allow removing everyone)
    if (memberIds != null) {
        List<User> members = new ArrayList<>();
        for (Long mId : memberIds) {
            members.add(userService.getUserById(mId));
        }
        existingProject.setMembers(members);
    }

        Project updatedProject = projectRepository.save(existingProject);

        broadcast("project_updated", ProjectDTOMapper.INSTANCE.convertEntityToProjectGetDTO(updatedProject));

        return updatedProject;
}
    public List<Project> getProjectsByUserId(Long userId) {
        User user = userService.getUserById(userId);
        
        // 1. Get projects where user is the OWNER
        List<Project> allProjects = new ArrayList<>(user.getOwnedProjects());
        
        // 2. Get projects where user is a MEMBER (the ManyToMany side)
        List<Project> memberProjects = user.getProjects();

        if (memberProjects != null) {
            for (Project project : memberProjects) {
                // Only add if it's not already in the list (prevents duplicates if owner is also a member)
                if (!allProjects.contains(project)) {
                    allProjects.add(project);
                }
            }
        }
        
        return allProjects;
    }

    private void broadcast(String type, Object payload) {
        messagingTemplate.convertAndSend("/topic/projects", new ProjectMessage(type, payload));
    }

    private void createInitialSprint(Project project) {
        Sprint backlog = new Sprint();
        backlog.setName("Backlog");
        backlog.setStartTime(Date.from(Instant.now()));
        backlog.setEndTime(Date.from(Instant.now().plusSeconds(31536000)));
        backlog.setSprintStatus(SprintStatus.ACTIVE);
        backlog.setProject(project);

        sprintRepository.save(backlog);
    }
}
