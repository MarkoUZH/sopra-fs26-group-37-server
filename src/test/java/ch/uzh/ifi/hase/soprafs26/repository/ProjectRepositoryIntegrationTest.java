package ch.uzh.ifi.hase.soprafs26.repository;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ProjectRepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ProjectRepository projectRepository;

	@Test
	public void findById_success() {
		// given
		Project project = new Project();
        project.setName("Test 1");
        project.setDescription("Test Description");

		entityManager.persist(project);
		entityManager.flush();

		// when
		Optional<Project> found = projectRepository.findById(1L);

		// then
		assertNotNull(found.get());
		assertEquals(found.get().getId(), project.getId());
		assertEquals(found.get().getName(), project.getName());
		assertEquals(found.get().getDescription(), project.getDescription());
	}
}
