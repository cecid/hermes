INSERT INTO sfrm_message_segment VALUES('a', 'OUTBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('a', 'OUTBOX', 2, 'PAYLOAD', 51, 100, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('a', 'OUTBOX', 3, 'PAYLOAD', 101, 150, 0, NULL, 'DF', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('a', 'OUTBOX', 4, 'PAYLOAD', 151, 200, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);

INSERT INTO sfrm_message_segment VALUES('b', 'OUTBOX', 2, 'PAYLOAD', 0, 50, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);

INSERT INTO sfrm_message_segment VALUES('c', 'OUTBOX', 1, 'PAYLOAD', 0, 50, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('c', 'OUTBOX', 2, 'PAYLOAD', 51, 100, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('c', 'OUTBOX', 3, 'PAYLOAD', 101, 150, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('c', 'OUTBOX', 4, 'PAYLOAD', 151, 200, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);

INSERT INTO sfrm_message_segment VALUES('testFindNumOfSegmentByMessageIdAndBoxAndTypeAndStatues', 'OUTBOX', 1, 'PAYLOAD', 1, 50, 0, NULL, 'DL', '2008-09-18 15:20:35.794198', NULL, NULL);
INSERT INTO sfrm_message_segment VALUES('testFindNumOfSegmentByMessageIdAndBoxAndTypeAndStatues', 'OUTBOX', 2, 'PAYLOAD', 51, 100, 0, NULL, 'DL', '2008-09-20 15:20:35.794198', '2008-09-20 15:20:35.794198', NULL);
INSERT INTO sfrm_message_segment VALUES('testFindNumOfSegmentByMessageIdAndBoxAndTypeAndStatues', 'OUTBOX', 3, 'PAYLOAD', 101, 150, 0, NULL, 'PS', '2008-09-20 15:20:35.794198', '2008-09-20 15:20:35.794198', NULL);
INSERT INTO sfrm_message_segment VALUES('testFindNumOfSegmentByMessageIdAndBoxAndTypeAndStatues', 'OUTBOX', 4, 'PAYLOAD', 151, 200, 0, NULL, 'DL', '2008-09-20 15:20:35.794198', '2008-09-18 15:20:35.794198', NULL);