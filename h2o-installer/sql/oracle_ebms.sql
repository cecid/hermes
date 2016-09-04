CREATE TABLE message (
	message_id varchar2(255),
	message_box varchar2(255),
	message_type varchar2(255),
	from_party_id varchar2(255),
	from_party_role varchar2(255),
	to_party_id varchar2(255),
	to_party_role varchar2(255),
	cpa_id varchar2(255),
	service varchar2(255),
	action varchar2(255),
	conv_id varchar2(255),
	ref_to_message_id varchar2(255),-- message_id of the message that the response replies to 
	primal_message_id varchar2(255),-- message_id of message which triggered "Resend as New Message"
	has_resend_as_new varchar2(5),
	partnership_id varchar2(255),
	sync_reply varchar2(5),
	dup_elimination varchar2(5),
	ack_requested varchar2(5),
	ack_sign_requested varchar2(5),
	sequence_no integer,
	sequence_status integer,
	sequence_group integer,
	time_to_live timestamp,
	time_stamp timestamp,
	timeout_time_stamp timestamp,
	status varchar2(2),
	status_description varchar2(4000),
	PRIMARY KEY (message_id, message_box)
);

CREATE TABLE repository (
	message_id varchar2(255),
	content_type varchar2(255),
	content blob,
	time_stamp timestamp,
	message_box varchar2(255),
	PRIMARY KEY (message_id, message_box)
);

CREATE TABLE outbox (
	message_id varchar2(255),
	retried integer,
	PRIMARY KEY (message_id)
);

CREATE TABLE inbox (
	message_id varchar2(255),
	order_no number,
	PRIMARY KEY (message_id)
);

CREATE TABLE partnership (
	partnership_id varchar2(255),
	cpa_id varchar2(255),
	service varchar2(255),
	action varchar2(255),
	transport_protocol varchar2(255),
	transport_endpoint varchar2(1000),
	is_hostname_verified varchar2(5),
	sync_reply_mode varchar2(20),
	ack_requested varchar2(20),
	ack_sign_requested varchar2(20),
	dup_elimination varchar2(20),
	actor varchar2(255),
	disabled varchar2(5),
	retries integer,
	retry_interval integer,
	persist_duration varchar2(255),
	message_order varchar2(13),
	sign_requested varchar2(5),
	sign_cert blob,
	ds_algorithm varchar2(255),
	md_algorithm varchar2(255),
	encrypt_requested varchar2(5),
	encrypt_cert blob,
	encrypt_algorithm varchar2(5),
	PRIMARY KEY (partnership_id)
);

CREATE SEQUENCE inbox_order_no_seq;
