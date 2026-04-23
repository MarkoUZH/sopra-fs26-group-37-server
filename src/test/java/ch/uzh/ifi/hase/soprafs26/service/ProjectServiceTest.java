package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private User testUser;
    private User testMember;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("testProject");
        testProject.setDescription("testProjectDescription");

        testUser = new User();
        testUser.setId(10L);
        testUser.setUsername("ownerUser");

        testMember = new User();
        testMember.setId(20L);
        testMember.setUsername("memberUser");

        Mockito.when(projectRepository.save(Mockito.any())).thenReturn(testProject);
    }

    @Test
    public void getProjects_returnsAllProjects() {
        List<Project> projects = Arrays.asList(testProject, new Project());
        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> result = projectService.getProjects();

        assertEquals(2, result.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void getProjects_emptyRepository_returnsEmptyList() {
        when(projectRepository.findAll()).thenReturn(new ArrayList<>());

        List<Project> result = projectService.getProjects();

        assertTrue(result.isEmpty());
    }

    @Test
    public void createProject_noOwnerNoMembers_success() {
        Project created = projectService.createProject(testProject, null, null, null);

        verify(projectRepository, times(1)).save(Mockito.any());
        assertEquals(testProject.getId(), created.getId());
        assertEquals(testProject.getName(), created.getName());
        assertEquals(testProject.getDescription(), created.getDescription());
    }

    @Test
    public void createProject_withOwner_setsOwnerAndSaves() {
        when(userService.getUserById(10L)).thenReturn(testUser);

        Project created = projectService.createProject(testProject, null, 10L, null);

        verify(userService, times(1)).getUserById(10L);
        verify(projectRepository, times(1)).save(Mockito.any());
        assertEquals(testUser, testProject.getOwner());
        assertEquals(testProject.getId(), created.getId());
    }

    @Test
    public void createProject_withMembers_setsMembersAndSaves() {
        when(userService.getUserById(20L)).thenReturn(testMember);

        Project created = projectService.createProject(testProject, Arrays.asList(20L), null, null);

        verify(userService, times(1)).getUserById(20L);
        verify(projectRepository, times(1)).save(Mockito.any());
        assertTrue(testProject.getMembers().contains(testMember));
    }

    @Test
    public void createProject_withOwnerAndMembers_setsAllAndSaves() {
        when(userService.getUserById(10L)).thenReturn(testUser);
        when(userService.getUserById(20L)).thenReturn(testMember);

        Project created = projectService.createProject(testProject, Arrays.asList(20L), 10L, "German");

        verify(userService, times(1)).getUserById(10L);
        verify(userService, times(1)).getUserById(20L);
        verify(projectRepository, times(1)).save(Mockito.any());
        assertEquals(testUser, testProject.getOwner());
        assertTrue(testProject.getMembers().contains(testMember));
    }


    @Test
    public void getProjectById_existingId_returnsProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        Optional<Project> result = projectService.getProjectById(1L);

        assertTrue(result.isPresent());
        assertEquals(testProject.getId(), result.get().getId());
    }

    @Test
    public void getProjectById_nonExistingId_returnsEmpty() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Project> result = projectService.getProjectById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    public void deleteProjectById_callsRepositoryDelete() {
        doNothing().when(projectRepository).deleteById(1L);

        projectService.deleteProjectById(1L);

        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    public void updateProject_validId_updatesFieldsAndSaves() {
        Project updatedInput = new Project();
        updatedInput.setName("updatedName");
        updatedInput.setDescription("updatedDescription");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(testProject)).thenReturn(testProject);

        Project result = projectService.updateProject(1L, updatedInput, null);

        assertEquals("updatedName", result.getName());
        assertEquals("updatedDescription", result.getDescription());
        verify(projectRepository, times(1)).save(testProject);
    }

    @Test
    public void updateProject_withNewMembers_updatesMemberList() {
        Project updatedInput = new Project();
        updatedInput.setName("updatedName");
        updatedInput.setDescription("updatedDescription");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(testProject)).thenReturn(testProject);
        when(userService.getUserById(20L)).thenReturn(testMember);

        Project result = projectService.updateProject(1L, updatedInput, Arrays.asList(20L));

        assertTrue(result.getMembers().contains(testMember));
        verify(userService, times(1)).getUserById(20L);
    }

    @Test
    public void updateProject_withEmptyMemberList_clearsMembership() {
        Project updatedInput = new Project();
        updatedInput.setName("updatedName");
        updatedInput.setDescription("updatedDescription");

        testProject.setMembers(new ArrayList<>(Arrays.asList(testMember)));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(testProject)).thenReturn(testProject);

        Project result = projectService.updateProject(1L, updatedInput, new ArrayList<>());

        assertTrue(result.getMembers().isEmpty());
    }

    @Test
    public void updateProject_nonExistingId_throwsNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        Project input = new Project();
        input.setName("project");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> projectService.updateProject(99L, input, null)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void getProjectsByUserId_ownerOnly_returnsOwnedProjects() {
        Project ownedProject = new Project();
        ownedProject.setId(2L);

        testUser.setOwnedProjects(Arrays.asList(ownedProject));
        testUser.setProjects(new ArrayList<>());
        when(userService.getUserById(10L)).thenReturn(testUser);

        List<Project> result = projectService.getProjectsByUserId(10L);

        assertEquals(1, result.size());
        assertTrue(result.contains(ownedProject));
    }

    @Test
    public void getProjectsByUserId_memberOnly_returnsMemberProjects() {
        Project memberProject = new Project();
        memberProject.setId(3L);

        testUser.setOwnedProjects(new ArrayList<>());
        testUser.setProjects(Arrays.asList(memberProject));
        when(userService.getUserById(10L)).thenReturn(testUser);

        List<Project> result = projectService.getProjectsByUserId(10L);

        assertEquals(1, result.size());
        assertTrue(result.contains(memberProject));
    }

    @Test
    public void getProjectsByUserId_ownerAndMember_noDuplicates() {
        Project sharedProject = new Project();
        sharedProject.setId(4L);

        testUser.setOwnedProjects(new ArrayList<>(Arrays.asList(sharedProject)));
        testUser.setProjects(Arrays.asList(sharedProject));
        when(userService.getUserById(10L)).thenReturn(testUser);

        List<Project> result = projectService.getProjectsByUserId(10L);

        assertEquals(1, result.size(), "Duplicate project should appear only once");
    }

    @Test
    public void getProjectsByUserId_ownerAndDistinctMember_returnsBoth() {
        Project ownedProject = new Project();
        ownedProject.setId(5L);
        Project memberProject = new Project();
        memberProject.setId(6L);

        testUser.setOwnedProjects(new ArrayList<>(Arrays.asList(ownedProject)));
        testUser.setProjects(Arrays.asList(memberProject));
        when(userService.getUserById(10L)).thenReturn(testUser);

        List<Project> result = projectService.getProjectsByUserId(10L);

        assertEquals(2, result.size());
        assertTrue(result.contains(ownedProject));
        assertTrue(result.contains(memberProject));
    }

    @Test
    public void getProjectsByUserId_nullMemberProjects_returnsOwnedOnly() {
        Project ownedProject = new Project();
        ownedProject.setId(7L);

        testUser.setOwnedProjects(Arrays.asList(ownedProject));
        testUser.setProjects(null);
        when(userService.getUserById(10L)).thenReturn(testUser);

        List<Project> result = projectService.getProjectsByUserId(10L);

        assertEquals(1, result.size());
        assertTrue(result.contains(ownedProject));
    }
}