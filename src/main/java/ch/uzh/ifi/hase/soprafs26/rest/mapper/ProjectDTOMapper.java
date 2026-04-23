package ch.uzh.ifi.hase.soprafs26.rest.mapper;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectPutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProjectDTOMapper {
	ProjectDTOMapper INSTANCE = Mappers.getMapper(ProjectDTOMapper.class);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "originalLanguage", target = "originalLanguage")
    Project convertProjectPostDTOtoEntity(ProjectPostDTO projectPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "members", target = "members")
    @Mapping(source = "sprints", target = "sprints")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "tasks", target = "tasks")
    ProjectGetDTO convertEntityToProjectGetDTO(Project project);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Project convertProjectPutDTOtoEntity(ProjectPutDTO projectPutDTO);
}
