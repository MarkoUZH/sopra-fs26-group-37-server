package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.TagRepository;
import ch.uzh.ifi.hase.soprafs26.repository.TaskRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.TaskMessage;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.TaskDTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;


    @Autowired
    public TaskService(@Qualifier("taskRepository") TaskRepository taskRepository,
                       @Qualifier("userRepository") UserRepository userRepository,
                       @Qualifier("tagRepository") TagRepository tagRepository,
                       @Qualifier("userService") UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    // --- RESTORED CORE METHODS FOR CONTROLLER ---

    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public void deleteTaskById(Long id) {
        Task taskToDelete = taskRepository.findById(id).orElse(null);
        taskToDelete.setSprint(null);
        taskToDelete.setAssignedUsers(null);
        taskToDelete.setProject(null);
        taskRepository.save(taskToDelete);
        taskRepository.delete(taskToDelete);
        broadcast("task_deleted", Map.of("id", id));
    }

    // --- CREATE LOGIC WITH TAG CONNECTION ---

    public Task createTask(Task task, String token) {
        User user = userService.getUserByToken(token);
        task.setOriginalLanguage(user.getLanguage());

        // Sync tags if they were provided in the DTO
        if (task.getTags() != null && !task.getTags().isEmpty()) {
            List<Long> tagIds = task.getTags().stream().map(Tag::getId).collect(Collectors.toList());
            List<Tag> realTags = tagRepository.findAllById(tagIds);
            task.setTags(realTags);
            // Since Tag is the owner, we update the relationship
            for (Tag tag : realTags) {
                if (!tag.getTasks().contains(task)) {
                    tag.getTasks().add(task);
                }
            }
        }

        Task createdTask = taskRepository.save(task);
        broadcast("task_created", TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(createdTask));

        return createdTask;
    }

    // --- UPDATE LOGIC WITH TAG CONNECTION ---

    public Task updateTask(Long id, Task newTask) {
        Task existingTask = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        // Update basic fields
        existingTask.setStatus(newTask.getStatus()); 
        existingTask.setName(newTask.getName());
        existingTask.setDescription(newTask.getDescription());
        existingTask.setPriority(newTask.getPriority());
        existingTask.setDueDate(newTask.getDueDate());
        existingTask.setTimeEstimate(newTask.getTimeEstimate());
        
        // Handle Assigned Users
        existingTask.setAssignedUsers(newTask.getAssignedUsers());

        // Handle Tags (The Connection)
        if (newTask.getTags() != null) {
            // Remove task from old tags first (cleanup)
            if (existingTask.getTags() != null) {
                for (Tag oldTag : existingTask.getTags()) {
                    oldTag.getTasks().remove(existingTask);
                }
            }

            // Link to new 'Real' tags
            List<Long> tagIds = newTask.getTags().stream().map(Tag::getId).collect(Collectors.toList());
            List<Tag> realTags = tagRepository.findAllById(tagIds);
            
            existingTask.setTags(realTags);
            for (Tag tag : realTags) {
                if (!tag.getTasks().contains(existingTask)) {
                    tag.getTasks().add(existingTask);
                }
            }
        }

        Task updatedTask = taskRepository.save(existingTask);
        broadcast("task_updated", TaskDTOMapper.INSTANCE.convertEntityToTaskGetDTO(updatedTask));

        return updatedTask;
    }

    private void broadcast(String type, Object payload) {
        messagingTemplate.convertAndSend("/topic/tasks", new TaskMessage(type, payload));
    }
}