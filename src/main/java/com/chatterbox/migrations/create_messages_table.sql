CREATE TABLE messages (
	id SERIAL PRIMARY KEY NOT NULL,
	user_id INT NOT NULL REFERENCES users(id),
	content TEXT NOT NULL,
	created_at TIMESTAMP,
	updated_at TIMESTAMP
);
