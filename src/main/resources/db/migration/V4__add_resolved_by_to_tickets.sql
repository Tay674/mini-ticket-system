ALTER TABLE tickets ADD COLUMN resolved_by UUID;
ALTER TABLE tickets ADD COLUMN resolved_at TIMESTAMP WITH TIME ZONE;

ALTER TABLE tickets 
ADD CONSTRAINT fk_ticket_resolver 
FOREIGN KEY (resolved_by) REFERENCES users(id);