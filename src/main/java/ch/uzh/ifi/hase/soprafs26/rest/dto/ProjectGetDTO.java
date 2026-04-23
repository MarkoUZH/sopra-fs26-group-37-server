package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.util.List;

public class ProjectGetDTO {
    private Long id;
    private String name;
    private String description;
    private UserGetDTO owner;
    private List<UserGetDTO> members;
    private List<SprintGetDTO> sprints;
    private List<TagDTO> tags;
    private List<TaskDTO> tasks;
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

    public UserGetDTO getOwner() {
        return owner;
    }

    public void setOwner(UserGetDTO owner) {
        this.owner = owner;
    }

    public List<UserGetDTO> getMembers() {
        return members;
    }

    public void setMembers(List<UserGetDTO> members) {
        this.members = members;
    }

    public List<SprintGetDTO> getSprints() {
        return sprints;
    }

    public void setSprints(List<SprintGetDTO> sprints) {
        this.sprints = sprints;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

}
