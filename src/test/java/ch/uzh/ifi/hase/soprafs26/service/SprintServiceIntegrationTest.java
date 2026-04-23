package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.repository.ProjectRepository;
import ch.uzh.ifi.hase.soprafs26.repository.SprintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest(properties = "HUGGINGFACE_API_TOKEN=mock-key")
public class SprintServiceIntegrationTest {

    @Qualifier("sprintRepository")
    @Autowired
    private SprintRepository sprintRepository;

    @Qualifier("projectRepository")
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private ProjectService projectService;

    private Project persistedProject;

    @BeforeEach
    public void setup() {
        sprintRepository.deleteAll();
        projectRepository.deleteAll();

        // Persist a reusable project for sprint tests
        Project project = new Project();
        project.setName("testProject_" + System.nanoTime());
        project.setDescription("integration test project");
        persistedProject = projectRepository.save(project);
    }

    @Test
    public void createSprint_validProjectId_success() {
        Sprint sprint = new Sprint();
        sprint.setName("sprintOne");

        Sprint created = sprintService.createSprint(sprint, persistedProject.getId());

        assertNotNull(created.getId());
        assertEquals("sprintOne", created.getName());
        assertEquals(persistedProject.getId(), created.getProject().getId());
    }

    @Test
    public void createSprint_invalidProjectId_throwsNotFound() {
        Sprint sprint = new Sprint();
        sprint.setName("orphan");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sprintService.createSprint(sprint, 9999L)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void getSprints_multipleSprints_returnsAll() {
        Sprint s1 = new Sprint();
        s1.setName("sprint1");
        sprintService.createSprint(s1, persistedProject.getId());

        Sprint s2 = new Sprint();
        s2.setName("sprint2");
        sprintService.createSprint(s2, persistedProject.getId());

        List<Sprint> sprints = sprintService.getSprints();

        assertEquals(2, sprints.size());
    }

    @Test
    public void getSprints_emptyRepository_returnsEmptyList() {
        List<Sprint> sprints = sprintService.getSprints();
        assertTrue(sprints.isEmpty());
    }


    @Test
    public void getSprintById_existingId_returnsSprint() {
        Sprint sprint = new Sprint();
        sprint.setName("findMe");
        Sprint saved = sprintService.createSprint(sprint, persistedProject.getId());

        Sprint found = sprintService.getSprintById(saved.getId());

        assertEquals(saved.getId(), found.getId());
        assertEquals("findMe", found.getName());
    }

    @Test
    public void getSprintById_nonExistingId_throwsNotFound() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sprintService.getSprintById(9999L)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void updateSprint_validInput_fieldsAreUpdated() {
        Sprint sprint = new Sprint();
        sprint.setName("original");
        Sprint saved = sprintService.createSprint(sprint, persistedProject.getId());

        Sprint updateInput = new Sprint();
        updateInput.setName("updated");

        Sprint updated = sprintService.updateSprint(saved.getId(), updateInput);

        assertEquals("updated", updated.getName());
    }

    @Test
    public void updateSprint_nullFields_existingValuesPreserved() {
        Sprint sprint = new Sprint();
        sprint.setName("keepMe");
        Sprint saved = sprintService.createSprint(sprint, persistedProject.getId());

        // Update with all-null input — nothing should change
        Sprint updateInput = new Sprint();

        Sprint updated = sprintService.updateSprint(saved.getId(), updateInput);

        assertEquals("keepMe", updated.getName());
    }

    @Test
    public void updateSprint_nonExistingId_throwsNotFound() {
        Sprint updateInput = new Sprint();
        updateInput.setName("ghost");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sprintService.updateSprint(9999L, updateInput)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void deleteSprint_existingSprint_isDeleted() {
        Sprint sprint = new Sprint();
        sprint.setName("toDelete");
        Sprint saved = sprintService.createSprint(sprint, persistedProject.getId());
        Long id = saved.getId();

        sprintService.deleteSprint(id);

        assertFalse(sprintRepository.findById(id).isPresent());
    }

    @Test
    public void deleteSprint_nonExistingId_throwsNotFound() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sprintService.deleteSprint(9999L)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}