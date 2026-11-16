package com.example.miniticketsystem.ticket;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // 1. TICKET ERSTELLEN (Das hat gefehlt!)
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketCreationRequest request) {
        org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        Ticket ticket = ticketService.createTicket(request, auth.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", ticket.getId(),
                "title", ticket.getTitle(),
                "status", ticket.getStatus(),
                "priority", ticket.getPriority(),
                "project", ticket.getProject().getName(),
                "reporter", ticket.getReporter().getUsername()
        ));
    }

    // 2. TICKET BEARBEITEN (Status / Zuweisung)
    @PutMapping("/{ticketId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTicket(@PathVariable UUID ticketId, @RequestBody TicketUpdateRequest request) {
        org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        // Übergebe auth.getName() an den Service
        Ticket updatedTicket = ticketService.updateTicket(ticketId, request, auth.getName());
        
        return ResponseEntity.ok(Map.of(
                "id", updatedTicket.getId(),
                "title", updatedTicket.getTitle(),
                "status", updatedTicket.getStatus(),
                "assignee", updatedTicket.getAssignee() != null ? updatedTicket.getAssignee().getUsername() : "Niemand",
                "resolvedBy", updatedTicket.getResolvedBy() != null ? updatedTicket.getResolvedBy().getUsername() : ""
        ));
    }
    // 3. TICKETS LADEN (Angepasst, damit das Frontend alle Infos bekommt)
    @GetMapping
    public ResponseEntity<?> getTicketsByProject(@RequestParam UUID projectId) {
        var tickets = ticketService.getTicketsForProject(projectId);
        
        // Wir nutzen hier HashMap statt Map.of, weil Map.of keine null-Werte erlaubt (assignee kann null sein!)
        var response = tickets.stream().map(ticket -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ticket.getId());
            map.put("title", ticket.getTitle());
            map.put("description", ticket.getDescription() != null ? ticket.getDescription() : "");
            map.put("status", ticket.getStatus().toString());
            map.put("priority", ticket.getPriority().toString());
            map.put("assignee", ticket.getAssignee() != null ? ticket.getAssignee().getUsername() : "");
            map.put("resolvedBy", ticket.getResolvedBy() != null ? ticket.getResolvedBy().getUsername() : "");
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyTickets() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        var tickets = ticketService.getMyTickets(auth.getName());
        
        return ResponseEntity.ok(mapTicketsToJson(tickets));
    }

    @GetMapping("/incoming")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getIncomingTickets() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        var tickets = ticketService.getIncomingTickets(auth.getName());
        
        return ResponseEntity.ok(mapTicketsToJson(tickets));
    }

    // Hilfsmethode, damit wir den Code nicht doppelt schreiben müssen
    private java.util.List<java.util.Map<String, Object>> mapTicketsToJson(java.util.List<Ticket> tickets) {
        return tickets.stream().map(ticket -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", ticket.getId());
            map.put("title", ticket.getTitle());
            map.put("description", ticket.getDescription() != null ? ticket.getDescription() : "");
            map.put("status", ticket.getStatus().toString());
            map.put("priority", ticket.getPriority().toString());
            map.put("assignee", ticket.getAssignee() != null ? ticket.getAssignee().getUsername() : "");
            map.put("resolvedBy", ticket.getResolvedBy() != null ? ticket.getResolvedBy().getUsername() : "");
            map.put("reporter", ticket.getReporter().getUsername()); // Wichtig für das Frontend!
            map.put("project", ticket.getProject().getName());
            return map;
        }).toList();
    }
}