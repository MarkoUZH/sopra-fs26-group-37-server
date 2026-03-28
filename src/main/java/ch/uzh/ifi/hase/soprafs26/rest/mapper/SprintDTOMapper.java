package ch.uzh.ifi.hase.soprafs26.rest.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SprintDTOMapper {
	SprintDTOMapper INSTANCE = Mappers.getMapper(SprintDTOMapper.class);
}
