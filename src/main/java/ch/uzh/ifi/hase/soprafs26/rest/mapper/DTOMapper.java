package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutDTO;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

	DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

	@Mapping(source = "email", target = "email")
	@Mapping(source = "username", target = "username")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "password", target = "password")
	@Mapping(source = "language", target = "language")
	@Mapping(source = "manager", target = "manager")
	@Mapping(target = "token", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "id", ignore = true)
	User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);
	

	@Mapping(source = "id", target = "id")
	@Mapping(source = "email", target = "email")
	@Mapping(source = "username", target = "username")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "token", target = "token")
	@Mapping(source = "language", target = "language")
	@Mapping(source = "manager", target = "manager")
	UserGetDTO convertEntityToUserGetDTO(User user);

	@Mapping(source = "username", target = "username")
	@Mapping(source = "password", target = "password")
	@Mapping(source = "name", target = "name")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "email", ignore = true)
	@Mapping(target = "token", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "language", ignore = true)
	@Mapping(target = "manager", ignore = true)
User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);
}
