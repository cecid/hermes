CREATE TABLE partnership (
	partnership_id varchar(200) NOT NULL,
	subject varchar(200),
	recipient_address varchar(200),
	receipt_address varchar(200),
	is_sync_reply varchar(200),
	is_receipt_requested varchar(200),
	is_outbound_sign_required varchar(200),
	is_outbound_encrypt_required varchar(200),
	is_outbound_compress_required varchar(200),
	is_receipt_sign_required varchar(200),
	is_inbound_sign_required varchar(200),
	is_inbound_encrypt_required varchar(200),
	sign_algorithm varchar(200),
	encrypt_algorithm varchar(200),
	mic_algorithm varchar(200),
	as2_from varchar(200) NOT NULL,
	as2_to varchar(200) NOT NULL,
	encrypt_cert BINARY,//  HSQL Specified
	verify_cert BINARY, // HSQL Specified
	retries INTEGER,
	retry_interval INTEGER,
	is_disabled varchar(200) NOT NULL,
	is_hostname_verified varchar(200),
	PRIMARY KEY (partnership_id)
);

CREATE TABLE message (
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
	primal_message_id varchar(200),-- The message_id of message which triggered "Resend as New Message"
	has_resend_as_new varchar(200),
	partnership_id varchar(200) NOT NULL,
	time_stamp timestamp NOT NULL,
	status varchar(200) NOT NULL,
	status_desc varchar(200),
	PRIMARY KEY (message_id, message_box)
);

CREATE TABLE repository (
	message_id varchar(200) NOT NULL,
	message_box varchar(200) NOT NULL,
	content BINARY NOT NULL, // HSQL Specified
	PRIMARY KEY (message_id, message_box)
);

CREATE TABLE raw_repository (
	message_id varchar(200) NOT NULL,
	content BINARY NOT NULL, // HSQL Specified
	PRIMARY KEY (message_id)
);