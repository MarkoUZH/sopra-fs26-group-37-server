package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.repository.SprintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@Service
@Transactional
public class SprintService {

    private final SprintRepository sprintRepository;
    
   
    private final ProjectService projectService;

    
    public SprintService(@Qualifier("sprintRepository") SprintRepository sprintRepository, 
                         ProjectService projectService) {
        this.sprintRepository = sprintRepository;
        this.projectService = projectService;
    }

    public Sprint createSprint(Sprint newSprint, Long projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        newSprint.setProject(project);
        return sprintRepository.save(newSprint);
    }

	public List<Sprint> getSprints() {
        return this.sprintRepository.findAll();
    }

    public Sprint getSprintById(Long id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Sprint with ID " + id + " was not found"));
    }

public Sprint updateSprint(Long sprintId, Sprint updatedSprint) {
    Sprint existingSprint = getSprintById(sprintId);

    // Only update fields if they are provided (Null-safe update)
    if (updatedSprint.getName() != null) {
        existingSprint.setName(updatedSprint.getName());
    }
    if (updatedSprint.getSprintStatus() != null) {
        existingSprint.setSprintStatus(updatedSprint.getSprintStatus());
    }
    if (updatedSprint.getStartTime() != null) {
        existingSprint.setStartTime(updatedSprint.getStartTime());
    }
    if (updatedSprint.getEndTime() != null) {
        existingSprint.setEndTime(updatedSprint.getEndTime());
    }

    if (updatedSprint.getProject() != null) {
        existingSprint.setProject(updatedSprint.getProject());
    }

    // Save and return
    return sprintRepository.save(existingSprint);
}

public void deleteSprint(Long sprintId) {
    Sprint existingSprint = getSprintById(sprintId);
    existingSprint.setProject(null);
    existingSprint.getTasks().forEach(task -> task.setSprint(null));
    sprintRepository.delete(existingSprint);
}


}