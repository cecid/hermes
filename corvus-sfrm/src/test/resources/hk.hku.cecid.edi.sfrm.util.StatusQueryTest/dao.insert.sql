INSERT INTO sfrm_message VALUES('testCheckMessageStatus', 'OUTBOX', 'loopback', 'http://localhost:8080', 3, 150, false, null, null, null, 'PR', 'Message is Processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');
INSERT INTO sfrm_message_segment VALUES('testCheckMessageStatus', 'OUTBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'PS', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testCheckMessageStatus', 'OUTBOX', 2, 'PAYLOAD', 51, 100, 0, NULL, 'PS', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testCheckMessageStatus', 'OUTBOX', 3, 'PAYLOAD', 101, 150, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);

INSERT INTO sfrm_message VALUES('testCheckMessageStatusNotStarted', 'OUTBOX', 'loopback', 'http://localhost:8080', 1, 50, false, null, null, null, 'PR', 'Message is Processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');
INSERT INTO sfrm_message_segment VALUES('testCheckMessageStatusNotStarted', 'OUTBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'PR', '2008-09-18 15:20:35.794198', NULL, NULL);
