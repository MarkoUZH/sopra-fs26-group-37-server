package ch.uzh.ifi.hase.soprafs26.rest.dto;
import java.time.LocalDate;

public class SprintPutDTO {
    private String name;
    private String sprintStatus; // Or use your SprintStatus enum type
    private LocalDate startTime;
    private LocalDate endTime;
    private Long projectId;

    // Standard Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSprintStatus() { return sprintStatus; }
    public void setSprintStatus(String sprintStatus) { this.sprintStatus = sprintStatus; }
    public LocalDate getStartTime() { return startTime; }
    public void setStartTime(LocalDate startTime) { this.startTime = startTime; }
    public LocalDate getEndTime() { return endTime; }
    public void setEndTime(LocalDate endTime) { this.endTime = endTime; } 
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }  
    // ... add others for status, startTime, and endTime
}