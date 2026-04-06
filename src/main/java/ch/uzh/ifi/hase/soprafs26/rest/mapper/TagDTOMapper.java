package ch.uzh.ifi.hase.soprafs26.rest.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagDTOMapper {
	TagDTOMapper INSTANCE = Mappers.getMapper(TagDTOMapper.class);
}
