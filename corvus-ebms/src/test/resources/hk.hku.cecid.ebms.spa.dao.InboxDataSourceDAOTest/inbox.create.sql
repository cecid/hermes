CREATE TABLE inbox(
message_id varchar,
order_no bigint,
PRIMARY KEY (message_id)
);

CREATE SEQUENCE inbox_order_no_seq;