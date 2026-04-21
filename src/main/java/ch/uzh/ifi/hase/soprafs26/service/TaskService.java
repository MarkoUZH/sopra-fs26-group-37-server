package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

	private final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final UserRepository userRepository;
	private final TaskRepository taskRepository;

	public TaskService(@Qualifier("taskRepository") TaskRepository taskRepository, @Qualifier("userRepository") UserRepository userRepository   ) {
		this.taskRepository = taskRepository;
        this.userRepository = userRepository;   
	}

    public List<Task> getTasks()
    {
        return taskRepository.findAll();
    }

    public Task createTask(Task task)
    {
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id)
    {
        return taskRepository.findById(id);
    }

    public void deleteTaskById(Long id)
    {
        taskRepository.deleteById(id);
    }

   public Task updateTask(Long id, Task newTask) {
    Task existingTask = taskRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

    existingTask.setStatus(newTask.getStatus()); 
    existingTask.setName(newTask.getName());
    existingTask.setDescription(newTask.getDescription());
    existingTask.setPriority(newTask.getPriority());
    existingTask.setDueDate(newTask.getDueDate());
    existingTask.setTimeEstimate(newTask.getTimeEstimate());

    return taskRepository.save(existingTask);
}

}
