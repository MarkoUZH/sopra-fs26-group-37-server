package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.constant.TaskStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.rest.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class TaskDTOMapperTest {
	@Test
	public void testCreateTask_fromTaskPostDTO_toTest_success() {
		// create ProjectPostDTO
		TaskPostDTO taskPostDTO = new TaskPostDTO();
        taskPostDTO.setName("name");
        taskPostDTO.setDescription("description");

		// MAP -> Create user
		Task task = TaskDTOMapper.INSTANCE.convertTaskPostDTOtoEntity(taskPostDTO);

		// check content
		assertEquals(taskPostDTO.getName(), task.getName());
		assertEquals(taskPostDTO.getDescription(), task.getDescription());
	}

    @Test
    public void testGetTask_fromEntity_success() {
        Project project = new Project();
        project.setId(9L);

        Tag tag = new Tag();
        tag.setId(50L);

        Task task = new Task();
        task.setId(1L);
        task.setName("Refactor");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(Priority.HIGH);
        task.setProject(project);
        task.setTags(List.of(tag));

        TaskGetDTO dto = TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(task);

        assertEquals(TaskStatus.IN_PROGRESS, dto.getStatus());
        assertEquals(Priority.HIGH, dto.getPriority());
        assertEquals(9L, dto.getProject().getId());
        assertEquals(50L, dto.getTags().get(0).getId());
    }

    @Test
    public void testUpdateTask_fromTask_toTaskGetDTO_success() {
        // create Project
        TaskPutDTO taskPutDTO = new TaskPutDTO();
        taskPutDTO.setName("test 1");
        taskPutDTO.setDescription("test description");

        // MAP -> Create UserGetDTO
        Task task = TaskDTOMapper.INSTANCE.convertTaskPutDTOtoEntity(taskPutDTO);

        // check content
        assertEquals(task.getId(), taskPutDTO.getId());
        assertEquals(task.getName(), taskPutDTO.getName());
        assertEquals(task.getDescription(), taskPutDTO.getDescription());
    }
}
