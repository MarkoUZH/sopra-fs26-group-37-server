package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TagPostDTO;
import ch.uzh.ifi.hase.soprafs26.service.TagService;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private UserService userService;

    @Test
    public void givenTags_whenGetAllTags_thenReturnJsonArray() throws Exception {
        // given
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Backend");

        List<Tag> allTags = Collections.singletonList(tag);

        given(tagService.getTags()).willReturn(allTags);

        // when
        MockHttpServletRequestBuilder getRequest = get("/tags")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(tag.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(tag.getName())));
    }

    @Test
    public void givenTags_whenGetAllTags_thenReturnEmptyArray() throws Exception {
        // given
        given(tagService.getTags()).willReturn(Collections.emptyList());

        // when
        MockHttpServletRequestBuilder getRequest = get("/tags")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void givenTag_whenGetTagById_thenReturnTag() throws Exception {
        // given
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Backend");

        given(tagService.getTagById(1L)).willReturn(tag);

        // when
        MockHttpServletRequestBuilder getRequest = get("/tags/1")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(tag.getId().intValue())))
                .andExpect(jsonPath("$.name", is(tag.getName())));
    }

    @Test
    public void givenNoTag_whenGetTagById_thenReturnNotFound() throws Exception {
        // given
        given(tagService.getTagById(99L))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag with id 99 not found"));

        // when
        MockHttpServletRequestBuilder getRequest = get("/tags/99")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", is("Tag with id 99 not found")));
    }

    @Test
    public void createTag_validInput_tagCreated() throws Exception {
        // given
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Backend");

        TagPostDTO tagPostDTO = new TagPostDTO();
        tagPostDTO.setName("Backend");
        tagPostDTO.setProjectId(1L);

        given(tagService.createTag(Mockito.any())).willReturn(tag);

        // when
        MockHttpServletRequestBuilder postRequest = post("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(tagPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(tag.getId().intValue())))
                .andExpect(jsonPath("$.name", is(tag.getName())));
    }

    @Test
    public void createTag_blankName_thenReturnBadRequest() throws Exception {
        // given
        TagPostDTO tagPostDTO = new TagPostDTO();
        tagPostDTO.setName("");
        tagPostDTO.setProjectId(1L);

        given(tagService.createTag(Mockito.any()))
                .willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag name must not be empty"));

        // when
        MockHttpServletRequestBuilder postRequest = post("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(tagPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Tag name must not be empty")));
    }

    @Test
    public void updateTag_validInput_tagUpdated() throws Exception {
        // given
        Tag updatedTag = new Tag();
        updatedTag.setId(1L);
        updatedTag.setName("Frontend");

        TagPostDTO tagPostDTO = new TagPostDTO();
        tagPostDTO.setName("Frontend");

        given(tagService.updateTag(Mockito.eq(1L), Mockito.any())).willReturn(updatedTag);

        // when
        MockHttpServletRequestBuilder putRequest = put("/tags/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(tagPostDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedTag.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updatedTag.getName())));
    }

    @Test
    public void updateTag_tagNotFound_thenReturnNotFound() throws Exception {
        // given
        TagPostDTO tagPostDTO = new TagPostDTO();
        tagPostDTO.setName("Frontend");

        given(tagService.updateTag(Mockito.eq(99L), Mockito.any()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag with id 99 not found"));

        // when
        MockHttpServletRequestBuilder putRequest = put("/tags/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(tagPostDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", is("Tag with id 99 not found")));
    }

    @Test
    public void deleteTag_validId_thenReturnNoContent() throws Exception {
        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/tags/1")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteTag_tagNotFound_thenReturnNotFound() throws Exception {
        // given
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag with id 99 not found"))
                .when(tagService).deleteTag(99L);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/tags/99")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", is("Tag with id 99 not found")));
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