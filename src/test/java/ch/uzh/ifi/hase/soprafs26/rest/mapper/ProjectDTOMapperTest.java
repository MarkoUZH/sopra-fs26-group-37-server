package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class ProjectDTOMapperTest {
	@Test
	public void testCreateProject_fromProjectPostDTO_toProject_success() {
		// create ProjectPostDTO
		ProjectPostDTO projectPostDTO = new ProjectPostDTO();
		projectPostDTO.setName("name");
		projectPostDTO.setDescription("description");

		// MAP -> Create user
		Project project = ProjectDTOMapper.INSTANCE.convertProjectPostDTOtoEntity(projectPostDTO);

		// check content
		assertEquals(projectPostDTO.getName(), project.getName());
		assertEquals(projectPostDTO.getDescription(), project.getDescription());
	}

	@Test
	public void testGetProject_fromProject_toProjectGetDTO_success() {
		// create Project
		Project project = new Project();
		project.setName("test 1");
		project.setDescription("test description");

		// MAP -> Create UserGetDTO
		ProjectGetDTO projectGetDTO = ProjectDTOMapper.INSTANCE.convertEntityToProjectGetDTO(project);

		// check content
		assertEquals(project.getId(), projectGetDTO.getId());
		assertEquals(project.getName(), projectGetDTO.getName());
		assertEquals(project.getDescription(), projectGetDTO.getDescription());
	}
}
