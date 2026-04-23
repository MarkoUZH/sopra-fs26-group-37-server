package ch.uzh.ifi.hase.soprafs26.repository;

import ch.uzh.ifi.hase.soprafs26.constant.Priority;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import ch.uzh.ifi.hase.soprafs26.entity.Task;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProjectRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void findById_success() {
        // given
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("A test description");
        project.setTasks(new ArrayList<>());
        project.setSprints(new ArrayList<>());

        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        task.setOriginalLanguage("EN");
        task.setPriority(Priority.MEDIUM);

        Sprint sprint = new Sprint();
        sprint.setName("Sprint 1");

        project.getTasks().add(task);
        project.getSprints().add(sprint);

        entityManager.persist(project);
        entityManager.flush();

        // when
        Optional<Project> found = projectRepository.findById(project.getId());

        // then
        assertTrue(found.isPresent());
        assertEquals(project.getId(), found.get().getId());
        assertEquals(project.getName(), found.get().getName());
        assertEquals(project.getDescription(), found.get().getDescription());
        assertEquals(project.getTasks().size(), found.get().getTasks().size());
        assertEquals(project.getSprints().size(), found.get().getSprints().size());
    }

    @Test
    public void findById_notFound() {
        // when
        Optional<Project> found = projectRepository.findById(-1L);

        // then
        assertFalse(found.isPresent());
    }

    @Test
    public void findAll_success() {
        // given
        Project p1 = new Project();
        p1.setName("Project 1");
        entityManager.persist(p1);

        Project p2 = new Project();
        p2.setName("Project 2");
        entityManager.persist(p2);

        entityManager.flush();

        // when
        List<Project> projects = projectRepository.findAll();

        // then
        assertEquals(2, projects.size());
        assertTrue(projects.stream().anyMatch(p -> p.getName().equals("Project 1")));
        assertTrue(projects.stream().anyMatch(p -> p.getName().equals("Project 2")));
    }

    @Test
    public void save_success() {
        // given
        Project project = new Project();
        project.setName("New Project");
        project.setDescription("Creation test");
        project.setTasks(new ArrayList<>());
        project.setOwner(createMockUser());

        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setTimeEstimate(1.0f);
        task.setDueDate(LocalDateTime.of(2026,1,1,1,1,0));
        task.setOriginalLanguage("EN");
        task.setPriority(Priority.MEDIUM);

        project.getTasks().add(task);

        // when
        Project savedProject = projectRepository.save(project);

        // then
        assertNotNull(savedProject.getId());
        Project found = entityManager.find(Project.class, savedProject.getId());
        assertEquals("New Project", found.getName());
    }

    @Test
    public void update_success() {
        // given
        Project project = new Project();
        project.setName("Original Name");
        entityManager.persist(project);
        entityManager.flush();

        // when
        project.setName("Updated Name");
        projectRepository.save(project);
        entityManager.flush();

        // then
        Project updated = entityManager.find(Project.class, project.getId());
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    public void delete_success() {
        // given
        Project project = new Project();
        project.setName("To be deleted");
        entityManager.persist(project);
        entityManager.flush();

        Long id = project.getId();

        // when
        projectRepository.deleteById(id);
        entityManager.flush();

        // then
        Project found = entityManager.find(Project.class, id);
        assertNull(found);
    }

    public static User createMockUser(){
        User user = new User();
        user.setUsername("loginUser");
        user.setPassword("password");
        user.setEmail("email");
        user.setToken("token");
        user.setManager(true);

        return user;
    }
}