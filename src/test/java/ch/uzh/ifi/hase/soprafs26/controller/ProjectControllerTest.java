package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs26.service.ProjectService;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Test
    public void givenProjects_whenGetProjects_thenReturnJsonArray() throws Exception {
        // given
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");

        List<Project> allProjects = Collections.singletonList(project);

        given(projectService.getProjects()).willReturn(allProjects);

        // when
        MockHttpServletRequestBuilder getRequest = get("/projects").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(project.getName())))
                .andExpect(jsonPath("$[0].description", is(project.getDescription())));
    }

    @Test
    public void createProject_validInput_projectCreated() throws Exception {
        // given
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");

        ProjectPostDTO projectPostDTO = new ProjectPostDTO();
        project.setName("Test Project");
        project.setDescription("Test Description");

        given(projectService.createProject(Mockito.any())).willReturn(project);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/projects").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(projectPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(project.getId().intValue())))
                .andExpect(jsonPath("$.name", is(project.getName())))
                .andExpect(jsonPath("$.description", is(project.getDescription())));
    }

    @Test
    public void givenProjects_whenGetProject_thenReturnJsonArray() throws Exception {
        // given
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");

        given(projectService.getProjectById(1L)).willReturn(Optional.of(project));

        // when
        MockHttpServletRequestBuilder getRequest = get("/projects/1").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(project.getName())))
                .andExpect(jsonPath("$.description", is(project.getDescription())));
    }

    @Test
    public void givenProjects_whenGetProject_thenReturnNotFound() throws Exception {
        // given
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");

        given(projectService.getProjectById(1L)).willReturn(Optional.of(project));

        // when
        MockHttpServletRequestBuilder getRequest = get("/projects/2").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound()).andExpect(jsonPath("$.detail", is("Project with id 2 does not exist")));
    }

    @Test
    public void givenProjects_whenDeleteProject_thenReturnOk() throws Exception {
        // given
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");

        given(projectService.getProjectById(1L)).willReturn(Optional.of(project));

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/projects/1").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(deleteRequest).andExpect(status().isOk());
    }

    @Test
    public void givenProjects_whenDeleteProject_thenReturnNotFound() throws Exception {
        // given
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");

        given(projectService.getProjectById(1L)).willReturn(Optional.of(project));

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/projects/2").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(deleteRequest).andExpect(status().isNotFound());
    }

    @Test
    public void updateProject_validInput_projectUpdated() throws Exception {
        // given
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");

        ProjectPostDTO projectPostDTO = new ProjectPostDTO();
        project.setName("Test Project Updated");
        project.setDescription("Test Description Updated");

        Project projectUpdated = new Project();
        projectUpdated.setId(1L);
        projectUpdated.setName("Test Project Updated");
        projectUpdated.setDescription("Test Description Updated");

        given(projectService.getProjectById(1L)).willReturn(Optional.of(project));
        given(projectService.updateProject(1L, projectUpdated)).willReturn(projectUpdated);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/projects/1").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(projectPostDTO));

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
