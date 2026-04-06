package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.ProjectRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class ProjectServiceIntegrationTest {

	@Qualifier("projectRepository")
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ProjectService projectService;

	@BeforeEach
	public void setup() {
		projectRepository.deleteAll();
	}

	@Test
	public void createUser_validInputs_success() {
		// given
		assertFalse(projectRepository.findById(1L).isPresent());

		Project testProject = new Project();
        testProject.setName("testName");
        testProject.setDescription("testDescription");

		// when
		Project createdProject = projectService.createProject(testProject);

		// then
		assertEquals(testProject.getId(), createdProject.getId());
		assertEquals(testProject.getName(), createdProject.getName());
		assertEquals(testProject.getDescription(), createdProject.getDescription());
	}
}
