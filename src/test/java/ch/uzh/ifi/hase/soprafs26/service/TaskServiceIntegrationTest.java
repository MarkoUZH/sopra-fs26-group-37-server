package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.ProjectRepository;
import ch.uzh.ifi.hase.soprafs26.repository.TaskRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest(properties = "HUGGINGFACE_API_TOKEN=mock-key")
@Transactional
public class TaskServiceIntegrationTest {

	@Qualifier("taskRepository")
	@Autowired
	private TaskRepository taskRepository;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserService userService;

	@Autowired
	private TaskService taskService;

    @BeforeEach
    public void setup() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

	@Test
	public void createTask_validInputs_success() {
		// given
        assertEquals(0, taskRepository.count());

        User user = userService.createUser(createMockUser());

        Task task = new Task();
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
        user.setToken(UUID.randomUUID().toString());
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setUsername("user_" + UUID.randomUUID());
        user.setPassword("password");
        user.setName("user_" + UUID.randomUUID());
        user.setManager(true);
        user.setLanguage("DE");

        return user;
    }
}
