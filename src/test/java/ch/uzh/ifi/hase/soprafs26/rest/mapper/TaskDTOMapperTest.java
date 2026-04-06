package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.rest.dto.*;
import org.junit.jupiter.api.Test;

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
	public void testGetTask_fromTask_toTaskGetDTO_success() {
		// create Project
		Task task = new Task();
        task.setName("test 1");
        task.setDescription("test description");

		// MAP -> Create UserGetDTO
		TaskGetDTO taskGetDTO = TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(task);

		// check content
		assertEquals(task.getId(), taskGetDTO.getId());
		assertEquals(task.getName(), taskGetDTO.getName());
		assertEquals(task.getDescription(), taskGetDTO.getDescription());
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
