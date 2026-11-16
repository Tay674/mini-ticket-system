CREATE TABLE tickets (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    project_id UUID NOT NULL,
    reporter_id UUID NOT NULL,
    assignee_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    
    CONSTRAINT fk_ticket_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_reporter FOREIGN KEY (reporter_id) REFERENCES users(id),
    CONSTRAINT fk_ticket_assignee FOREIGN KEY (assignee_id) REFERENCES users(id)
);