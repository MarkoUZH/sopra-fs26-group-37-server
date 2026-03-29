package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.ProjectRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectServiceTest {

	@Mock
	private ProjectRepository projectRepository;

	@InjectMocks
	private ProjectService projectService;

	private Project testProject;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		// given
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("testProject");
        testProject.setDescription("testProjectDescription");

		// when -> any object is being save in the userRepository -> return the dummy
		// testUser
		Mockito.when(projectRepository.save(Mockito.any())).thenReturn(testProject);
	}

	@Test
	public void createProject_validInputs_success() {
		// when -> any object is being save in the userRepository -> return the dummy
		Project createdProject = projectService.createProject(testProject);

		// then
		Mockito.verify(projectRepository, Mockito.times(1)).save(Mockito.any());

		assertEquals(testProject.getId(), createdProject.getId());
		assertEquals(testProject.getName(), createdProject.getName());
		assertEquals(testProject.getDescription(), createdProject.getDescription());
	}

}
