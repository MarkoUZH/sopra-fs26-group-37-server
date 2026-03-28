package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

	private final Logger log = LoggerFactory.getLogger(TaskService.class);

	private final TaskRepository taskRepository;

	public TaskService(@Qualifier("taskRepository") TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}
}
