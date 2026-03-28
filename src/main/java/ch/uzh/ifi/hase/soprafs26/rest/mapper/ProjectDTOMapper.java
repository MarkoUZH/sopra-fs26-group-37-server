package ch.uzh.ifi.hase.soprafs26.rest.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProjectDTOMapper {
	ProjectDTOMapper INSTANCE = Mappers.getMapper(ProjectDTOMapper.class);
}
