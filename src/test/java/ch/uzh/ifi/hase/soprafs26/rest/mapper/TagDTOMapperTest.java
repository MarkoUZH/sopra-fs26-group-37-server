package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TagGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TagPostDTO;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagDTOMapperTest {

    @Test
    public void testCreateTag_fromPostDTO_success() {
        TagPostDTO dto = new TagPostDTO();
        dto.setName("Bug");
        dto.setProjectId(1L);

        Tag tag = TagDTOMapper.INSTANCE.convertTagPostDTOtoEntity(dto);

        assertEquals("Bug", tag.getName());
        assertEquals(1L, tag.getProject().getId());
    }

    @Test
    public void testGetTag_fromEntity_success() {
        Task t1 = new Task(); t1.setId(101L);
        Task t2 = new Task(); t2.setId(102L);

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Feature");
        tag.setTasks(List.of(t1, t2));

        TagGetDTO dto = TagDTOMapper.INSTANCE.convertEntityToTagGetDTO(tag);

        assertEquals("Feature", dto.getName());
        assertEquals(2, dto.getTaskIds().size());
        assertEquals(101L, dto.getTaskIds().get(0));
    }
}