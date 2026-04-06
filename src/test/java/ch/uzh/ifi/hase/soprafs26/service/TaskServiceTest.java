package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.repository.TaskRepository;
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

	@InjectMocks
	private TaskService taskService;

	private Task testTask;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		// given
        testTask = new Task();
        testTask.setId(1L);
        testTask.setName("testTask");
        testTask.setDescription("testTaskDescription");

		// when -> any object is being save in the userRepository -> return the dummy
		// testUser
		Mockito.when(taskRepository.save(Mockito.any())).thenReturn(testTask);
	}

	@Test
	public void createTask_validInputs_success() {
		// when -> any object is being save in the userRepository -> return the dummy
		Task createdTask = taskService.createTask(testTask);

		// then
		Mockito.verify(taskRepository, Mockito.times(1)).save(Mockito.any());

		assertEquals(testTask.getId(), createdTask.getId());
		assertEquals(testTask.getName(), createdTask.getName());
		assertEquals(testTask.getDescription(), createdTask.getDescription());
	}

}
