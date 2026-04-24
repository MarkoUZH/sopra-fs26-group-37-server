package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest(properties = "HUGGINGFACE_API_TOKEN=mock-key")
public class TagServiceIntegrationTest {

    @Qualifier("tagRepository")
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagService tagService;

    @BeforeEach
    public void setup() {
        tagRepository.deleteAll();
    }

    // Helper — builds and persists a tag with a valid name
    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tagService.createTag(tag);
    }

    @Test
    public void createTag_validName_isPersisted() {
        Tag created = createTag("featureTag");

        assertNotNull(created.getId());
        assertEquals("featureTag", created.getName());
        assertTrue(tagRepository.findById(created.getId()).isPresent());
    }

    @Test
    public void createTag_nullName_throwsBadRequest() {
        Tag invalid = new Tag();
        invalid.setName(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.createTag(invalid));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(0, tagRepository.count());
    }

    @Test
    public void createTag_blankName_throwsBadRequest() {
        Tag invalid = new Tag();
        invalid.setName("   ");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.createTag(invalid));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    public void getTags_multipleTags_returnsAll() {
        createTag("tag1");
        createTag("tag2");

        List<Tag> result = tagService.getTags();

        assertEquals(2, result.size());
    }

    @Test
    public void getTags_emptyRepository_returnsEmptyList() {
        assertTrue(tagService.getTags().isEmpty());
    }

    @Test
    public void getTagById_existingId_returnsTag() {
        Tag saved = createTag("findMe");

        Tag found = tagService.getTagById(saved.getId());

        assertEquals(saved.getId(), found.getId());
        assertEquals("findMe", found.getName());
    }

    @Test
    public void getTagById_nonExistingId_throwsNotFound() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.getTagById(9999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void updateTag_validId_nameIsPersisted() {
        Tag saved = createTag("original");

        Tag input = new Tag();
        input.setName("updated");

        Tag result = tagService.updateTag(saved.getId(), input);

        assertEquals("updated", result.getName());
        assertEquals("updated", tagRepository.findById(saved.getId()).orElseThrow().getName());
    }

    @Test
    public void updateTag_nullFields_existingValuesPreserved() {
        Tag saved = createTag("keepMe");

        // All-null input — name must be unchanged
        Tag result = tagService.updateTag(saved.getId(), new Tag());

        assertEquals("keepMe", result.getName());
    }

    @Test
    public void updateTag_nonExistingId_throwsNotFound() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.updateTag(9999L, new Tag()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void deleteTag_existingId_isRemovedFromRepository() {
        Tag saved = createTag("toDelete");
        Long id = saved.getId();

        Tag deleted = tagService.deleteTag(id);

        assertEquals(id, deleted.getId());
        assertFalse(tagRepository.findById(id).isPresent());
    }

    @Test
    public void deleteTag_returnsSnapshotOfDeletedTag() {
        Tag saved = createTag("snapshot");

        Tag deleted = tagService.deleteTag(saved.getId());

        assertEquals("snapshot", deleted.getName());
    }

    @Test
    public void deleteTag_nonExistingId_throwsNotFound() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> tagService.deleteTag(9999L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}