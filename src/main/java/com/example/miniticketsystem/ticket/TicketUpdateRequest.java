package com.example.miniticketsystem.ticket;

public record TicketUpdateRequest(
    TicketStatus status,
    TicketPriority priority
) {}