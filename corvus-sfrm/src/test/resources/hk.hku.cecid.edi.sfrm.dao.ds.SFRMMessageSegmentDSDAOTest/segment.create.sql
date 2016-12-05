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
