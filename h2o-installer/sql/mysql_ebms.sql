CREATE TABLE message (
	message_id varchar(255),
	message_box varchar(255),
	message_type varchar(255),
	from_party_id varchar(255),
	from_party_role varchar(255),
	to_party_id varchar(255),
	to_party_role varchar(255),
	cpa_id varchar(255),
	service varchar(255),
	action varchar(255),
	conv_id varchar(255),
	ref_to_message_id varchar(255),-- message_id of the message that the response replies to 
	primal_message_id varchar(255),-- message_id of message which triggered "Resend as New Message"
	has_resend_as_new varchar(5),
	partnership_id varchar(255),
	sync_reply varchar(5),
	dup_elimination varchar(5),
	ack_requested varchar(5),
	ack_sign_requested varchar(5),
	sequence_no integer,
	sequence_status integer,
	sequence_group integer,
	time_to_live timestamp null default null,
	time_stamp timestamp null default null,
	timeout_time_stamp timestamp null default null,
	status varchar(2),
	status_description varchar(4000),
	PRIMARY KEY (message_id, message_box)
)TYPE= INNODB;

CREATE TABLE repository (
	message_id varchar(255),
	content_type varchar(255),
	content LONGBLOB,
	time_stamp timestamp null default null,
	message_box varchar(255),
	PRIMARY KEY (message_id, message_box)
)TYPE= INNODB;

CREATE TABLE outbox (
	message_id varchar(255),
	retried integer,
	PRIMARY KEY (message_id)
)TYPE= INNODB;

CREATE TABLE inbox (
	message_id varchar(255),
	order_no bigint,
	PRIMARY KEY (message_id)
)TYPE= INNODB;

CREATE TABLE partnership (
	partnership_id varchar(255),
	cpa_id varchar(255),
	service varchar(255),
	action varchar(255),
	transport_protocol varchar(255),
	transport_endpoint varchar(1000),
	is_hostname_verified varchar(5),
	sync_reply_mode varchar(20),
	ack_requested varchar(20),
	ack_sign_requested varchar(20),
	dup_elimination varchar(20),
	actor varchar(255),
	disabled varchar(5),
	retries integer,
	retry_interval integer,
	persist_duration varchar(255),
	message_order varchar(13),
	sign_requested varchar(5),
	sign_cert LONGBLOB,
	ds_algorithm varchar(255),
	md_algorithm varchar(255),
	encrypt_requested varchar(5),
	encrypt_cert LONGBLOB,
	encrypt_algorithm varchar(5),
	PRIMARY KEY (partnership_id)
)TYPE= INNODB;
