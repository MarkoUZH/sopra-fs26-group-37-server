package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintPutDTO;
import ch.uzh.ifi.hase.soprafs26.service.SprintService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SprintController.class)
public class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SprintService sprintService;

    @MockitoBean
    private UserService userService;

    @Test
    public void createSprint_validInput_sprintCreated() throws Exception {
        // given
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setName("Sprint 1");

        SprintPostDTO sprintPostDTO = new SprintPostDTO();
        sprintPostDTO.setName("Sprint 1");
        sprintPostDTO.setProjectId(1L);

        given(sprintService.createSprint(Mockito.any(), Mockito.any())).willReturn(sprint);

        // when
        MockHttpServletRequestBuilder postRequest = post("/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sprintPostDTO))
                .header("Authorization", "Bearer test-token");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(sprint.getId().intValue())))
                .andExpect(jsonPath("$.name", is(sprint.getName())));
    }

    @Test
    public void getAllSprints_success_thenReturnJsonArray() throws Exception {
        // given
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setName("Sprint 1");

        List<Sprint> allSprints = Collections.singletonList(sprint);

        given(sprintService.getSprints()).willReturn(allSprints);

        // when
        MockHttpServletRequestBuilder getRequest = get("/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test-token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(sprint.getName())));
    }

    @Test
    public void getSprintById_validId_success() throws Exception {
        // given
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setName("Sprint 1");

        given(sprintService.getSprintById(1L)).willReturn(sprint);

        // when
        MockHttpServletRequestBuilder getRequest = get("/sprints/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test-token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(sprint.getId().intValue())))
                .andExpect(jsonPath("$.name", is(sprint.getName())));
    }

    @Test
    public void updateSprint_validInput_success() throws Exception {
        // given
        Sprint updatedSprint = new Sprint();
        updatedSprint.setId(1L);
        updatedSprint.setName("Updated Sprint Name");

        SprintPutDTO sprintPutDTO = new SprintPutDTO();
        sprintPutDTO.setName("Updated Sprint Name");

        given(sprintService.updateSprint(Mockito.eq(1L), Mockito.any())).willReturn(updatedSprint);

        // when
        MockHttpServletRequestBuilder putRequest = put("/sprints/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sprintPutDTO))
                .header("Authorization", "Bearer test-token");

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedSprint.getName())));
    }

    @Test
    public void deleteSprint_validId_success() throws Exception {
        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/sprints/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test-token");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());

        Mockito.verify(sprintService, Mockito.times(1)).deleteSprint(1L);
    }

    /**
     * Helper Method to convert DTO object into a JSON string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
