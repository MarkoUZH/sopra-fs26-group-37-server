package ch.uzh.ifi.hase.soprafs26.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	private User testUser;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		// given
		testUser = new User();
		testUser.setId(1L);
		testUser.setUsername("testUsername");

		// when -> any object is being save in the userRepository -> return the dummy
		// testUser
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
	}

	@Test
	public void createUser_validInputs_success() {
		// when -> any object is being save in the userRepository -> return the dummy
		// testUser
		User createdUser = userService.createUser(testUser);

		// then
		Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

		assertEquals(testUser.getId(), createdUser.getId());
		assertEquals(testUser.getUsername(), createdUser.getUsername());
		assertNotNull(createdUser.getToken());
		assertEquals(UserStatus.ONLINE, createdUser.getStatus());
	}

	@Test
	public void createUser_duplicateInputs_throwsException() {
		// given -> a first user has already been created
		userService.createUser(testUser);

		// when -> setup additional mocks for UserRepository
		Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

		// then -> attempt to create second user with same user -> check that an error
		// is thrown
		assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
	}

	@Test
	public void loginUser_invalidPassword_throwsException() {
		// given
		User existingUser = new User();
		existingUser.setUsername("testUsername");
		existingUser.setPassword("correctPassword");

		User loginAttempt = new User();
		loginAttempt.setUsername("testUsername");
		loginAttempt.setPassword("wrongPassword");

		// Mock: findByUsername returns the user, but the passwords won't match
		Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(existingUser);

		// then -> attempt to login with wrong password -> check for 401 UNAUTHORIZED
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			userService.loginUser(loginAttempt);
		});

		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
		assertTrue(exception.getReason().contains("Wrong password"));
	}


		@Test
	public void updateUser_validUsername_success() {
		// given
		User updates = new User();
		updates.setUsername("newUsername");

		Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));

		// when
		userService.updateUser(1L, updates);

		// then
		assertEquals("newUsername", testUser.getUsername());
		Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
	}

	@Test
	public void updateUser_validPassword_success() {
		// given
		User updates = new User();
		updates.setPassword("newSecurePassword");

		Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));

		// when
		userService.updateUser(1L, updates);

		// then
		assertEquals("newSecurePassword", testUser.getPassword());
		Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
	}

	@Test
	public void updateUser_nonExistentUser_throwsException() {
		// given
		Mockito.when(userRepository.findById(99L)).thenReturn(java.util.Optional.empty());

		// when / then
		assertThrows(ResponseStatusException.class, () -> userService.updateUser(99L, new User()));
	}

	//for the token verification. all our services use this now so it better work lol
	@Test
    public void verifyToken_validToken_success() {
        // given
        String validToken = "valid-token-123";
        testUser.setToken(validToken);
        
        Mockito.when(userRepository.findByToken(validToken)).thenReturn(testUser);

        // when & then: should not throw any exception
        assertDoesNotThrow(() -> userService.verifyToken(validToken));
    }

    @Test
    public void verifyToken_invalidToken_throwsUnauthorized() {
        // given
        String invalidToken = "wrong-token";
        Mockito.when(userRepository.findByToken(invalidToken)).thenReturn(null);

        // when & then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> userService.verifyToken(invalidToken));
            
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid token!", exception.getReason());
    }

    @Test
    public void verifyToken_nullToken_throwsUnauthorized() {
        // given
        String nullToken = null;

        // when & then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> userService.verifyToken(nullToken));
            
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Token is missing!", exception.getReason());
    }





}
