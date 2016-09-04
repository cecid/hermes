CREATE TABLE sfrm_message
(
  message_id varchar NOT NULL,
  message_box varchar NOT NULL,
  partnership_id varchar NOT NULL, 
  partner_endpoint varchar NOT NULL,
  total_segment int,
  total_size bigint,
  is_hostname_verified boolean,
  partner_cert_content varchar,
  sign_algorithm varchar,
  encrypt_algorithm varchar,
  status varchar NOT NULL,
  status_desc varchar,
  created_timestamp timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  proceed_timestamp timestamp,
  completed_timestamp timestamp,
  filename varchar,
  PRIMARY KEY (message_id, message_box)
);

CREATE TABLE sfrm_message_segment 
(
   message_id varchar NOT NULL,
   message_box varchar NOT NULL,
   segment_no int NOT NULL,
   segment_type varchar NOT NULL,
   segment_start bigint,
   segment_end bigint,
   retried int DEFAULT -1,
   md5_value varchar,
   status varchar,
   created_timestamp timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
   proceed_timestamp timestamp,
   completed_timestamp timestamp,
   PRIMARY KEY (message_id, message_box, segment_no, segment_type)
);

CREATE TABLE sfrm_partnership
(
  partnership_seq int DEFAULT 1,
  partnership_id varchar(50) NOT NULL,
  description varchar,
  partner_endpoint varchar NOT NULL,
  partner_cert_fingerprint varchar,
  is_hostname_verified boolean DEFAULT FALSE,
  sign_algorithm varchar,
  encrypt_algorithm varchar,
  retry_max int DEFAULT 3,
  retry_interval int DEFAULT 30000,
  is_disabled boolean DEFAULT FALSE NOT NULL,  
  created_timestamp timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified_timestamp timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  UNIQUE (partnership_id),
  PRIMARY KEY (partnership_seq)
);