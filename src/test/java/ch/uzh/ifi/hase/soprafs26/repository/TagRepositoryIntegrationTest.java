package ch.uzh.ifi.hase.soprafs26.repository;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TagRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TagRepository tagRepository;

    @Test
    public void findById_success() {
        // given: A tag needs an associated project
        Project project = new Project();
        project.setName("Test Project");
        entityManager.persist(project);

        Tag tag = new Tag();
        tag.setName("Backend");
        tag.setProject(project);

        entityManager.persist(tag);
        entityManager.flush();

        // when
        Optional<Tag> found = tagRepository.findById(tag.getId());

        // then
        assertTrue(found.isPresent());
        assertEquals(tag.getId(), found.get().getId());
        assertEquals("Backend", found.get().getName());
        assertEquals(project.getId(), found.get().getProject().getId());
    }

    @Test
    public void save_success() {
        // given
        Tag tag = new Tag();
        tag.setName("Frontend");

        // when
        Tag savedTag = tagRepository.save(tag);
        entityManager.flush();

        // then
        assertNotNull(savedTag.getId());
        Tag found = entityManager.find(Tag.class, savedTag.getId());
        assertEquals("Frontend", found.getName());
    }

    @Test
    public void findAll_success() {
        // given
        Tag t1 = new Tag();
        t1.setName("Bug");
        entityManager.persist(t1);

        Tag t2 = new Tag();
        t2.setName("Feature");
        entityManager.persist(t2);

        entityManager.flush();

        // when
        List<Tag> tags = tagRepository.findAll();

        // then
        assertEquals(2, tags.size());
        assertTrue(tags.stream().anyMatch(t -> t.getName().equals("Bug")));
    }

    @Test
    public void delete_success() {
        // given
        Tag tag = new Tag();
        tag.setName("Temporary");
        entityManager.persist(tag);
        entityManager.flush();

        Long id = tag.getId();

        // when
        tagRepository.deleteById(id);
        entityManager.flush();

        // then
        Tag found = entityManager.find(Tag.class, id);
        assertNull(found);
    }
}