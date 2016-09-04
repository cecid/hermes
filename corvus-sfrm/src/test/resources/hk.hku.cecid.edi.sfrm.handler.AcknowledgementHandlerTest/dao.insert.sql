INSERT INTO sfrm_message VALUES('testUpdateMessageSD', 'OUTBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PSD', 'Message Pre-suspended', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');

INSERT INTO sfrm_message VALUES('testUpdateMessagePS', 'OUTBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PPS', 'Message Pre-processed', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');

INSERT INTO sfrm_message VALUES('testUpdateMessageDF', 'OUTBOX', 'loopback', 'http://localhost:8080', 1, 1024, false, null, null, null, 'PR', 'Message processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');

INSERT INTO sfrm_message VALUES('testUpdateMessageSegmentForPRMessage', 'OUTBOX', 'loopback', 'http://localhost:8080', 3, 150, false, null, null, null, 'PR', 'Message processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');

INSERT INTO sfrm_message_segment VALUES('testUpdateMessageSegmentForPRMessage', 'OUTBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testUpdateMessageSegmentForPRMessage', 'OUTBOX', 2, 'PAYLOAD', 51, 100, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testUpdateMessageSegmentForPRMessage', 'OUTBOX', 3, 'PAYLOAD', 101, 150, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);

INSERT INTO sfrm_message VALUES('testMarkMessagePreCompleted', 'OUTBOX', 'loopback', 'http://localhost:8080', 3, 150, false, null, null, null, 'PR', 'Message processing', '2008-09-18 15:21:35.794198', '2008-09-18 15:21:55.794198', NULL, 'file.tar');
INSERT INTO sfrm_message_segment VALUES('testMarkMessagePreCompleted', 'OUTBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'PS', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testMarkMessagePreCompleted', 'OUTBOX', 2, 'PAYLOAD', 51, 100, 0, NULL, 'PS', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testMarkMessagePreCompleted', 'OUTBOX', 3, 'PAYLOAD', 101, 150, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);

