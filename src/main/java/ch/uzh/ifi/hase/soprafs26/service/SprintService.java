package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.repository.SprintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SprintService {

	private final Logger log = LoggerFactory.getLogger(SprintService.class);

	private final SprintRepository sprintRepository;

	public SprintService(@Qualifier("sprintRepository") SprintRepository sprintRepository) {
		this.sprintRepository = sprintRepository;
	}
}
