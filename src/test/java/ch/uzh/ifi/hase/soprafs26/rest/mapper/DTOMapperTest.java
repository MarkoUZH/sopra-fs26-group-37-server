package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DTOMapperTest {

    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("username");
        userPostDTO.setEmail("test@email.com");
        userPostDTO.setPassword("password");
        userPostDTO.setName("name");
        userPostDTO.setManager(true);

        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        assertEquals(userPostDTO.getUsername(), user.getUsername());
        assertEquals(userPostDTO.getEmail(), user.getEmail());
        assertEquals(userPostDTO.getPassword(), user.getPassword());
        assertEquals(userPostDTO.getName(), user.getName());
        assertEquals(userPostDTO.getManager(), user.getManager());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");
        user.setManager(true);
        user.setPassword("password");
        user.setEmail("email@uzh.ch");
        user.setName("name");

        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
        assertEquals(user.getName(), userGetDTO.getName());
        assertEquals(user.getLanguage(), userGetDTO.getLanguage());
        assertEquals(user.getToken(), userGetDTO.getToken());
        assertEquals(user.getEmail(), userGetDTO.getEmail());
        assertEquals(user.getManager(), userGetDTO.getManager());
    }

    @Test
    public void testUpdateUser_fromUserPutDTO_toEntity_success() {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("newUsername");
        userPutDTO.setPassword("newPass");
        userPutDTO.setName("name");
        userPutDTO.setLanguage("German");

        User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        assertEquals(userPutDTO.getUsername(), user.getUsername());
        assertEquals(userPutDTO.getPassword(), user.getPassword());
        assertEquals(userPutDTO.getName(), user.getName());
        assertEquals(userPutDTO.getLanguage(), user.getLanguage());
    }
}