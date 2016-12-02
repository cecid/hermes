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
	ref_to_message_id varchar(200),-- message_id of the message that the response replies to 
	primal_message_id varchar(200),-- message_id of message which triggered "Resend as New Message"
	has_resend_as_new varchar(200),
	partnership_id varchar(200),
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

CREATE TABLE repository (
	message_id varchar(200),
	content_type varchar(200),
	content binary,
	time_stamp timestamp,
	message_box varchar(200),
	PRIMARY KEY (message_id, message_box)
);

CREATE TABLE outbox (
	message_id varchar(200),
	retried integer,
	PRIMARY KEY (message_id)
);

CREATE TABLE inbox (
	message_id varchar(200),
	order_no bigint,
	PRIMARY KEY (message_id)
);

CREATE TABLE partnership (
	partnership_id varchar(200),
	cpa_id varchar(200),
	service varchar(200),
	action varchar(200),
	transport_protocol varchar(200),
	transport_endpoint varchar(200),
	is_hostname_verified varchar(200),
	sync_reply_mode varchar(200),
	ack_requested varchar(200),
	ack_sign_requested varchar(200),
	dup_elimination varchar(200),
	actor varchar(200),
	disabled varchar(200),
	retries integer,
	retry_interval integer,
	persist_duration varchar(200),
	message_order varchar(200),
	sign_requested varchar(200),
	sign_cert binary,
	ds_algorithm varchar(200),
	md_algorithm varchar(200),
	encrypt_requested varchar(200),
	encrypt_cert binary,
	encrypt_algorithm varchar(200),
	PRIMARY KEY (partnership_id)
);

CREATE SEQUENCE inbox_order_no_seq;
