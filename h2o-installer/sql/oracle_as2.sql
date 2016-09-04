CREATE TABLE partnership (
  partnership_id varchar2(255) NOT NULL,
  subject varchar2(255),
  recipient_address varchar2(1000),
  receipt_address varchar2(1000),
  is_sync_reply varchar2(5),
  is_receipt_requested varchar2(5),
  is_outbound_sign_required varchar2(5),
  is_outbound_encrypt_required varchar2(5),
  is_outbound_compress_required varchar2(5),
  is_receipt_sign_required varchar2(5),
  is_inbound_sign_required varchar2(5),
  is_inbound_encrypt_required varchar2(5),
  sign_algorithm varchar2(5),
  encrypt_algorithm varchar2(5),
  mic_algorithm varchar2(5),
  as2_from varchar2(255) NOT NULL,
  as2_to varchar2(255) NOT NULL,
  encrypt_cert blob,
  verify_cert blob,
  retries number,
  retry_interval number,
  is_disabled varchar2(5) NOT NULL,
  is_hostname_verified varchar2(5),
  PRIMARY KEY (partnership_id)
);

CREATE TABLE message (
  message_id varchar2(255) NOT NULL,
  message_box varchar2(255) NOT NULL,
  as2_from varchar2(255) NOT NULL,
  as2_to varchar2(255) NOT NULL,
  is_receipt varchar2(5),
  is_acknowledged varchar2(5),
  is_receipt_requested varchar2(5),
  receipt_url varchar2(1000),
  mic_value varchar2(255),
  original_message_id varchar2(255),
  time_stamp timestamp NOT NULL,
  principal_id varchar2(40),
  status varchar2(2) NOT NULL,
  status_desc varchar2(4000),
  PRIMARY KEY (message_id, message_box)
);

CREATE TABLE repository (
  message_id varchar2(255) NOT NULL,
  message_box varchar2(255) NOT NULL,
  content blob NOT NULL,
  PRIMARY KEY (message_id, message_box)
);

