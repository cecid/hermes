CREATE TABLE partnership (
	partnership_id varchar(255) NOT NULL,
	subject varchar(255),
	recipient_address varchar(1000),
	receipt_address varchar(1000),
	is_sync_reply varchar(5),
	is_receipt_requested varchar(5),
	is_outbound_sign_required varchar(5),
	is_outbound_encrypt_required varchar(5),
	is_outbound_compress_required varchar(5),
	is_receipt_sign_required varchar(5),
	is_inbound_sign_required varchar(5),
	is_inbound_encrypt_required varchar(5),
	sign_algorithm varchar(5),
	encrypt_algorithm varchar(5),
	mic_algorithm varchar(5),
	as2_from varchar(255) NOT NULL,
	as2_to varchar(255) NOT NULL,
	encrypt_cert LONGBLOB,
	verify_cert LONGBLOB,
	retries integer,
	retry_interval integer,
	is_disabled varchar(5) NOT NULL,
	is_hostname_verified varchar(5),
	PRIMARY KEY (partnership_id)
)TYPE= INNODB;

CREATE TABLE message (
	message_id varchar(255) NOT NULL,
	message_box varchar(50) NOT NULL,
	as2_from varchar(255) NOT NULL,
	as2_to varchar(255) NOT NULL,
	is_receipt varchar(5),
	is_acknowledged varchar(5),
	is_receipt_requested varchar(5),
	receipt_url varchar(1000),
	mic_value varchar(255),
	original_message_id varchar(255), -- message_id of the message that the receipt replies to 
	primal_message_id varchar(255),-- The message_id of message which triggered "Resend as New Message"
	has_resend_as_new varchar(5),
	partnership_id varchar(255),
	time_stamp timestamp NOT NULL,
	status varchar(2) NOT NULL,
	status_desc varchar(4000),
	PRIMARY KEY (message_id, message_box)
)TYPE= INNODB;

CREATE TABLE repository (
	message_id varchar(255) NOT NULL,
	message_box varchar(50) NOT NULL,
	content LONGBLOB NOT NULL,
	PRIMARY KEY (message_id, message_box)

)TYPE= INNODB;

CREATE TABLE raw_repository (
	message_id varchar(255) NOT NULL,
	content LONGBLOB NOT NULL,
	PRIMARY KEY (message_id)
)TYPE= INNODB;