package com.example.miniticketsystem.project;

import com.example.miniticketsystem.user.User;
import com.example.miniticketsystem.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // 1. BOARD (PROJEKT) ERSTELLEN (Der Controller ruft diese auf!)
    @Transactional
    public Project createProject(ProjectCreationRequest request, String username) {
        // Den Besitzer anhand des eingeloggten Users aus dem JWT suchen
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Benutzer nicht gefunden"));

        Project newProject = new Project(
                request.name(),
                request.description(),
                owner
        );

        return projectRepository.save(newProject);
    }
    
    
    
    @Transactional
    public Project updateProject(java.util.UUID id, ProjectCreationRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Board nicht gefunden"));
        project.setName(request.name());
        project.setDescription(request.description());
        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(java.util.UUID id) {
        projectRepository.deleteById(id);
    }
    

    // 2. BOARDS LADEN (Mit Admin-Logik: Admins sehen alles, User nur ihre eigenen)
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsForUser(String username) {
        // Da wir jetzt zentrale Boards haben, dürfen ALLE User alle Boards sehen.
        List<Project> projects = projectRepository.findAll();

        return projects.stream()
                .map(p -> new ProjectResponse(p.getId(), p.getName()))
                .toList();
    }
}