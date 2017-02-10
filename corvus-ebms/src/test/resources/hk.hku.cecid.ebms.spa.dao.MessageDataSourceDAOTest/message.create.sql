CREATE TABLE message (
	message_id varchar(200),
	message_box varchar(200),
	message_type varchar(200),
	from_party_id varchar(200),
	from_party_role varchar(200),
	to_party_id varchar(200),
	to_party_role varchar(200),
	cpa_id varchar(200),
	service varchar(200),
	action varchar(200),
	conv_id varchar(200),
	ref_to_message_id varchar(200),
	sync_reply varchar(200),
	dup_elimination varchar(200),
	ack_requested varchar(200),
	ack_sign_requested varchar(200),
	sequence_no integer,
	sequence_status integer,
	sequence_group integer,
	time_to_live timestamp,
	time_stamp timestamp,
	timeout_time_stamp timestamp,
	status varchar(200),
	status_description varchar(200),
	PRIMARY KEY (message_id, message_box)
);

CREATE TABLE inbox (
	message_id varchar(200),
	order_no bigint,
	PRIMARY KEY (message_id)
);
