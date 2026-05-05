package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TaskPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TaskPutDTO;
import ch.uzh.ifi.hase.soprafs26.service.TaskService;
import ch.uzh.ifi.hase.soprafs26.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ch.uzh.ifi.hase.soprafs26.controller.ProjectControllerTest.createMockUser;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockitoBean
    private TaskService taskService;

    @Autowired
    @MockitoBean
    private UserService userService;

    @Test
    public void givenTasks_whenGetTasks_thenReturnJsonArray() throws Exception {
        // given
        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,1));
        task.setOriginalLanguage("EN");
        task.setPriority(Priority.MEDIUM);

        List<Task> allTasks = Collections.singletonList(task);

        given(taskService.getTasks()).willReturn(allTasks);

        // when
        MockHttpServletRequestBuilder getRequest = get("/tasks").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer 1");

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(task.getName())))
                .andExpect(jsonPath("$[0].timeEstimate", is((double) task.getTimeEstimate())))
                .andExpect(jsonPath("$[0].dueDate", is(task.getDueDate().toString())))
                .andExpect(jsonPath("$[0].originalLanguage", is(task.getOriginalLanguage())))
                .andExpect(jsonPath("$[0].priority", is(task.getPriority().toString())))
                .andExpect(jsonPath("$[0].description", is(task.getDescription())));
    }

    @Test
    public void createTask_validInput_taskCreated() throws Exception {
        // given
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        task.setOriginalLanguage("EN");
        task.setPriority(Priority.MEDIUM);

        TaskPostDTO taskPostDTO = new TaskPostDTO();
        taskPostDTO.setName("Test Project");
        taskPostDTO.setDescription("Test Description");
        taskPostDTO.setOriginalLanguage("EN");
        taskPostDTO.setPriority(Priority.MEDIUM);
        taskPostDTO.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        taskPostDTO.setTimeEstimate(1.0f);

        User user = createMockUser();

        given(taskService.createTask(Mockito.any(), eq(user.getToken()))).willReturn(task);
        given(userService.getUserByToken(eq(user.getToken()))).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/tasks").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(taskPostDTO)).header("Authorization", user.getToken());

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(task.getId().intValue())))
                .andExpect(jsonPath("$.name", is(task.getName())))
                .andExpect(jsonPath("$.description", is(task.getDescription())));
    }

    @Test
    public void givenTasks_whenGetTask_thenReturnJsonArray() throws Exception {
        // given
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,1));
        task.setOriginalLanguage("EN");
        task.setPriority(Priority.MEDIUM);

        given(taskService.getTaskById(1L)).willReturn(Optional.of(task));

        // when
        MockHttpServletRequestBuilder getRequest = get("/tasks/1").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer 1");

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(task.getName())))
                .andExpect(jsonPath("$.timeEstimate", is((double) task.getTimeEstimate())))
                .andExpect(jsonPath("$.dueDate", is(task.getDueDate().toString())))
                .andExpect(jsonPath("$.originalLanguage", is(task.getOriginalLanguage())))
                .andExpect(jsonPath("$.priority", is(task.getPriority().toString())))
                .andExpect(jsonPath("$.description", is(task.getDescription())));
    }

    @Test
    public void givenTasks_whenGetProject_thenReturnNotFound() throws Exception {
        // given
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        task.setOriginalLanguage("EN");
        task.setPriority(Priority.MEDIUM);

        given(taskService.getTaskById(1L)).willReturn(Optional.of(task));

        // when
        MockHttpServletRequestBuilder getRequest = get("/tasks/2").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer 1");

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound()).andExpect(jsonPath("$.detail", is("Task with id 2 does not exist")));
    }

    @Test
    public void givenTasks_whenDeleteTask_thenReturnOk() throws Exception {
        // given
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        task.setOriginalLanguage("EN");
        task.setPriority(Priority.MEDIUM);

        given(taskService.getTaskById(1L)).willReturn(Optional.of(task));

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/tasks/1").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer 1");

        // then
        mockMvc.perform(deleteRequest).andExpect(status().isOk());
    }

    @Test
    public void givenTasks_whenDeleteTask_thenReturnNotFound() throws Exception {
        // given
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        task.setOriginalLanguage("EN");
        task.setPriority(Priority.MEDIUM);

        given(taskService.getTaskById(1L)).willReturn(Optional.of(task));

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/tasks/2").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer 1");

        // then
        mockMvc.perform(deleteRequest).andExpect(status().isNotFound());
    }

    @Test
    public void updateTask_validInput_taskUpdated() throws Exception {
        // given
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        task.setOriginalLanguage("EN");
        task.setPriority(Priority.MEDIUM);

        TaskPutDTO taskPutDTO = new TaskPutDTO();
        taskPutDTO.setName("Test Project Updated");
        taskPutDTO.setDescription("Test Description Updated");
        taskPutDTO.setTimeEstimate(1.0f);
        taskPutDTO.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        taskPutDTO.setOriginalLanguage("EN");
        taskPutDTO.setPriority(Priority.MEDIUM);

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setName("Test Project Updated");
        updatedTask.setDescription("Test Description Updated");
        updatedTask.setTimeEstimate(1.0f);
        updatedTask.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        updatedTask.setOriginalLanguage("EN");
        updatedTask.setPriority(Priority.MEDIUM);

        given(taskService.getTaskById(1L)).willReturn(Optional.of(task));
        given(taskService.updateTask(1L, updatedTask)).willReturn(updatedTask);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/tasks/1").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(taskPutDTO)).header("Authorization", "Bearer 1");

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
