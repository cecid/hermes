CREATE TABLE repository (
	message_id varchar NOT NULL,
	message_box varchar NOT NULL,
	content VARBINARY NOT NULL,
	PRIMARY KEY (message_id, message_box)
);