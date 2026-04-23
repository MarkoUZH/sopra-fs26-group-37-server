package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.constant.SprintStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintPostDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SprintDTOMapperTest {

    @Test
    public void testCreateSprint_fromPostDTO_success() {
        SprintPostDTO sprintPostDTO = new SprintPostDTO();
        sprintPostDTO.setName("Sprint 1");
        sprintPostDTO.setSprintStatus(SprintStatus.ACTIVE);

        Sprint sprint = SprintDTOMapper.INSTANCE.convertSprintPostDTOtoEntity(sprintPostDTO);

        assertEquals(sprintPostDTO.getName(), sprint.getName());
        assertEquals(sprintPostDTO.getSprintStatus(), sprint.getSprintStatus());
    }

    @Test
    public void testGetSprint_fromEntity_success() {
        Project project = new Project();
        project.setId(5L);
        project.setName("Main Project");

        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setName("Sprint Alpha");
        sprint.setProject(project);

        SprintGetDTO sprintGetDTO = SprintDTOMapper.INSTANCE.convertEntityToSprintDTO(sprint);

        assertEquals(1L, sprintGetDTO.getId());
        assertEquals(5L, sprintGetDTO.getProjectId());
        assertEquals("Main Project", sprintGetDTO.getProjectName());
    }
}