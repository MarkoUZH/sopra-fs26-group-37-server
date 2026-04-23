package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.SprintPutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import java.util.List;


@Mapper
public interface SprintDTOMapper {

    SprintDTOMapper INSTANCE = Mappers.getMapper(SprintDTOMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true) 
    @Mapping(target = "tasks", ignore = true)   
    Sprint convertSprintPostDTOtoEntity(SprintPostDTO sprintPostDTO);

    @Mapping(source = "project.id", target = "projectId")
	@Mapping(source = "project.name", target = "projectName") // Add this!
	SprintGetDTO convertEntityToSprintDTO(Sprint sprint);
	List<SprintGetDTO> convertEntitiesToSprintDTOs(List<Sprint> sprints);

    @Mapping(target = "id", ignore = true)      // Don't let the DTO change the ID
    @Mapping(target = "project", ignore = true) // Don't let the DTO change the Project
    @Mapping(target = "tasks", ignore = true)   // Tasks are usually managed elsewhere
    Sprint convertSprintPutDTOtoEntity(SprintPutDTO sprintPutDTO);

	
}