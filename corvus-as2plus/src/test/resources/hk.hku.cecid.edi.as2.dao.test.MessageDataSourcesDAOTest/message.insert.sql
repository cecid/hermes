// MessageDataSourceDAOTest
//Insert OUTBOX Message
INSERT INTO message (message_id, message_box, as2_from, as2_to,  is_receipt, is_acknowledged, is_receipt_requested, receipt_url, mic_value, original_message_id, time_stamp, status, status_desc)
VALUES ('20090910-120000-12345@127.0.0.1', 'OUT', 'as2From', 'as2To', false, '', false, '"http://127.0.0.1:8080/corvus/httpd/as2/inbound"','','', '2008-01-07 12:08:11.79', 'DL', 'Message was sent.');
INSERT INTO message (message_id, message_box, as2_from, as2_to,  is_receipt, is_acknowledged, is_receipt_requested, receipt_url, mic_value, original_message_id, time_stamp, status, status_desc)
VALUES ('20090910-120000-12346@127.0.0.1', 'OUT', 'as2From', 'as2To', false, '', false, '"http://127.0.0.1:8080/corvus/httpd/as2/inbound"','','', '2008-01-07 12:08:11.79', 'PS', 'automatic-action/mdn-sent-automatically; processed');
INSERT INTO message (message_id, message_box, as2_from, as2_to,  is_receipt, is_acknowledged, is_receipt_requested, receipt_url, mic_value, original_message_id, time_stamp, status, status_desc)
VALUES ('20090910-120000-12347@127.0.0.1', 'OUT', 'as2From', 'as2To', false, '', false, '"http://127.0.0.1:8080/corvus/httpd/as2/inbound"','','', '2008-01-07 12:08:11.79', 'PS', 'automatic-action/mdn-sent-automatically; processed');
INSERT INTO message (message_id, message_box, as2_from, as2_to,  is_receipt, is_acknowledged, is_receipt_requested, receipt_url, mic_value, original_message_id, time_stamp, status, status_desc)
VALUES ('20090910-120000-12348@127.0.0.1', 'OUT', 'as2From', 'as2To', false, '', false, '"http://127.0.0.1:8080/corvus/httpd/as2/inbound"','','', '2008-01-07 12:08:11.79', 'DF', 'automatic-action/MDN-sent-automatically; processed/error: authentication-failed');
INSERT INTO message (message_id, message_box, as2_from, as2_to,  is_receipt, is_acknowledged, is_receipt_requested, receipt_url, mic_value, original_message_id, time_stamp, status, status_desc)
VALUES ('20090910-120000-12349@127.0.0.1', 'OUT', 'as2From', 'as2To', false, '', false, '"http://127.0.0.1:8080/corvus/httpd/as2/inbound"','','', '2008-01-07 12:08:11.79', 'PD', 'Message is pending to send.');

INSERT INTO message (message_id, message_box, as2_from, as2_to,  is_receipt, is_acknowledged, is_receipt_requested, receipt_url, mic_value, original_message_id, time_stamp, status, status_desc)
VALUES ('20090910-120000-23456@127.0.0.1', 'IN', 'as2From', 'as2To', false, '', false, '"http://127.0.0.1:8080/corvus/httpd/as2/inbound"','','', '2008-01-07 12:08:11.79', 'DL', 'Message was sent.');
INSERT INTO message (message_id, message_box, as2_from, as2_to,  is_receipt, is_acknowledged, is_receipt_requested, receipt_url, mic_value, original_message_id, time_stamp, status, status_desc)
VALUES ('20090910-120000-23457@127.0.0.1', 'IN', 'as2From', 'as2To', false, '', false, '"http://127.0.0.1:8080/corvus/httpd/as2/inbound"','','', '2008-01-07 12:08:11.79', 'PS', 'automatic-action/mdn-sent-automatically; processed');
INSERT INTO message (message_id, message_box, as2_from, as2_to,  is_receipt, is_acknowledged, is_receipt_requested, receipt_url, mic_value, original_message_id, time_stamp, status, status_desc)
VALUES ('20090910-120000-23458@127.0.0.1', 'IN', 'as2From', 'as2To', false, '', false, '"http://127.0.0.1:8080/corvus/httpd/as2/inbound"','','', '2008-01-07 12:08:11.79', 'PS', 'automatic-action/mdn-sent-automatically; processed');
INSERT INTO message (message_id, message_box, as2_from, as2_to,  is_receipt, is_acknowledged, is_receipt_requested, receipt_url, mic_value, original_message_id, time_stamp, status, status_desc)
VALUES ('20090910-120000-23459@127.0.0.1', 'IN', 'as2From', 'as2To', false, '', false, '"http://127.0.0.1:8080/corvus/httpd/as2/inbound"','','', '2008-01-07 12:08:11.79',  'DF', 'automatic-action/MDN-sent-automatically; processed/error: authentication-failed');
INSERT INTO message (message_id, message_box, as2_from, as2_to,  is_receipt, is_acknowledged, is_receipt_requested, receipt_url, mic_value, original_message_id, time_stamp, status, status_desc)
VALUES ('20090910-120000-23460@127.0.0.1', 'IN', 'as2From', 'as2To', false, '', false, '"http://127.0.0.1:8080/corvus/httpd/as2/inbound"','','', '2008-01-07 12:08:11.79',  'PD', 'Message is pending to send.');