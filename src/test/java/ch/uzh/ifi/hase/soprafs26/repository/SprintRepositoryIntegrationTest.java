package ch.uzh.ifi.hase.soprafs26.repository;

import ch.uzh.ifi.hase.soprafs26.constant.SprintStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Sprint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SprintRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SprintRepository sprintRepository;

    @Test
    public void findById_success() {
        // given: A project must exist first to be assigned to a sprint
        Project project = new Project();
        project.setName("Main Project");
        entityManager.persist(project);

        Sprint sprint = new Sprint();
        sprint.setName("Sprint 1");
        sprint.setSprintStatus(SprintStatus.ACTIVE);
        sprint.setProject(project);
        sprint.setStartTime(new Date());

        entityManager.persist(sprint);
        entityManager.flush();

        // when
        Optional<Sprint> found = sprintRepository.findById(sprint.getId());

        // then
        assertTrue(found.isPresent());
        assertEquals(sprint.getName(), found.get().getName());
        assertEquals(SprintStatus.ACTIVE, found.get().getSprintStatus());
        assertEquals(project.getId(), found.get().getProject().getId());
    }

    @Test
    public void save_success() {
        // given
        Sprint sprint = new Sprint();
        sprint.setName("New Sprint");
        sprint.setSprintStatus(SprintStatus.PLANNED);

        // when
        Sprint savedSprint = sprintRepository.save(sprint);
        entityManager.flush();

        // then
        assertNotNull(savedSprint.getId());
        Sprint found = entityManager.find(Sprint.class, savedSprint.getId());
        assertEquals("New Sprint", found.getName());
    }

    @Test
    public void findAll_success() {
        // given
        Sprint s1 = new Sprint();
        s1.setName("Sprint Alpha");
        entityManager.persist(s1);

        Sprint s2 = new Sprint();
        s2.setName("Sprint Beta");
        entityManager.persist(s2);

        entityManager.flush();

        // when
        List<Sprint> sprints = sprintRepository.findAll();

        // then
        assertEquals(2, sprints.size());
        assertTrue(sprints.stream().anyMatch(s -> s.getName().equals("Sprint Alpha")));
    }

    @Test
    public void updateStatus_success() {
        // given
        Sprint sprint = new Sprint();
        sprint.setName("Incomplete Sprint");
        sprint.setSprintStatus(SprintStatus.ACTIVE);
        entityManager.persist(sprint);
        entityManager.flush();

        // when
        sprint.setSprintStatus(SprintStatus.COMPLETED);
        sprintRepository.save(sprint);
        entityManager.flush();

        // then
        Sprint updated = entityManager.find(Sprint.class, sprint.getId());
        assertEquals(SprintStatus.COMPLETED, updated.getSprintStatus());
    }

    @Test
    public void delete_success() {
        // given
        Sprint sprint = new Sprint();
        sprint.setName("To Delete");
        entityManager.persist(sprint);
        entityManager.flush();

        Long id = sprint.getId();

        // when
        sprintRepository.deleteById(id);
        entityManager.flush();

        // then
        Sprint found = entityManager.find(Sprint.class, id);
        assertNull(found);
    }
}