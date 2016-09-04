CREATE TABLE message (
	message_id varchar,
	message_box varchar,
	message_type varchar,
	from_party_id varchar,
	from_party_role varchar,
	to_party_id varchar,
	to_party_role varchar,
	cpa_id varchar,
	service varchar,
	action varchar,
	conv_id varchar,
	ref_to_message_id varchar,
	sync_reply varchar,
	dup_elimination varchar,
	ack_requested varchar,
	ack_sign_requested varchar,
	sequence_no integer,
	sequence_status integer,
	sequence_group integer,
	time_to_live timestamp,
	time_stamp timestamp,
	timeout_time_stamp timestamp,
	status varchar,
	status_description varchar,
	PRIMARY KEY (message_id, message_box)
);

CREATE TABLE inbox (
	message_id varchar,
	order_no bigint,
	PRIMARY KEY (message_id)
);
