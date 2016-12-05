INSERT INTO sfrm_message VALUES('testSuspendMessage', 'OUTBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'PR', 'Processing', '2008-09-18 15:14:35.794198', '2008-09-18 15:16:35.794198', NULL, 'file.tar');
INSERT INTO sfrm_message_segment VALUES('testSuspendMessage', 'OUTBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'PD', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testSuspendMessage', 'OUTBOX', 2, 'PAYLOAD', 51, 100, 0, NULL, 'PS', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testSuspendMessage', 'OUTBOX', 3, 'PAYLOAD', 101, 150, 0, NULL, 'PD', '2008-09-18 15:20:35.794198', NULL, NULL);

INSERT INTO sfrm_message VALUES('testResumeMessage', 'OUTBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'SD', 'Processing', '2008-09-18 15:14:35.794198', '2008-09-18 15:16:35.794198', NULL, 'file.tar');
INSERT INTO sfrm_message_segment VALUES('testResumeMessage', 'OUTBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testResumeMessage', 'OUTBOX', 2, 'PAYLOAD', 51, 100, 0, NULL, 'SD', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testResumeMessage', 'OUTBOX', 3, 'PAYLOAD', 101, 150, 0, NULL, 'SD', '2008-09-18 15:20:35.794198', NULL, NULL);

INSERT INTO sfrm_message VALUES('testSuspendProcessedMessage', 'OUTBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'PS', 'Processing', '2008-09-18 15:14:35.794198', '2008-09-18 15:16:35.794198', '2008-09-18 15:16:40.794198', 'file.tar');

INSERT INTO sfrm_message VALUES('testResumeProcessedMessage', 'OUTBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'PS', 'Processing', '2008-09-18 15:14:35.794198', '2008-09-18 15:16:35.794198', '2008-09-18 15:16:40.794198', 'file.tar');

INSERT INTO sfrm_message VALUES('testSuspendSegmentingMessage', 'OUTBOX', 'CECID', 'http://localhost:8080', 5, 2048, false, null, null, null, 'ST', 'Segmenting', '2008-09-18 15:14:35.794198', '2008-09-18 15:16:35.794198', '2008-09-18 15:16:40.794198', 'file.tar');
INSERT INTO sfrm_message_segment VALUES('testSuspendSegmentingMessage', 'OUTBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testSuspendSegmentingMessage', 'OUTBOX', 2, 'PAYLOAD', 51, 100, 0, NULL, 'PR', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testSuspendSegmentingMessage', 'OUTBOX', 3, 'PAYLOAD', 101, 150, 0, NULL, 'PS', '2008-09-18 15:20:35.794198', NULL, NULL);
