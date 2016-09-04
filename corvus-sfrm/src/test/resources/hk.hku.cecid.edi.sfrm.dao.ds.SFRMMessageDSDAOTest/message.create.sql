CREATE TABLE sfrm_message
(
  message_id varchar NOT NULL,
  message_box varchar NOT NULL,
  partnership_id varchar NOT NULL, 
  partner_endpoint varchar NOT NULL,
  total_segment int,
  total_size bigint,
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