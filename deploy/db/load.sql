CREATE DATABASE ebms;
GRANT ALL ON ebms.* to 'corvus'@'%' IDENTIFIED BY 'corvus';
USE ebms;
SOURCE /build/ebms.sql

CREATE DATABASE as2;
GRANT ALL ON as2.* to 'corvus'@'%' IDENTIFIED BY 'corvus';
USE as2;
SOURCE /build/as2.sql
