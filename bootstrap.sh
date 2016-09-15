#!/usr/bin/env bash

# install packages
echo "nameserver 8.8.8.8" > /etc/resolv.conf
apt-get update
echo "mysql-server mysql-server/root_password password root" | debconf-set-selections
echo "mysql-server mysql-server/root_password_again password root" | debconf-set-selections
apt-get install -y openjdk-8-jdk tomcat8 maven mysql-server libmysql-java
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
echo "export JAVA_HOME=$JAVA_HOME" >> /etc/profile

# compile
cd /vagrant
sudo -u ubuntu mvn pre-clean
sudo -u ubuntu mvn compile
sudo -u ubuntu mvn package

# setup database
MYSQL_PWD=root mysql -u root -e "create database if not exists as2 collate=latin1_general_cs"
MYSQL_PWD=root mysql -u root -e "grant all on as2.* to 'corvus'@'localhost' identified by 'corvus'"
MYSQL_PWD=root mysql -u root -e "create database if not exists ebms collate=latin1_general_cs"
MYSQL_PWD=root mysql -u root -e "grant all on ebms.* to 'corvus'@'localhost' identified by 'corvus'"
MYSQL_PWD=corvus mysql -u corvus as2 < /vagrant/h2o-installer/sql/mysql_as2.sql
MYSQL_PWD=corvus mysql -u corvus ebms < /vagrant/h2o-installer/sql/mysql_ebms.sql

# deploy
service tomcat8 stop
mv /etc/tomcat8/tomcat-users.xml /etc/tomcat8/tomcat-users.xml.bak
cat <<EOF > /etc/tomcat8/tomcat-users.xml
<?xml version='1.0' encoding='utf-8'?>
<tomcat-users>
  <role rolename="tomcat"/>
  <role rolename="admin"/>
  <user username="corvus" password="corvus" roles="tomcat,admin"/>
</tomcat-users>
EOF
chown root:tomcat8 /etc/tomcat8/tomcat-users.xml
chmod 640 /etc/tomcat8/tomcat-users.xml
rm -rf /home/vagrant/hermes_home
mkdir -p /home/vagrant/hermes_home/logs
mkdir -p /tmp/h
cd /tmp/h
unzip /vagrant/target/hermes2_bin.zip
rm -rf /tmp/h/plugins/corvus-sfrm
rm -rf /tmp/h/plugins/corvus-as2plus-admin
find . -name *.xml -exec sed -i 's/@h2\.home@/\/home\/vagrant\/hermes_home/g' {} \;
find . -name *.xml -exec sed -i 's/@as2PageletAdaptor@/hk\.hku\.cecid\.edi\.as2\.admin\.listener\.MessageHistoryPageletAdaptor/g' {} \;
find . -name *.xml -exec sed -i 's/@as2DriverClass@/com\.mysql\.jdbc\.Driver/g' {} \;
find . -name *.xml -exec sed -i 's/@as2ConnStr@/jdbc:mysql:\/\/127\.0\.0\.1\/as2/g' {} \;
find . -name *.xml -exec sed -i 's/@as2user@/corvus/g' {} \;
find . -name *.xml -exec sed -i 's/@as2pw@/corvus/g' {} \;
find . -name *.xml -exec sed -i 's/@as2ValidationQuery@/SELECT now\(\)/g' {} \;
find . -name *.xml -exec sed -i 's/@as2DAOFile@/hk\/hku\/cecid\/edi\/as2\/conf\/as2.dao.xml/g' {} \;
find . -name *.xml -exec sed -i 's/@ebmsPageletAdaptor@/hk\.hku\.cecid\.ebms\.admin\.listener\.MessageHistoryPageletAdaptor/g' {} \;
find . -name *.xml -exec sed -i 's/@ebmsDriverClass@/com\.mysql\.jdbc\.Driver/g' {} \;
find . -name *.xml -exec sed -i 's/@ebmsConnStr@/jdbc:mysql:\/\/127\.0\.0\.1\/ebms/g' {} \;
find . -name *.xml -exec sed -i 's/@ebmsuser@/corvus/g' {} \;
find . -name *.xml -exec sed -i 's/@ebmspw@/corvus/g' {} \;
find . -name *.xml -exec sed -i 's/@ebmsValidationQuery@/SELECT now\(\)/g' {} \;
find . -name *.xml -exec sed -i 's/@ebmsDAOFile@/hk\/hku\/cecid\/ebms\/spa\/conf\/ebms.mysql.dao.xml/g' {} \;
cd
mv /tmp/h/plugins /home/vagrant/hermes_home
cp /usr/share/java/mysql.jar /tmp/h/webapps/corvus/WEB-INF/lib/
rm -rf /var/lib/tomcat8/webapps/corvus
mv /tmp/h/webapps/* /var/lib/tomcat8/webapps
chown -R tomcat8:tomcat8 /var/lib/tomcat8/webapps/*
chown -R tomcat8:tomcat8 /home/vagrant/hermes_home
rm -rf /tmp/h
service tomcat8 start
echo "Provision done!"
