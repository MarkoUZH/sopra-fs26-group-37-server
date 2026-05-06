package ch.uzh.ifi.hase.soprafs26.entity;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.constant.TaskStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Task {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
    @ManyToMany(mappedBy = "tasks", fetch = FetchType.EAGER)
    private List<Tag> tags;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "task_assigned_users",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> assignedUsers = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private LocalDateTime dueDate;
    private float timeEstimate;
    private String originalLanguage;
    @ManyToOne(fetch = FetchType.EAGER)
    private Sprint sprint;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    public Task(String name, String description, List<Tag> tags, List<User> assignedUsers, Priority priority, LocalDateTime dueDate, float timeEstimate, String originalLanguage, Project project, TaskStatus status, Sprint sprint) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.assignedUsers = assignedUsers != null ? new ArrayList<>(assignedUsers) : new ArrayList<>();
        this.priority = priority;
        this.dueDate = dueDate;
        this.timeEstimate = timeEstimate;
        this.originalLanguage = originalLanguage;
        this.project = project;
        this.status = status;
        this.sprint = sprint;
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
    if (this.assignedUsers == null) {
        this.assignedUsers = new ArrayList<>();
    }
    this.assignedUsers.clear();
    if (assignedUsers != null) {
        this.assignedUsers.addAll(assignedUsers);
    }
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }
    public Sprint getSprint() {
    return sprint;
}

public void setSprint(Sprint sprint) {
    this.sprint = sprint;
}
}
