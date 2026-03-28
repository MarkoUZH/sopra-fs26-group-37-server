package ch.uzh.ifi.hase.soprafs26.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	public UserService(@Qualifier("userRepository") UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getUsers() {
		return this.userRepository.findAll();
	}

	public User createUser(User newUser) {
		newUser.setToken(UUID.randomUUID().toString());
		newUser.setStatus(UserStatus.ONLINE);
		checkIfUserExists(newUser);
		// saves the given entity but data is only persisted in the database once
		// flush() is called
		newUser = userRepository.save(newUser);
		userRepository.flush();

		log.debug("Created Information for User: {}", newUser);
		return newUser;
	}

		public User loginUser(User userCredentials){
		//look and find by username
		User existingUser = userRepository.findByUsername(userCredentials.getUsername());
		//throw an error if we can't find the user
		if (existingUser == null){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: Username not found");
		}
		//check the password
		if(!existingUser.getPassword().equals(userCredentials.getPassword())){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error: Wrong password"); }
		//login if everything is good
		existingUser.setStatus(UserStatus.ONLINE); //set status to online
		userRepository.flush();

		return existingUser;}
	

	public boolean verifyToken(String token) {
    if (token == null || token.isEmpty()) {
        return false;
    }
    User user = userRepository.findByToken(token);
    return user != null;
}
	public User getUserByToken(String token) {
		if (token == null || token.isEmpty()) {
			return null;
		}
		return userRepository.findByToken(token);
	}

	public User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}

	public void setUserStatus(String token) {
		User user = userRepository.findByToken(token);
		if (user != null) {
			user.setStatus(UserStatus.OFFLINE);
			userRepository.save(user);
			userRepository.flush();
		}
	}
	/**
	 * This is a helper method that will check the uniqueness criteria of the
	 * username and the name
	 * defined in the User entity. The method will do nothing if the input is unique
	 * and throw an error otherwise.
	 *
	 * @param userToBeCreated
	 * @throws org.springframework.web.server.ResponseStatusException
	 * @see User
	 */
	private void checkIfUserExists(User userToBeCreated) {
		User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
		String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
		if (userByUsername != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					String.format(baseErrorMessage, "username and the name", "are"));
		} 
	}
}
