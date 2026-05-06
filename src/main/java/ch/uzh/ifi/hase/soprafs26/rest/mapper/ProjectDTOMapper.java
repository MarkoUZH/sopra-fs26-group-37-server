package ch.uzh.ifi.hase.soprafs26.rest.mapper;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ProjectPutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(uses = { TaskDTOMapper.class })
public interface ProjectDTOMapper {
	ProjectDTOMapper INSTANCE = Mappers.getMapper(ProjectDTOMapper.class);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "originalLanguage", target = "originalLanguage")
    @Mapping(source = "memberIds", target = "members", qualifiedByName = "idsToMembers")
    Project convertProjectPostDTOtoEntity(ProjectPostDTO projectPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "members", target = "members")
    @Mapping(source = "sprints", target = "sprints")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "tasks", target = "tasks")
    @Mapping(source = "originalLanguage", target = "originalLanguage")
    ProjectGetDTO convertEntityToProjectGetDTO(Project project);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Project convertProjectPutDTOtoEntity(ProjectPutDTO projectPutDTO);

    @Named("idsToMembers")
    default List<User> idsToMembers(List<Long> ids) {
        return ids == null ? null : ids.stream().map(id -> {
            User user = new User();
            user.setId(id);
            return user;
        }).toList();
    }
}
