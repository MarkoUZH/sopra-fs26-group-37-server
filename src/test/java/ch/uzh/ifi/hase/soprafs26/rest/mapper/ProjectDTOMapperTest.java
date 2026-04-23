package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectPutDTO;
import org.junit.jupiter.api.Test;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectDTOMapperTest {

    @Test
    public void testCreateProject_fromProjectPostDTO_toProject_success() {
        ProjectPostDTO projectPostDTO = new ProjectPostDTO();
        projectPostDTO.setName("New Project");
        projectPostDTO.setDescription("Description");

        Project project = ProjectDTOMapper.INSTANCE.convertProjectPostDTOtoEntity(projectPostDTO);

        assertEquals(projectPostDTO.getName(), project.getName());
        assertEquals(projectPostDTO.getDescription(), project.getDescription());
    }

    @Test
    public void testGetProject_fromProject_toProjectGetDTO_success() {
        User owner = new User();
        owner.setId(10L);

        Project project = new Project();
        project.setId(1L);
        project.setName("Existing Project");
        project.setOwner(owner);
        project.setMembers(Collections.emptyList());

        ProjectGetDTO dto = ProjectDTOMapper.INSTANCE.convertEntityToProjectGetDTO(project);

        assertEquals(project.getId(), dto.getId());
        assertEquals(project.getName(), dto.getName());
        assertEquals(10L, dto.getOwner().getId());
    }

    @Test
    public void testUpdateProject_fromProjectPutDTO_toProject_success() {
        ProjectPutDTO projectPutDTO = new ProjectPutDTO();
        projectPutDTO.setName("New Project");
        projectPutDTO.setDescription("Description");

        Project project = ProjectDTOMapper.INSTANCE.convertProjectPutDTOtoEntity(projectPutDTO);

        assertEquals(projectPutDTO.getName(), project.getName());
        assertEquals(projectPutDTO.getDescription(), project.getDescription());
    }
}