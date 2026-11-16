package com.example.miniticketsystem.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    // Nützlich für spätere Features: Alle Tickets eines Projekts finden
    List<Ticket> findByProjectId(UUID projectId);
    java.util.List<Ticket> findByReporterUsername(String username);
}