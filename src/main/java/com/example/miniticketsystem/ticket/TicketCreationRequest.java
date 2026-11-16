package com.example.miniticketsystem.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TicketCreationRequest(
	    @NotBlank(message = "Titel darf nicht leer sein") String title,
	    String description,
	    @NotNull(message = "Projekt-ID muss angegeben werden") UUID projectId
	) {}