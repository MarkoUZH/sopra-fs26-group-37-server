package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.ProjectRepository;
import ch.uzh.ifi.hase.soprafs26.repository.TaskRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest(properties = "HUGGINGFACE_API_TOKEN=mock-key")
public class TaskServiceIntegrationTest {

	@Qualifier("taskRepository")
	@Autowired
	private TaskRepository taskRepository;

    @Autowired
    UserService userService;

	@Autowired
	private TaskService taskService;

	@BeforeEach
	public void setup() {
		taskRepository.deleteAll();
	}

	@Test
	public void createTask_validInputs_success() {
		// given
		assertFalse(taskRepository.findById(1L).isPresent());

        User user = userService.createUser(createMockUser());

        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        task.setAcceptanceCriteria("Good Code");
        task.setPriority(Priority.MEDIUM);

		// when
		Task createdTask = taskService.createTask(task, user.getToken());

		// then
		assertEquals(task.getId(), createdTask.getId());
		assertEquals(task.getName(), createdTask.getName());
		assertEquals(task.getDescription(), createdTask.getDescription());
	}

    private User createMockUser(){
        User user = new User();
        user.setUsername("loginUser");
        user.setName("TEST USER");
        user.setPassword("TEST PASSWORD");
        user.setManager(true);
        user.setEmail("email@test.com");
        user.setToken("token");
        user.setLanguage("DE");

        return user;
    }
}
