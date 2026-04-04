package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Project;
import ch.uzh.ifi.hase.soprafs26.repository.ProjectRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {

	private final Logger log = LoggerFactory.getLogger(ProjectService.class);

	private final ProjectRepository projectRepository;
    private final UserService userService;

	public ProjectService(@Qualifier("projectRepository") ProjectRepository projectRepository, @Qualifier("userService") UserService UserService) {
		this.projectRepository = projectRepository;
        this.userService = UserService;
	}

    public List<Project> getProjects()
    {
    	return projectRepository.findAll();
    }

    public Project createProject(Project project)
    {
    	return projectRepository.save(project);
    }

    public Optional<Project> getProjectById(Long id)
    {
        return projectRepository.findById(id);
    }

    public void deleteProjectById(Long id)
    {
        projectRepository.deleteById(id);
    }

    public Project updateProject(Long id, Project newProject)
    {
        Optional<Project> oldProject = projectRepository.findById(id);
        Project updatedProject = oldProject.orElse(newProject);
        if(oldProject.isPresent())
        {
            updatedProject.setName(newProject.getName());
            updatedProject.setDescription(newProject.getDescription());
        }
        else
        {
            log.error("Project with id {} could not be found", id);
        }
        projectRepository.save(updatedProject);
        return updatedProject;
    }

    public List<Project> getProjectsByUserId(Long userId) {
    User user = userService.getUserById(userId);
    List<Project> allProjects = new ArrayList<>(user.getOwnedProjects());
    return allProjects;
}
}
