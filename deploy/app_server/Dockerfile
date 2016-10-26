FROM ubuntu:16.04
MAINTAINER patrick.yee@gmail.com

# Update System
RUN apt-get update && apt-get install -y \
	openjdk-8-jdk \
	tomcat8 \
	maven \
	unzip \
	libmysql-java
ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk-amd64
RUN echo "export JAVA_HOME=$JAVA_HOME" >> /etc/profile

# Build Hermes
RUN mkdir -p /build
COPY . /build
RUN cd /build && \
	mvn pre-clean && \
	mvn compile && \
	mvn package -Dmaven.test.failure.ignore=true

# Set up Tomcat
COPY deploy/app_server/tomcat-users.xml /etc/tomcat8/tomcat-users.xml
RUN chown root:tomcat8 /etc/tomcat8/tomcat-users.xml
RUN chmod 640 /etc/tomcat8/tomcat-users.xml

# Deploy Hermes
RUN rm -rf /hermes_home
RUN mkdir -p /hermes_home/logs
RUN mkdir -p /tmp/h
RUN cd /tmp/h && unzip /build/target/hermes2_bin.zip
RUN rm -rf /tmp/h/plugins/corvus-sfrm
RUN rm -rf /tmp/h/plugins/corvus-as2plus-admin
RUN find /tmp/h -name *.xml -exec sed -i 's/@h2\.home@/\/hermes_home/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@as2PageletAdaptor@/hk\.hku\.cecid\.edi\.as2\.admin\.listener\.MessageHistoryPageletAdaptor/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@as2DriverClass@/com\.mysql\.jdbc\.Driver/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@as2ConnStr@/jdbc:mysql:\/\/db\/as2/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@as2user@/corvus/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@as2pw@/corvus/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@as2ValidationQuery@/SELECT now\(\)/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@as2DAOFile@/hk\/hku\/cecid\/edi\/as2\/conf\/as2.dao.xml/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@ebmsPageletAdaptor@/hk\.hku\.cecid\.ebms\.admin\.listener\.MessageHistoryPageletAdaptor/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@ebmsDriverClass@/com\.mysql\.jdbc\.Driver/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@ebmsConnStr@/jdbc:mysql:\/\/db\/ebms/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@ebmsuser@/corvus/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@ebmspw@/corvus/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@ebmsValidationQuery@/SELECT now\(\)/g' {} \;
RUN find /tmp/h -name *.xml -exec sed -i 's/@ebmsDAOFile@/hk\/hku\/cecid\/ebms\/spa\/conf\/ebms.mysql.dao.xml/g' {} \;
RUN mv /tmp/h/plugins /hermes_home
RUN cp /usr/share/java/mysql.jar /tmp/h/webapps/corvus/WEB-INF/lib/
RUN rm -rf /var/lib/tomcat8/webapps/corvus
RUN mv /tmp/h/webapps/corvus /var/lib/tomcat8/webapps
RUN chown -R tomcat8:tomcat8 /var/lib/tomcat8/webapps/corvus
RUN chown -R tomcat8:tomcat8 /hermes_home
RUN rm -rf /tmp/h
#RUN rm -rf /build

EXPOSE 8080

CMD service tomcat8 start && tail -f /var/log/tomcat8/catalina.out
