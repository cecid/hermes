INSERT INTO sfrm_message VALUES('A', 'OUTBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'PR', 'Processing', '2008-09-18 15:14:35.794198', '2008-09-18 15:16:35.794198', NULL, 'file.tar');
INSERT INTO sfrm_message VALUES('A', 'INBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'PR', 'Processing', '2008-09-18 15:14:37.794198', '2008-09-18 15:14:40.794198', NULL, 'file.tar');
INSERT INTO sfrm_message VALUES('B', 'OUTBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'PPS', 'Pre-Completed', '2008-09-18 15:17:35.794198', '2008-09-18 15:18:35.794198', NULL, 'file.tar');
INSERT INTO sfrm_message VALUES('C', 'OUTBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'PSD', 'Pre-Suspended', '2008-09-18 15:19:35.794198', '2008-09-18 15:19:55.794198', NULL, 'file.tar');
INSERT INTO sfrm_message VALUES('D', 'OUTBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'PDF', 'Pre-Delivery Failed', '2008-09-18 15:20:35.794198', '2008-09-18 15:20:55.794198', NULL, 'file.tar');
INSERT INTO sfrm_message VALUES('E', 'OUTBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'PRS', 'Pre-Resume', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');

INSERT INTO sfrm_message VALUES('testSegmentReceived', 'INBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PR', 'Message Processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');
INSERT INTO sfrm_message VALUES('testSegmentReceivedInvalidCRC', 'INBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PR', 'Message Processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');


INSERT INTO sfrm_partnership VALUES (1, 'loopback', 'loopback 1231', 'http://localhost:8080', NULL, false, null, null, 10, 4000, false, '2009-02-16 17:54:32.25', '2009-02-16 17:54:32.25');

INSERT INTO sfrm_message_segment VALUES('abc', 'OUTBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'PS', '2008-09-18 15:20:35.794198', NULL, NULL);

INSERT INTO sfrm_message VALUES('testProcessPRAck', 'INBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PR', 'Message Processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');
INSERT INTO sfrm_message_segment VALUES('testProcessPRAck', 'INBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testProcessPRAck', 'INBOX', 2, 'PAYLOAD', 51, 100, 0, NULL, 'DF', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testProcessPRAck', 'INBOX', 3, 'PAYLOAD', 101, 150, 0, NULL, 'PR', '2008-09-18 15:20:35.794198', NULL, NULL);

INSERT INTO sfrm_message VALUES('testProcessPDFAck', 'INBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PR', 'Message Processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');

INSERT INTO sfrm_message VALUES('testProcessPPSAck', 'INBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PR', 'Message Processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');

INSERT INTO sfrm_message VALUES('testProcessPSDAck', 'INBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PR', 'Message Processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');

INSERT INTO sfrm_message VALUES('testProcessPRSAck', 'INBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PR', 'Message Processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');

INSERT INTO sfrm_message VALUES('MSGS_SUSPENDED', 'INBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PR', 'Message Processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');
