CREATE TABLE sfrm_message_segment 
(
   message_id varchar(200) NOT NULL,
   message_box varchar(200) NOT NULL,
   segment_no int NOT NULL,
   segment_type varchar(200) NOT NULL,
   segment_start bigint,
   segment_end bigint,
   retried int DEFAULT -1,
   md5_value varchar(200),
   status varchar(200),
   created_timestamp timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
   proceed_timestamp timestamp,
   completed_timestamp timestamp,
   PRIMARY KEY (message_id, message_box, segment_no, segment_type)
);
