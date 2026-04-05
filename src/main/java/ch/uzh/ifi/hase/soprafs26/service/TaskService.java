package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

	private final Logger log = LoggerFactory.getLogger(TaskService.class);

	private final TaskRepository taskRepository;

	public TaskService(@Qualifier("taskRepository") TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
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

    public Task updateTask(Long id, Task newTask)
    {
        Optional<Task> oldTask = taskRepository.findById(id);
        Task updatedTask = oldTask.orElse(newTask);
        if(oldTask.isPresent())
        {
            updatedTask.setName(oldTask.get().getName());
            updatedTask.setDescription(oldTask.get().getDescription());
            updatedTask.setAcceptanceCriteria(oldTask.get().getAcceptanceCriteria());
            updatedTask.setAssignedUsers(oldTask.get().getAssignedUsers());
            updatedTask.setPriority(oldTask.get().getPriority());
            updatedTask.setTags(oldTask.get().getTags());
            updatedTask.setDueDate(oldTask.get().getDueDate());
            updatedTask.setSprint(oldTask.get().getSprint());
            updatedTask.setTimeEstimate(oldTask.get().getTimeEstimate());
        }
        else
        {
            log.error("Task with id {} could not be found", id);
        }
        taskRepository.save(updatedTask);
        return updatedTask;
    }

}
