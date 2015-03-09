CREATE TABLE users (
	id SERIAL PRIMARY KEY NOT NULL,
	name char(56) NOT NULL,
	password char(32) NOT NULL,
	email char(320) NOT NULL,
	created_at TIMESTAMP,
	updated_at TIMESTAMP
);
