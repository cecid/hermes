CREATE TABLE IF NOT EXISTS message (
	message_id varchar(200) NOT NULL,
	message_box varchar(200) NOT NULL,
	as2_from varchar(200) NOT NULL,
	as2_to varchar(200) NOT NULL,
	is_receipt varchar(200),
	is_acknowledged varchar(200),
	is_receipt_requested varchar(200),
	receipt_url varchar(200),
	mic_value varchar(200),
	original_message_id varchar(200),
	time_stamp timestamp NOT NULL,
	status varchar(200) NOT NULL,
	status_desc varchar(200),
	PRIMARY KEY (message_id, message_box)
);
