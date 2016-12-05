CREATE TABLE sfrm_message
(
  message_id varchar(200) NOT NULL,
  message_box varchar(200) NOT NULL,
  partnership_id varchar(200) NOT NULL, 
  partner_endpoint varchar(200) NOT NULL,
  total_segment int,
  total_size bigint,
  sign_algorithm varchar(200),
  encrypt_algorithm varchar(200),
  status varchar(200) NOT NULL,
  status_desc varchar(200),
  created_timestamp timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  proceed_timestamp timestamp,
  completed_timestamp timestamp,
  filename varchar(200),
  PRIMARY KEY (message_id, message_box)
);
