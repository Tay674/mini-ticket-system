package com.example.miniticketsystem.ticket;

import com.example.miniticketsystem.project.Project;
import com.example.miniticketsystem.project.ProjectRepository;
import com.example.miniticketsystem.user.User;
import com.example.miniticketsystem.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, 
                         ProjectRepository projectRepository, 
                         UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Ticket createTicket(TicketCreationRequest request, String reporterUsername) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new IllegalArgumentException("Projekt nicht gefunden"));

        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new IllegalArgumentException("Reporter nicht gefunden"));

        // Neue Tickets starten immer mit LOW
        Ticket ticket = new Ticket(
                request.title(),
                request.description(),
                TicketStatus.OPEN, 
                TicketPriority.LOW, 
                project,
                reporter
        );

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket updateTicket(UUID ticketId, TicketUpdateRequest request, String currentUsername) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket nicht gefunden"));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User nicht gefunden"));

        // Priorität ändern (falls Admin dies manuell macht)
        if (request.priority() != null) {
            ticket.setPriority(request.priority());
        }

        if (request.status() != null) {
            // 1. TICKET NEHMEN (Claim)
            if (request.status() == TicketStatus.IN_PROGRESS) {
                if (ticket.getStatus() != TicketStatus.OPEN) {
                    throw new IllegalArgumentException("Nur offene Tickets können genommen werden.");
                }
                if (ticket.getAssignee() != null && !ticket.getAssignee().getId().equals(currentUser.getId())) {
                    throw new IllegalArgumentException("Das Ticket ist bereits jemand anderem zugewiesen.");
                }
                ticket.setAssignee(currentUser); // Ich übernehme das Ticket
                ticket.setStatus(TicketStatus.IN_PROGRESS);
            } 
            // 2. TICKET ABGEBEN (Release)
            else if (request.status() == TicketStatus.OPEN) {
                if (ticket.getStatus() == TicketStatus.IN_PROGRESS && ticket.getAssignee() != null && ticket.getAssignee().getId().equals(currentUser.getId())) {
                    ticket.setAssignee(null); // Zuweisung entfernen, zurück in den Pool
                    ticket.setStatus(TicketStatus.OPEN);
                } else {
                    throw new IllegalArgumentException("Nur der aktuelle Bearbeiter kann das Ticket abgeben.");
                }
            } 
            // 3. TICKET FERTIGSTELLEN (Done)
            else if (request.status() == TicketStatus.DONE) {
                if (ticket.getStatus() == TicketStatus.IN_PROGRESS && ticket.getAssignee() != null && ticket.getAssignee().getId().equals(currentUser.getId())) {
                    ticket.setResolvedBy(currentUser);
                    ticket.setResolvedAt(Instant.now());
                    ticket.setStatus(TicketStatus.DONE);
                } else {
                    throw new IllegalArgumentException("Nur der aktuelle Bearbeiter kann das Ticket abschließen.");
                }
            }
        }

        return ticketRepository.save(ticket);
    }

    // SLA-Logik: Wird jetzt auch in der Datenbank gespeichert!
    @Transactional
    public List<Ticket> getMyTickets(String username) {
        List<Ticket> tickets = ticketRepository.findByReporterUsername(username);
        applySlaPriorities(tickets);
        return tickets;
    }

    @Transactional
    public List<Ticket> getIncomingTickets(String currentUsername) {
        List<Ticket> tickets = ticketRepository.findAll().stream()
                .filter(t -> !t.getReporter().getUsername().equals(currentUsername))
                .toList();
        applySlaPriorities(tickets);
        return tickets;
    }

    @Transactional
    public List<Ticket> getTicketsForProject(UUID projectId) {
        List<Ticket> tickets = ticketRepository.findByProjectId(projectId);
        applySlaPriorities(tickets);
        return tickets;
    }

    // Die überarbeitete SLA-Magie
    private void applySlaPriorities(List<Ticket> tickets) {
        Instant now = Instant.now();
        boolean changed = false;
        
        for (Ticket t : tickets) {
            if (t.getStatus() == TicketStatus.DONE) continue;
            
            long days = Duration.between(t.getCreatedAt(), now).toDays();
            TicketPriority autoPriority = TicketPriority.LOW;
            
            if (days >= 3) {
                autoPriority = TicketPriority.HIGH;
            } else if (days >= 1) {
                autoPriority = TicketPriority.MEDIUM;
            }
            
            if (t.getPriority() != autoPriority) {
                t.setPriority(autoPriority);
                changed = true;
            }
        }
        
        // Wenn sich etwas geändert hat, speichern wir es in der DB
        if (changed) {
            ticketRepository.saveAll(tickets); 
        }
    }
}