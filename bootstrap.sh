#!/usr/bin/env bash

echo "nameserver 8.8.8.8" > /etc/resolv.conf
echo "export JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64" >> /etc/profile
apt-get update
echo "mysql-server mysql-server/root_password password root" | debconf-set-selections
echo "mysql-server mysql-server/root_password_again password root" | debconf-set-selections
apt-get install -y tomcat7 maven mysql-server

# compile
cd /vagrant
sudo -u ubuntu mvn pre-clean
sudo -u ubuntu mvn clean package

# setup database
MYSQL_PWD=root mysql -u root -e "create database as2 collate=latin1_general_cs"
MYSQL_PWD=root mysql -u root -e "grant all on as2.* to 'corvus'@'localhost' identified by 'corvus'"
MYSQL_PWD=root mysql -u root -e "create database ebms collate=latin1_general_cs"
MYSQL_PWD=root mysql -u root -e "grant all on ebms.* to 'corvus'@'localhost' identified by 'corvus'"
MYSQL_PWD=corvus mysql -u corvus -f /vagrant/h2o-installer/sql/mysql_as2.sql as2
MYSQL_PWD=corvus mysql -u corvus -f /vagrant/h2o-installer/sql/mysql_ebms.sql ebms

