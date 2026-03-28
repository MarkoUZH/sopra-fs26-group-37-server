package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProjectService {

	private final Logger log = LoggerFactory.getLogger(ProjectService.class);

	private final ProjectRepository projectRepository;

	public ProjectService(@Qualifier("projectRepository") ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}
}
