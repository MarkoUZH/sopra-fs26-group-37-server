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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setToken("test-token");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setLanguage("EN");

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    public void getUsers_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, new User()));

        List<User> result = userService.getUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getUsers_emptyRepository_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertTrue(userService.getUsers().isEmpty());
    }

    @Test
    public void createUser_validInputs_success() {
        User createdUser = userService.createUser(testUser);

        verify(userRepository, times(1)).save(Mockito.any());
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        userService.createUser(testUser);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void loginUser_validCredentials_success() {
        testUser.setStatus(UserStatus.OFFLINE);
        when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

        User loggedIn = userService.loginUser(testUser);

        assertEquals(UserStatus.ONLINE, loggedIn.getStatus());
    }

    @Test
    public void loginUser_usernameNotFound_throwsNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        User attempt = new User();
        attempt.setUsername("unknown");
        attempt.setPassword("any");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.loginUser(attempt));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void loginUser_wrongPassword_throwsUnauthorized() {
        User stored = new User();
        stored.setUsername("testUsername");
        stored.setPassword("correctPassword");
        when(userRepository.findByUsername("testUsername")).thenReturn(stored);

        User attempt = new User();
        attempt.setUsername("testUsername");
        attempt.setPassword("wrongPassword");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.loginUser(attempt));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Wrong password"));
    }

    @Test
    public void getUserById_existingId_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertEquals(testUser.getId(), result.getId());
    }

    @Test
    public void getUserById_nonExistingId_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.getUserById(99L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void getUserByToken_validToken_returnsUser() {
        when(userRepository.findByToken("test-token")).thenReturn(testUser);

        User result = userService.getUserByToken("test-token");

        assertEquals(testUser.getId(), result.getId());
    }

    @Test
    public void getUserByToken_nullToken_returnsNull() {
        assertNull(userService.getUserByToken(null));
        verify(userRepository, never()).findByToken(any());
    }

    @Test
    public void getUserByToken_emptyToken_returnsNull() {
        assertNull(userService.getUserByToken(""));
        verify(userRepository, never()).findByToken(any());
    }

    @Test
    public void verifyToken_validToken_doesNotThrow() {
        when(userRepository.findByToken("test-token")).thenReturn(testUser);

        assertDoesNotThrow(() -> userService.verifyToken("test-token"));
    }

    @Test
    public void verifyToken_invalidToken_throwsUnauthorized() {
        when(userRepository.findByToken("bad-token")).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.verifyToken("bad-token"));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid token!", exception.getReason());
    }

    @Test
    public void verifyToken_nullToken_throwsUnauthorized() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.verifyToken(null));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Token is missing!", exception.getReason());
    }

    @Test
    public void verifyToken_emptyToken_throwsUnauthorized() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.verifyToken(""));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Token is missing!", exception.getReason());
    }

    @Test
    public void updateUser_validUsername_updatesField() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User updates = new User();
        updates.setUsername("newUsername");

        userService.updateUser(1L, updates);

        assertEquals("newUsername", testUser.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void updateUser_validPassword_updatesField() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User updates = new User();
        updates.setPassword("newPassword");

        userService.updateUser(1L, updates);

        assertEquals("newPassword", testUser.getPassword());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void updateUser_validLanguage_updatesField() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User updates = new User();
        updates.setLanguage("FR");

        userService.updateUser(1L, updates);

        assertEquals("FR", testUser.getLanguage());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void updateUser_nullFields_existingValuesPreserved() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // All fields null/empty — nothing should be overwritten
        userService.updateUser(1L, new User());

        assertEquals("testUsername", testUser.getUsername());
        assertEquals("testPassword", testUser.getPassword());
    }

    @Test
    public void updateUser_nonExistingId_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.updateUser(99L, new User()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void updateUserLanguage_validToken_updatesLanguage() {
        when(userRepository.findByToken("test-token")).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.updateUserLanguage("test-token", "DE");

        assertEquals("DE", result.getLanguage());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void updateUserLanguage_invalidToken_throwsNotFound() {
        when(userRepository.findByToken("bad-token")).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.updateUserLanguage("bad-token", "DE"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    @Test
    public void setUserStatus_validToken_setsOffline() {
        when(userRepository.findByToken("test-token")).thenReturn(testUser);

        userService.setUserStatus("test-token");

        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void setUserStatus_unknownToken_doesNothing() {
        when(userRepository.findByToken("unknown")).thenReturn(null);

        // Should not throw — method guards with null check
        assertDoesNotThrow(() -> userService.setUserStatus("unknown"));
        verify(userRepository, never()).save(any());
    }

    @Test
    public void logoutById_validId_setsOffline() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.logoutById(1L);

        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void logoutById_nonExistingId_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.logoutById(99L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}