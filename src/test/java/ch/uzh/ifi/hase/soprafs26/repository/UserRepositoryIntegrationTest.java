package ch.uzh.ifi.hase.soprafs26.repository;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_success() {
        // given
        User user = new User();
        user.setName("First Last");
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEmail("test@uzh.ch");
        user.setToken("unique-token-123");
        user.setStatus(UserStatus.OFFLINE);
        user.setLanguage("German");

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername(user.getUsername());

        // then
        assertNotNull(found);
        assertEquals(user.getUsername(), found.getUsername());
        assertEquals(user.getEmail(), found.getEmail());
    }

    @Test
    public void findByToken_success() {
        // given
        User user = new User();
        user.setName("Token User");
        user.setUsername("tokenUser");
        user.setPassword("securePass");
        user.setEmail("token@uzh.ch");
        user.setToken("auth-token-xyz");
        user.setStatus(UserStatus.ONLINE);
        user.setLanguage("German");

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByToken("auth-token-xyz");

        // then
        assertNotNull(found);
        assertEquals(user.getUsername(), found.getUsername());
        assertEquals("auth-token-xyz", found.getToken());
    }

    @Test
    public void findByUsername_notFound_returnsNull() {
        // when
        User found = userRepository.findByUsername("nonExistent");

        // then
        assertNull(found);
    }

    @Test
    public void saveUser_success() {
        // given
        User user = new User();
        user.setName("New User");
        user.setUsername("newbie");
        user.setPassword("password");
        user.setEmail("new@uzh.ch");
        user.setToken("new-token");
        user.setStatus(UserStatus.OFFLINE);
        user.setLanguage("German");

        // when
        User savedUser = userRepository.save(user);

        // then
        assertNotNull(savedUser.getId());
        User found = entityManager.find(User.class, savedUser.getId());
        assertEquals("newbie", found.getUsername());
    }

    @Test
    public void updateUserStatus_success() {
        // given
        User user = new User();
        user.setName("Status User");
        user.setUsername("statusUser");
        user.setPassword("password");
        user.setEmail("status@uzh.ch");
        user.setToken("status-token");
        user.setStatus(UserStatus.OFFLINE);
        user.setLanguage("German");

        entityManager.persist(user);
        entityManager.flush();

        // when
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
        entityManager.flush();

        // then
        User updated = entityManager.find(User.class, user.getId());
        assertEquals(UserStatus.ONLINE, updated.getStatus());
    }
}