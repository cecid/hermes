#!/usr/bin/env bash

echo "nameserver 8.8.8.8" > /etc/resolv.conf
echo "export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64" >> /etc/profile
apt-get update
echo "mysql-server mysql-server/root_password password root" | debconf-set-selections
echo "mysql-server mysql-server/root_password_again password root" | debconf-set-selections
apt-get install -y tomcat8 maven mysql-server

# compile
cd /vagrant
sudo -u ubuntu mvn package

# setup database
