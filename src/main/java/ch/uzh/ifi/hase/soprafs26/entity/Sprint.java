package ch.uzh.ifi.hase.soprafs26.entity;

import ch.uzh.ifi.hase.soprafs26.constant.SprintStatus;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sprints")
public class Sprint {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private SprintStatus sprintStatus;
    private Date startTime;
    private Date endTime;
    @ManyToOne
    private Project project;
    @OneToMany
    private List<Task> tasks;

    public Sprint() {

    }

    public Sprint(String name, SprintStatus sprintStatus, Date startTime, Date endTime, Project project, List<Task> tasks) {
        this.name = name;
        this.sprintStatus = sprintStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.project = project;
        this.tasks = tasks;
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

    public SprintStatus getSprintStatus() {
        return sprintStatus;
    }

    public void setSprintStatus(SprintStatus sprintStatus) {
        this.sprintStatus = sprintStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
