package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.ProjectRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest(properties = "HUGGINGFACE_API_TOKEN=mock-key")
public class ProjectServiceIntegrationTest {

    @Qualifier("projectRepository")
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    private User persistedOwner;
    private User persistedMember;

    @BeforeEach
    public void setup() {
        projectRepository.deleteAll();
        userRepository.deleteAll();

        // Persist a reusable owner user
        persistedOwner = new User();
        persistedOwner.setUsername("ownerUser_" + System.nanoTime());
        persistedOwner.setPassword("password");
        persistedOwner.setEmail("owner@uzh.ch");
        persistedOwner.setLanguage("German");
        persistedOwner.setManager(true);
        persistedOwner.setName("Owner");
        persistedOwner.setToken("321321321");
        persistedOwner.setStatus(UserStatus.ONLINE);
        persistedOwner = userRepository.save(persistedOwner);

        // Persist a reusable member user
        persistedMember = new User();
        persistedMember.setUsername("memberUser_" + System.nanoTime());
        persistedMember.setPassword("password");
        persistedMember.setEmail("member@uzh.ch");
        persistedMember.setLanguage("German");
        persistedMember.setManager(false);
        persistedMember.setName("Member");
        persistedMember.setToken("123123123");
        persistedMember.setStatus(UserStatus.ONLINE);
        persistedMember = userRepository.save(persistedMember);
    }

    @Test
    public void createProject_validInputs_success() {
        assertFalse(projectRepository.findById(1L).isPresent());

        Project testProject = new Project();
        testProject.setName("testName");
        testProject.setDescription("testDescription");

        Project createdProject = projectService.createProject(testProject, null, null, null);

        assertEquals(testProject.getId(), createdProject.getId());
        assertEquals("testName", createdProject.getName());
        assertEquals("testDescription", createdProject.getDescription());
    }

    @Test
    public void createProject_withOwner_ownerIsSet() {
        Project testProject = new Project();
        testProject.setName("projectWithOwner");
        testProject.setDescription("desc");

        Project created = projectService.createProject(testProject, null, persistedOwner.getId(), null);

        assertNotNull(created.getOwner());
        assertEquals(persistedOwner.getId(), created.getOwner().getId());
    }

    @Test
    public void createProject_withMembers_membersAreSet() {
        Project testProject = new Project();
        testProject.setName("projectWithMembers");
        testProject.setDescription("desc");

        Project created = projectService.createProject(
                testProject, Arrays.asList(persistedMember.getId()), null, null);

        assertNotNull(created.getMembers());
        assertEquals(1, created.getMembers().size());
        assertEquals(persistedMember.getId(), created.getMembers().get(0).getId());
    }

    @Test
    public void createProject_withOwnerAndMembers_allRelationsSet() {
        Project testProject = new Project();
        testProject.setName("fullProject");
        testProject.setDescription("desc");

        Project created = projectService.createProject(
                testProject, Arrays.asList(persistedMember.getId()), persistedOwner.getId(), "DE");

        assertEquals(persistedOwner.getId(), created.getOwner().getId());
        assertEquals(1, created.getMembers().size());
        assertEquals(persistedMember.getId(), created.getMembers().get(0).getId());
    }

    @Test
    public void getProjects_multipleProjects_returnsAll() {
        Project p1 = new Project();
        p1.setName("project1");
        p1.setDescription("desc1");
        projectService.createProject(p1, null, null,  null);

        Project p2 = new Project();
        p2.setName("project2");
        p2.setDescription("desc2");
        projectService.createProject(p2, null, null,   null);

        List<Project> projects = projectService.getProjects();

        assertEquals(2, projects.size());
    }

    @Test
    public void getProjects_emptyRepository_returnsEmptyList() {
        List<Project> projects = projectService.getProjects();
        assertTrue(projects.isEmpty());
    }

    @Test
    public void getProjectById_existingId_returnsProject() {
        Project testProject = new Project();
        testProject.setName("fetchMe");
        testProject.setDescription("desc");
        Project saved = projectService.createProject(testProject, null, null, null);

        Optional<Project> found = projectService.getProjectById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    public void getProjectById_nonExistingId_returnsEmpty() {
        Optional<Project> found = projectService.getProjectById(9999L);
        assertFalse(found.isPresent());
    }

    @Test
    public void deleteProjectById_existingProject_isDeleted() {
        Project testProject = new Project();
        testProject.setName("toDelete");
        testProject.setDescription("desc");
        Project saved = projectService.createProject(testProject, null, null, null);
        Long id = saved.getId();

        projectService.deleteProjectById(id);

        assertFalse(projectRepository.findById(id).isPresent());
    }

    @Test
    public void updateProject_validInput_fieldsAreUpdated() {
        Project testProject = new Project();
        testProject.setName("original");
        testProject.setDescription("originalDesc");
        Project saved = projectService.createProject(testProject, null, null, null);

        Project updateInput = new Project();
        updateInput.setName("updated");
        updateInput.setDescription("updatedDesc");

        Project updated = projectService.updateProject(saved.getId(), updateInput, null);

        assertEquals("updated", updated.getName());
        assertEquals("updatedDesc", updated.getDescription());
    }

    @Test
    public void updateProject_withNewMembers_memberListReplaced() {
        Project testProject = new Project();
        testProject.setName("p");
        testProject.setDescription("d");
        Project saved = projectService.createProject(testProject, null, null, null);

        Project updateInput = new Project();
        updateInput.setName("p");
        updateInput.setDescription("d");

        Project updated = projectService.updateProject(
                saved.getId(), updateInput, Arrays.asList(persistedMember.getId()));

        assertEquals(1, updated.getMembers().size());
        assertEquals(persistedMember.getId(), updated.getMembers().get(0).getId());
    }

    @Test
    public void updateProject_nonExistingId_throwsNotFound() {
        Project updateInput = new Project();
        updateInput.setName("x");
        updateInput.setDescription("y");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> projectService.updateProject(9999L, updateInput, null)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void getProjectsByUserId_ownedProject_isReturned() {
        Project testProject = new Project();
        testProject.setName("owned");
        testProject.setDescription("desc");
        projectService.createProject(testProject, null, persistedOwner.getId(), null);

        List<Project> results = projectService.getProjectsByUserId(persistedOwner.getId());

        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> "owned".equals(p.getName())));
    }

    @Test
    public void getProjectsByUserId_memberProject_isReturned() {
        Project testProject = new Project();
        testProject.setName("memberOf");
        testProject.setDescription("desc");
        projectService.createProject(
                testProject, Arrays.asList(persistedMember.getId()), null, null);

        List<Project> results = projectService.getProjectsByUserId(persistedMember.getId());

        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> "memberOf".equals(p.getName())));
    }

    @Test
    public void getProjectsByUserId_ownerAndMemberSameProject_noDuplicate() {
        // User is both owner and member of the same project
        Project testProject = new Project();
        testProject.setName("dual");
        testProject.setDescription("desc");
        projectService.createProject(
                testProject, Arrays.asList(persistedOwner.getId()), persistedOwner.getId(), null);

        List<Project> results = projectService.getProjectsByUserId(persistedOwner.getId());

        long count = results.stream().filter(p -> "dual".equals(p.getName())).count();
        assertEquals(1, count, "Project should not appear more than once");
    }
}