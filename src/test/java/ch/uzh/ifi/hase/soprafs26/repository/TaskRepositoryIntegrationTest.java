package ch.uzh.ifi.hase.soprafs26.repository;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class TaskRepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private TaskRepository taskRepository;

	@Test
	public void findById_success() {
		// given
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        task.setAcceptanceCriteria("Good Code");
        task.setPriority(Priority.MEDIUM);

		entityManager.persist(task);
		entityManager.flush();

		// when
		Optional<Task> found = taskRepository.findById(1L);

		// then
		assertNotNull(found.get());
		assertEquals(found.get().getId(), task.getId());
		assertEquals(found.get().getName(), task.getName());
		assertEquals(found.get().getDescription(), task.getDescription());
	}
}
