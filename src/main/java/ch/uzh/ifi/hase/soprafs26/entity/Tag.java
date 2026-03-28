package ch.uzh.ifi.hase.soprafs26.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    private Project project;
    @ManyToMany
    private List<Task> tasks;

    public Tag() {
    }

    public Tag(String name, Project project, List<Task> tasks) {
        this.name = name;
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
