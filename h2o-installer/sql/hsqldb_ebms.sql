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
	ref_to_message_id varchar,-- message_id of the message that the response replies to 
	primal_message_id varchar,-- message_id of message which triggered "Resend as New Message"
	has_resend_as_new varchar,
	partnership_id varchar,
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

CREATE TABLE repository (
	message_id varchar,
	content_type varchar,
	content binary,
	time_stamp timestamp,
	message_box varchar,
	PRIMARY KEY (message_id, message_box)
);

CREATE TABLE outbox (
	message_id varchar,
	retried integer,
	PRIMARY KEY (message_id)
);

CREATE TABLE inbox (
	message_id varchar,
	order_no bigint,
	PRIMARY KEY (message_id)
);

CREATE TABLE partnership (
	partnership_id varchar,
	cpa_id varchar,
	service varchar,
	action varchar,
	transport_protocol varchar,
	transport_endpoint varchar,
	is_hostname_verified varchar,
	sync_reply_mode varchar,
	ack_requested varchar,
	ack_sign_requested varchar,
	dup_elimination varchar,
	actor varchar,
	disabled varchar,
	retries integer,
	retry_interval integer,
	persist_duration varchar,
	message_order varchar,
	sign_requested varchar,
	sign_cert binary,
	ds_algorithm varchar,
	md_algorithm varchar,
	encrypt_requested varchar,
	encrypt_cert binary,
	encrypt_algorithm varchar,
	PRIMARY KEY (partnership_id)
);

CREATE SEQUENCE inbox_order_no_seq;
