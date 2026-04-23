package ch.uzh.ifi.hase.soprafs26.repository;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.constant.TaskStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void findById_success() {
        // given: Create parent project
        Project project = new Project();
        project.setName("Work Project");
        entityManager.persist(project);

        Task task = new Task();
        task.setName("Implement Auth");
        task.setDescription("Setup JWT");
        task.setPriority(Priority.HIGH);
        task.setStatus(TaskStatus.TODO);
        task.setProject(project);
        task.setDueDate(LocalDateTime.now().plusDays(5));

        entityManager.persist(task);
        entityManager.flush();

        // when
        Optional<Task> found = taskRepository.findById(task.getId());

        // then
        assertTrue(found.isPresent());
        assertEquals(task.getName(), found.get().getName());
        assertEquals(Priority.HIGH, found.get().getPriority());
        assertEquals(project.getId(), found.get().getProject().getId());
    }

    @Test
    public void findAll_success() {
        // given
        Task t1 = new Task();
        t1.setName("Task 1");
        entityManager.persist(t1);

        Task t2 = new Task();
        t2.setName("Task 2");
        entityManager.persist(t2);

        entityManager.flush();

        // when
        List<Task> tasks = taskRepository.findAll();

        // then
        assertEquals(2, tasks.size());
    }

    @Test
    public void updateTaskStatus_success() {
        // given
        Task task = new Task();
        task.setName("Incomplete Task");
        task.setStatus(TaskStatus.TODO);
        entityManager.persist(task);
        entityManager.flush();

        // when
        task.setStatus(TaskStatus.DONE);
        taskRepository.save(task);
        entityManager.flush();

        // then
        Task updated = entityManager.find(Task.class, task.getId());
        assertEquals(TaskStatus.DONE, updated.getStatus());
    }

    @Test
    public void deleteTask_success() {
        // given
        Task task = new Task();
        task.setName("Temporary Task");
        entityManager.persist(task);
        entityManager.flush();

        Long id = task.getId();

        // when
        taskRepository.deleteById(id);
        entityManager.flush();

        // then
        Task found = entityManager.find(Task.class, id);
        assertNull(found);
    }
}