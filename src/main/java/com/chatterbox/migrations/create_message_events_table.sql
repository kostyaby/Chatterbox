CREATE TABLE message_events (
	id SERIAL PRIMARY KEY NOT NULL,
	message_id INT NOT NULL REFERENCES messages(id),
	event_type TEXT NOT NULL,
	created_at TIMESTAMP,
	updated_at TIMESTAMP
);
