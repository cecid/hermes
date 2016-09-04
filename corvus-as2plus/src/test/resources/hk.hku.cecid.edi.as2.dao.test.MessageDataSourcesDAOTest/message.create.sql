CREATE TABLE message (
	message_id varchar NOT NULL,
	message_box varchar NOT NULL,
	as2_from varchar NOT NULL,
	as2_to varchar NOT NULL,
	is_receipt varchar,
	is_acknowledged varchar,
	is_receipt_requested varchar,
	receipt_url varchar,
	mic_value varchar,
	original_message_id varchar,
	time_stamp timestamp NOT NULL,
	status varchar NOT NULL,
	status_desc varchar,
	PRIMARY KEY (message_id, message_box)
);