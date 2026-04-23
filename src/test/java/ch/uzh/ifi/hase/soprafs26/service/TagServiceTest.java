package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag testTag;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("testTag");

        when(tagRepository.save(any())).thenReturn(testTag);
    }

    @Test
    public void getTags_returnsAllTags() {
        List<Tag> tags = Arrays.asList(testTag, new Tag());
        when(tagRepository.findAll()).thenReturn(tags);

        List<Tag> result = tagService.getTags();

        assertEquals(2, result.size());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    public void getTags_emptyRepository_returnsEmptyList() {
        when(tagRepository.findAll()).thenReturn(new ArrayList<>());

        assertTrue(tagService.getTags().isEmpty());
    }

    @Test
    public void getTagById_existingId_returnsTag() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));

        Tag result = tagService.getTagById(1L);

        assertEquals(testTag.getId(), result.getId());
        assertEquals(testTag.getName(), result.getName());
    }

    @Test
    public void getTagById_nonExistingId_throwsNotFound() {
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.getTagById(99L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void createTag_validName_savesAndReturnsTag() {
        Tag result = tagService.createTag(testTag);

        verify(tagRepository, times(1)).save(testTag);
        verify(tagRepository, times(1)).flush();
        assertEquals(testTag.getId(), result.getId());
        assertEquals(testTag.getName(), result.getName());
    }

    @Test
    public void createTag_nullName_throwsBadRequest() {
        Tag invalidTag = new Tag();
        invalidTag.setName(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.createTag(invalidTag));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(tagRepository, never()).save(any());
    }

    @Test
    public void createTag_blankName_throwsBadRequest() {
        Tag invalidTag = new Tag();
        invalidTag.setName("   ");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.createTag(invalidTag));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(tagRepository, never()).save(any());
    }

    @Test
    public void createTag_emptyName_throwsBadRequest() {
        Tag invalidTag = new Tag();
        invalidTag.setName("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.createTag(invalidTag));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(tagRepository, never()).save(any());
    }

    @Test
    public void updateTag_updatesName() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
        when(tagRepository.save(testTag)).thenReturn(testTag);

        Tag input = new Tag();
        input.setName("updatedName");

        Tag result = tagService.updateTag(1L, input);

        assertEquals("updatedName", result.getName());
        verify(tagRepository, times(1)).save(testTag);
    }

    @Test
    public void updateTag_updatesProject() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
        when(tagRepository.save(testTag)).thenReturn(testTag);

        Project newProject = new Project();
        newProject.setId(2L);
        newProject.setName("newProject");

        Tag input = new Tag();
        input.setProject(newProject);

        Tag result = tagService.updateTag(1L, input);

        assertEquals(newProject, result.getProject());
    }

    @Test
    public void updateTag_updatesTasks() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
        when(tagRepository.save(testTag)).thenReturn(testTag);

        Task task = new Task();
        task.setId(10L);
        task.setName("someTask");

        Tag input = new Tag();
        input.setTasks(List.of(task));

        Tag result = tagService.updateTag(1L, input);

        assertEquals(1, result.getTasks().size());
        assertEquals(task, result.getTasks().get(0));
    }

    @Test
    public void updateTag_nullFields_existingValuesPreserved() {
        testTag.setName("originalName");
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
        when(tagRepository.save(testTag)).thenReturn(testTag);

        Tag result = tagService.updateTag(1L, new Tag());

        assertEquals("originalName", result.getName());
    }

    @Test
    public void updateTag_nonExistingId_throwsNotFound() {
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.updateTag(99L, new Tag()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(tagRepository, never()).save(any());
    }

    @Test
    public void deleteTag_existingId_deletesAndReturnsTag() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
        doNothing().when(tagRepository).deleteById(1L);

        Tag deleted = tagService.deleteTag(1L);

        verify(tagRepository, times(1)).deleteById(1L);
        verify(tagRepository, times(1)).flush();
        assertEquals(testTag.getId(), deleted.getId());
        assertEquals(testTag.getName(), deleted.getName());
    }

    @Test
    public void deleteTag_nonExistingId_throwsNotFound() {
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.deleteTag(99L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(tagRepository, never()).deleteById(any());
    }
}