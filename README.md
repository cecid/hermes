# ![Hermes 2+](http://hermes.cecid.org/en/latest/_static/hermes-2-plus-logo.png)

Hermes Business Messaging Gateway is a proven open-source solution for
enterprises to automate business transactions with business partners through
secure and reliable exchange of electronic documents (e.g., purchase
orders). Hermes is secure; it allows you to encrypt and digitally sign the
documents for transmission. Hermes is reliable; the sender can automatically
retransmit a message when it is dropped in the network while the receiver can
guarantee every message is delivered once and only once, and in the right order.

## Table of Contents
**[Documentations](#documentations)** <br/>
**[Quick Start](#quick-start)** <br/>
**[Development](#development)**

## Documentations
Full documentations are available at [hermes.cecid.org](http://hermes.cecid.org/).

## Quick Start
### Install Hermes with Docker
1. Install the [Docker Engine](https://docs.docker.com/engine/installation/).
2. Run the Docker container for Hermes database (MySQL). <br/>
`docker run --name hermes_db -e MYSQL_ROOT_PASSWORD=corvus -d cecid/hermes_db:2.1`

3. Run the Docker container for Hermes application server (Tomcat). <br/>
`docker run --name hermes_app --link hermes_db:db -p 8080:8080 -d cecid/hermes_app:2.1`

4. Log in to the Hermes administration console at
`http://localhost:8080/corvus/admin/home` (username:`corvus`, password:`corvus`)
to check if Hermes is up and running.

## Development
### Compile
1. Install [Apache Maven](http://maven.apache.org/install.html)
2. Execute processes needed prior to actual project build. <br/>
`mvn pre-clean`
3. Compile Hermes.<br/>
`mvn install`
4. Locate `hermes2_installer.jar` under the `target/` directory. Install Hermes 
following the [installation guide](http://hermes.cecid.org/en/latest/installation.html).

### Java API Documentations
The Java API Documentations are available at
[javadocs.hermes.cecid.org](http://javadoc.hermes.cecid.org/)

