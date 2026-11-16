package com.example.miniticketsystem.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectCreationRequest(
    @NotBlank(message = "Projektname darf nicht leer sein")
    @Size(max = 100, message = "Projektname darf maximal 100 Zeichen lang sein")
    String name,

    String description
    // ownerId wurde entfernt! Das macht jetzt Spring Security.
) {}