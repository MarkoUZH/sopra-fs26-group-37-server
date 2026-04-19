package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.util.List;

public class TagGetDTO {
    private Long id;
    private String name;
    private Long projectId;
    private List<Long> taskIds;

    public TagGetDTO() {}

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

    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }
    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }
}
