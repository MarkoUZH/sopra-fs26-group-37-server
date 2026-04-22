package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.TaskRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    // Falls TaskService den UserService aufruft, mocken wir den UserService:
    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService; // Das ist die Unit, die wir testen

    private Task testTask;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setName("testTask");

        User user = createMockUser();

        // Wenn TaskService das UserRepository direkt nutzt:
        //Mockito.when(userRepository.findByToken("token")).thenReturn(user);

        // ODER: Wenn TaskService den userService.getUserByToken() nutzt:
        Mockito.when(userService.getUserByToken("token")).thenReturn(user);

        Mockito.when(taskRepository.save(Mockito.any())).thenReturn(testTask);
    }

	@Test
	public void createTask_validInputs_success() {
		// when -> any object is being save in the userRepository -> return the dummy
		Task createdTask = taskService.createTask(testTask, "token");

		// then

		Mockito.verify(taskRepository, Mockito.times(1)).save(Mockito.any());

		assertEquals(testTask.getId(), createdTask.getId());
		assertEquals(testTask.getName(), createdTask.getName());
		assertEquals(testTask.getDescription(), createdTask.getDescription());
	}

    private User createMockUser(){
        User user = new User();
        user.setId(1L);
        user.setUsername("loginUser");
        user.setPassword("password");
        user.setEmail("email");
        user.setToken("token");
        user.setLanguage("DE");
        user.setManager(true);

        return user;
    }

}
