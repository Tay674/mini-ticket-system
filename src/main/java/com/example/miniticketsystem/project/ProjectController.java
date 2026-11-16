package com.example.miniticketsystem.project;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") // NEU
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectCreationRequest request) {
        org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        Project project = projectService.createProject(request, auth.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", project.getId(),
                "name", project.getName(),
                "owner", project.getOwner().getUsername()
        ));
    }
    
    
    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProject(@PathVariable java.util.UUID id, @Valid @RequestBody ProjectCreationRequest request) {
        Project updated = projectService.updateProject(id, request);
        return ResponseEntity.ok(Map.of("id", updated.getId(), "name", updated.getName()));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProject(@PathVariable java.util.UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
    
    
    
    @GetMapping
    public ResponseEntity<?> getMyProjects() {
        org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        return ResponseEntity.ok(projectService.getProjectsForUser(auth.getName()));
    }
}