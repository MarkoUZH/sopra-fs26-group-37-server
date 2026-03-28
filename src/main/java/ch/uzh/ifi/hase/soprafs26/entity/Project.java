package ch.uzh.ifi.hase.soprafs26.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;
    @OneToMany
    private List<Task> tasks;
    @ManyToOne
    private User owner;
    @ManyToMany
    private List<User> members;
    @OneToMany
    private List<Sprint> sprints;
    @OneToMany
    private List<Tag> tags;

    public Project() {

    }

    public Project(String name, String description, List<Task> tasks, User owner, List<User> members, List<Sprint> sprints, List<Tag> tags) {
        this.name = name;
        this.description = description;
        this.tasks = tasks;
        this.owner = owner;
        this.members = members;
        this.sprints = sprints;
        this.tags = tags;
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

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
