package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.repository.SprintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SprintServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private SprintService sprintService;

    private Sprint testSprint;
    private Project testProject;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("testProject");
        testProject.setDescription("testProjectDescription");

        testSprint = new Sprint();
        testSprint.setId(10L);
        testSprint.setName("testSprint");
        testSprint.setProject(testProject);

        when(sprintRepository.save(Mockito.any())).thenReturn(testSprint);
    }

    @Test
    public void createSprint_validProjectId_success() {
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(testProject));

        Sprint created = sprintService.createSprint(testSprint, 1L);

        verify(projectService, times(1)).getProjectById(1L);
        verify(sprintRepository, times(1)).save(testSprint);
        assertEquals(testProject, testSprint.getProject());
        assertEquals(testSprint.getId(), created.getId());
        assertEquals(testSprint.getName(), created.getName());
    }

    @Test
    public void createSprint_invalidProjectId_throwsNotFound() {
        when(projectService.getProjectById(99L)).thenReturn(Optional.empty());

        Sprint newSprint = new Sprint();
        newSprint.setName("bad sprint");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sprintService.createSprint(newSprint, 99L)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(sprintRepository, never()).save(any());
    }

    @Test
    public void getSprints_returnsAllSprints() {
        List<Sprint> sprints = Arrays.asList(testSprint, new Sprint());
        when(sprintRepository.findAll()).thenReturn(sprints);

        List<Sprint> result = sprintService.getSprints();

        assertEquals(2, result.size());
        verify(sprintRepository, times(1)).findAll();
    }

    @Test
    public void getSprints_emptyRepository_returnsEmptyList() {
        when(sprintRepository.findAll()).thenReturn(new ArrayList<>());

        List<Sprint> result = sprintService.getSprints();

        assertTrue(result.isEmpty());
    }

    @Test
    public void getSprintById_existingId_returnsSprint() {
        when(sprintRepository.findById(10L)).thenReturn(Optional.of(testSprint));

        Sprint result = sprintService.getSprintById(10L);

        assertEquals(testSprint.getId(), result.getId());
        assertEquals(testSprint.getName(), result.getName());
    }

    @Test
    public void getSprintById_nonExistingId_throwsNotFound() {
        when(sprintRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sprintService.getSprintById(99L)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void updateSprint_updatesNameOnly() {
        when(sprintRepository.findById(10L)).thenReturn(Optional.of(testSprint));
        when(sprintRepository.save(testSprint)).thenReturn(testSprint);

        Sprint input = new Sprint();
        input.setName("updatedName");

        Sprint result = sprintService.updateSprint(10L, input);

        assertEquals("updatedName", result.getName());
        verify(sprintRepository, times(1)).save(testSprint);
    }

    @Test
    public void updateSprint_updatesProject() {
        when(sprintRepository.findById(10L)).thenReturn(Optional.of(testSprint));
        when(sprintRepository.save(testSprint)).thenReturn(testSprint);

        Project newProject = new Project();
        newProject.setId(2L);
        newProject.setName("newProject");

        Sprint input = new Sprint();
        input.setProject(newProject);

        Sprint result = sprintService.updateSprint(10L, input);

        assertEquals(newProject, result.getProject());
    }

    @Test
    public void updateSprint_nullFields_doesNotOverwriteExistingValues() {
        testSprint.setName("originalName");
        when(sprintRepository.findById(10L)).thenReturn(Optional.of(testSprint));
        when(sprintRepository.save(testSprint)).thenReturn(testSprint);

        Sprint input = new Sprint();

        Sprint result = sprintService.updateSprint(10L, input);

        assertEquals("originalName", result.getName());
    }

    @Test
    public void updateSprint_nonExistingId_throwsNotFound() {
        when(sprintRepository.findById(99L)).thenReturn(Optional.empty());

        Sprint input = new Sprint();
        input.setName("x");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sprintService.updateSprint(99L, input)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(sprintRepository, never()).save(any());
    }

    @Test
    public void deleteSprint_existingSprint_callsRepositoryDelete() {
        when(sprintRepository.findById(10L)).thenReturn(Optional.of(testSprint));
        doNothing().when(sprintRepository).delete(testSprint);

        sprintService.deleteSprint(10L);

        verify(sprintRepository, times(1)).delete(testSprint);
    }

    @Test
    public void deleteSprint_nonExistingId_throwsNotFound() {
        when(sprintRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> sprintService.deleteSprint(99L)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(sprintRepository, never()).delete(any());
    }
}