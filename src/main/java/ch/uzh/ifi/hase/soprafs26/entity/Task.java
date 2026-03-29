package ch.uzh.ifi.hase.soprafs26.entity;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    private Long id;
    private String name;
    private String description;
    @ManyToMany
    private List<Tag> tags;
    @ManyToMany
    private List<User> assignedUsers;
    private Priority priority;
    private LocalDateTime dueDate;
    private float timeEstimate;
    @ManyToOne
    private Sprint sprint;
    private String acceptanceCriteria;

    public Task(String name, String description, List<Tag> tags, List<User> assignedUsers, Priority priority, LocalDateTime dueDate, float timeEstimate, Sprint sprint, String acceptanceCriteria) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.assignedUsers = assignedUsers;
        this.priority = priority;
        this.dueDate = dueDate;
        this.timeEstimate = timeEstimate;
        this.sprint = sprint;
        this.acceptanceCriteria = acceptanceCriteria;
    }

    public Task() {

    }

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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<User> assignedUsers) {
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

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }
}
