package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.constant.TaskStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.TaskRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private User persistedUser;

    @BeforeEach
    public void setup() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        persistedUser = userService.createUser(buildUser());
    }

    // Helper — builds a unique, valid user without persisting
    private User buildUser() {
        User user = new User();
        user.setUsername("user_" + UUID.randomUUID());
        user.setPassword("password");
        user.setName("name_" + UUID.randomUUID());
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setLanguage("DE");
        user.setManager(false);
        return user;
    }

    // Helper — builds a minimal valid task without persisting
    private Task buildTask(String name) {
        Task task = new Task();
        task.setName(name);
        task.setDescription("description");
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(LocalDateTime.of(2026, 12, 31, 23, 59));
        task.setTimeEstimate(2.0f);
        return task;
    }

    @Test
    public void createTask_validToken_success() {
        Task task = buildTask("Test Task");

        Task created = taskService.createTask(task, persistedUser.getToken());

        assertNotNull(created.getId());
        assertEquals("Test Task", created.getName());
        assertEquals("description", created.getDescription());
    }

    @Test
    public void createTask_setsOriginalLanguageFromUser() {
        Task task = buildTask("LangTask");

        Task created = taskService.createTask(task, persistedUser.getToken());

        // User was created with language "DE"
        assertEquals("DE", created.getOriginalLanguage());
    }

    @Test
    public void createTask_persistedInRepository() {
        assertEquals(0, taskRepository.count());

        taskService.createTask(buildTask("SaveTask"), persistedUser.getToken());

        assertEquals(1, taskRepository.count());
    }

    @Test
    public void getTasks_multipleTasks_returnsAll() {
        taskService.createTask(buildTask("t1"), persistedUser.getToken());
        taskService.createTask(buildTask("t2"), persistedUser.getToken());

        List<Task> tasks = taskService.getTasks();

        assertEquals(2, tasks.size());
    }

    @Test
    public void getTasks_emptyRepository_returnsEmptyList() {
        assertTrue(taskService.getTasks().isEmpty());
    }

    @Test
    public void getTaskById_existingId_returnsTask() {
        Task saved = taskService.createTask(buildTask("findTask"), persistedUser.getToken());

        Optional<Task> found = taskService.getTaskById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("findTask", found.get().getName());
    }

    @Test
    public void getTaskById_nonExistingId_returnsEmpty() {
        assertFalse(taskService.getTaskById(9999L).isPresent());
    }

    @Test
    public void deleteTaskById_existingTask_isDeleted() {
        Task saved = taskService.createTask(buildTask("deleteTask"), persistedUser.getToken());
        Long id = saved.getId();

        taskService.deleteTaskById(id);

        assertFalse(taskRepository.findById(id).isPresent());
    }

    @Test
    public void updateTask_validId_fieldsAreUpdated() {
        Task saved = taskService.createTask(buildTask("originalTask"), persistedUser.getToken());

        Task input = new Task();
        input.setName("updated");
        input.setDescription("updatedDesc");
        input.setPriority(Priority.HIGH);
        input.setDueDate(LocalDateTime.of(2026, 6, 15, 10, 0));
        input.setTimeEstimate(5.0f);
        input.setStatus(TaskStatus.IN_PROGRESS);
        input.setAssignedUsers(List.of(persistedUser));

        Task result = taskService.updateTask(saved.getId(), input);

        assertEquals("updated", result.getName());
        assertEquals("updatedDesc", result.getDescription());
        assertEquals(Priority.HIGH, result.getPriority());
        assertEquals(5.0f, result.getTimeEstimate());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals(1, result.getAssignedUsers().size());
    }

    @Test
    public void updateTask_persistsChangesToDatabase() {
        Task saved = taskService.createTask(buildTask("original"), persistedUser.getToken());

        Task input = buildTask("persisted update");
        input.setStatus(TaskStatus.DONE);
        taskService.updateTask(saved.getId(), input);

        Task reloaded = taskRepository.findById(saved.getId()).orElseThrow();
        assertEquals("persisted update", reloaded.getName());
        assertEquals(TaskStatus.DONE, reloaded.getStatus());
    }

    @Test
    public void updateTask_nonExistingId_throwsNotFound() {
        Task input = buildTask("empty");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.updateTask(9999L, input));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void updateTask_clearAssignedUsers_listIsEmpty() {
        Task saved = taskService.createTask(buildTask("withUser"), persistedUser.getToken());

        Task assign = buildTask("withUser");
        assign.setAssignedUsers(List.of(persistedUser));
        taskService.updateTask(saved.getId(), assign);

        Task clear = buildTask("withUser");
        clear.setAssignedUsers(List.of());
        Task result = taskService.updateTask(saved.getId(), clear);

        assertTrue(result.getAssignedUsers().isEmpty());
    }
}