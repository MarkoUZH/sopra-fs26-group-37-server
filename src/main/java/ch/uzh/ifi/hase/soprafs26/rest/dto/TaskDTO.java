package ch.uzh.ifi.hase.soprafs26.rest.dto;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.constant.TaskStatus;
import java.util.List;
import java.time.LocalDateTime;

public class TaskDTO {
    private Long id;
    private String name;
    private String description;
    private Priority priority;
    private LocalDateTime dueDate;
    private float timeEstimate;
    private List<UserGetDTO> assignedUsers;
    private TaskStatus status;
    private String originalLanguage;

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

     public TaskStatus getStatus() {
        return status;
    }
    public void setStatus(TaskStatus status) {
        this.status = status;
}

    public String getOriginalLanguage() {
        return originalLanguage;
    }
    public void setOriginalLanguage(String originalLanguage) {}

    public List<UserGetDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<UserGetDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
}
