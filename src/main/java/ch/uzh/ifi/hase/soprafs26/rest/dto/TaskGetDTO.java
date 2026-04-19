package ch.uzh.ifi.hase.soprafs26.rest.dto;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.constant.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public class TaskGetDTO {
    private Long id;
    private String name;
    private String description;
    private List<TagDTO> tags;
    private List<UserGetDTO> assignedUsers;
    private Priority priority;
    private LocalDateTime dueDate;
    private float timeEstimate;
    private SprintGetDTO sprint;
    private ProjectDTO project;
    private String acceptanceCriteria;
    private TaskStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public List<UserGetDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<UserGetDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public float getTimeEstimate() {
        return timeEstimate;
    }

    public void setTimeEstimate(float timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    public SprintGetDTO getSprint() {
        return sprint;
    }

    public void setSprint(SprintGetDTO sprint) {
        this.sprint = sprint;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }

    public TaskStatus getStatus() {
        return status;
    }
    public void setStatus(TaskStatus status) {
        this.status = status;
}
}