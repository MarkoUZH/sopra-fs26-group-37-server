package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.constant.TaskStatus;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setToken("test-token");
        testUser.setLanguage("DE");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setName("testTask");
        testTask.setDescription("testDescription");
        testTask.setPriority(Priority.MEDIUM);

        when(userService.getUserByToken("test-token")).thenReturn(testUser);
        when(taskRepository.save(Mockito.any())).thenReturn(testTask);
    }

    @Test
    public void getTasks_returnsAllTasks() {
        List<Task> tasks = Arrays.asList(testTask, new Task());
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getTasks();

        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void getTasks_emptyRepository_returnsEmptyList() {
        when(taskRepository.findAll()).thenReturn(new ArrayList<>());

        assertTrue(taskService.getTasks().isEmpty());
    }

    @Test
    public void createTask_validToken_setsLanguageAndSaves() {
        Task created = taskService.createTask(testTask, "test-token");

        verify(userService, times(1)).getUserByToken("test-token");
        verify(taskRepository, times(1)).save(testTask);
        // originalLanguage is set from the user's language
        assertEquals("DE", testTask.getOriginalLanguage());
        assertEquals(testTask.getId(), created.getId());
        assertEquals(testTask.getName(), created.getName());
    }

    @Test
    public void createTask_userHasDifferentLanguage_languageIsCorrectlySet() {
        testUser.setLanguage("FR");
        when(userService.getUserByToken("test-token")).thenReturn(testUser);

        taskService.createTask(testTask, "test-token");

        assertEquals("FR", testTask.getOriginalLanguage());
    }

    @Test
    public void getTaskById_existingId_returnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        Optional<Task> result = taskService.getTaskById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTask.getId(), result.get().getId());
    }

    @Test
    public void getTaskById_nonExistingId_returnsEmpty() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(taskService.getTaskById(99L).isPresent());
    }

    @Test
    public void deleteTaskById_callsRepositoryDeleteById() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTaskById(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    public void updateTask_validId_updatesAllFieldsAndSaves() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(testTask)).thenReturn(testTask);

        Task input = new Task();
        input.setName("updatedName");
        input.setDescription("updatedDesc");
        input.setPriority(Priority.HIGH);
        input.setDueDate(LocalDateTime.of(2026, 6, 1, 12, 0));
        input.setTimeEstimate(3.5f);
        input.setStatus(TaskStatus.IN_PROGRESS);
        input.setAssignedUsers(List.of(testUser));

        Task result = taskService.updateTask(1L, input);

        assertEquals("updatedName", result.getName());
        assertEquals("updatedDesc", result.getDescription());
        assertEquals(Priority.HIGH, result.getPriority());
        assertEquals(LocalDateTime.of(2026, 6, 1, 12, 0), result.getDueDate());
        assertEquals(3.5f, result.getTimeEstimate());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals(1, result.getAssignedUsers().size());
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    public void updateTask_nonExistingId_throwsNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Task input = new Task();
        input.setName("task");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.updateTask(99L, input));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(taskRepository, never()).save(any());
    }

    @Test
    public void updateTask_clearsAssignedUsersWhenEmptyListProvided() {
        testTask.setAssignedUsers(new ArrayList<>(List.of(testUser)));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(testTask)).thenReturn(testTask);

        Task input = new Task();
        input.setName("name");
        input.setAssignedUsers(new ArrayList<>());

        Task result = taskService.updateTask(1L, input);

        assertTrue(result.getAssignedUsers().isEmpty());
    }
}