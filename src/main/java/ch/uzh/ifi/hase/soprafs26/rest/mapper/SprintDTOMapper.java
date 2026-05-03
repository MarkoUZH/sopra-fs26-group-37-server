package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintPutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import java.util.List;


@Mapper(imports = { ch.uzh.ifi.hase.soprafs26.constant.TaskStatus.class })
public interface SprintDTOMapper {

    SprintDTOMapper INSTANCE = Mappers.getMapper(SprintDTOMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true) 
    @Mapping(target = "tasks", ignore = true)   
    Sprint convertSprintPostDTOtoEntity(SprintPostDTO sprintPostDTO);

    @Mapping(source = "project.id", target = "projectId")
	@Mapping(source = "project.name", target = "projectName") // Add this!
    @Mapping(target = "totalTasks", expression = "java(sprint.getTasks() != null ? sprint.getTasks().size() : 0)")
    @Mapping(target = "completedTasks", expression = "java(sprint.getTasks() != null ? (int) sprint.getTasks().stream().filter(t -> t.getStatus() == TaskStatus.DONE).count() : 0)")
	SprintGetDTO convertEntityToSprintDTO(Sprint sprint);
	List<SprintGetDTO> convertEntitiesToSprintDTOs(List<Sprint> sprints);

    @Mapping(target = "id", ignore = true)      // Don't let the DTO change the ID
    @Mapping(target = "project", ignore = true) // Don't let the DTO change the Project
    @Mapping(target = "tasks", ignore = true)   // Tasks are usually managed elsewhere
    Sprint convertSprintPutDTOtoEntity(SprintPutDTO sprintPutDTO);

	
}