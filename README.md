## H2O Development Environment

1. Install [Vagrant](https://www.vagrantup.com)
1. Install [Virtualbox](https://www.virtualbox.org)
1. Clone repository (if not already cloned)
   ```
   git clone git@gitlab.com:cecid/hermes.git
   ```
1. Start vagrant box
   ```
   cd hermes
   vagrant up
   ```
   Provision is done when `Provision done!` message is shown.
1. Hermes admin page at:
   ```
   http://localhost:18080/corvus/admin/home
   ```
   User name and password are both `corvus`


## Create and Run Docker Containers

1. Install [Docker](https://www.docker.com) and [Docker Compose](https://docs.docker.com/compose/)
1. Clone repository (if not already cloned)
   ```
   git clone git@gitlab.com:cecid/hermes.git
   ```
1. Set environment variable
   ```
   export DOCKER_HOST=unix:///var/run/docker.sock
   ```
1. Run
   ```
   cd hermes
   docker-compose -f deploy/docker-compose.yml up -d
   ```


## Create Docker Container for Hermes Database

(Optional, should be automatically built if `docker-compose` is used)

1. Install [Docker](https://www.docker.com)
1. Clone repository (if not already cloned)
   ```
   git clone git@gitlab.com:cecid/hermes.git
   ```
1. Set environment variable
   ```
   export DOCKER_HOST=unix:///var/run/docker.sock
   ```
1. Build Hermes Database image
   ```
   cd hermes
   docker build --tag "h2o/db:1.0" -f deploy/db/Dockerfile .
   ```
1. Run Docker Container for Hermes Database
   ```
   docker run --name h2o_db -e MYSQL_ROOT_PASSWORD=<ROOT_PASSWORD> -d h2o/db:1.0
   ```
   Two databases (`ebms` and `as2`) and a user (`corvus` with password `corvus`) will be created.
1. Connect to databases
   ```
   docker run -it --link h2o_db:db --rm h2o/db:1.0 mysql -hdb -P3306 -ucorvus -p ebms
   docker run -it --link h2o_db:db --rm h2o/db:1.0 mysql -hdb -P3306 -ucorvus -p as2
   ```


## Create Docker Container for Hermes Application Server

(Optional, should be automatically built if `docker-compose` is used)

1. Install [Docker](https://www.docker.com)
1. Clone repository (if not already cloned)
   ```
   git clone git@gitlab.com:cecid/hermes.git
   ```
1. Set environment variable
   ```
   export DOCKER_HOST=unix:///var/run/docker.sock
   ```
1. Build Hermes App Server image
   ```
   cd hermes
   docker build --tag "h2o/app:1.0" -f deploy/app_server/Dockerfile .
   ```
1. Run Docker Container for Hermes Database (should be built beforehand)
   ```
   docker run --name h2o_db -e MYSQL_ROOT_PASSWORD=<ROOT_PASSWORD> -d h2o/db:1.0
   ```
1. Run Docker Container for Hermes Application Server
   ```
   docker run --name h2o_app --link h2o_db:db -p 18080:8080 -d h2o/app:1.0
   ```


## Admin Page and Connect to Hermes API

1. Once Hermes server is deployed. You should be able to login to Admin page of
   Hermes and start working with it. The URL is at
   ```
   http://localhost:18080/corvus/admin/home
   ```
1. Authentication is needed to use the admin page and API. The user for accessing
   both is located at `<TOMCAT_HOME>/tomcat-users.xml`. Note that for both Vagrant
   and Docker environment, accounts have already been created in the build script.
   It can be modified if needed.
1. (Optional) The authentication setting is configured via deployment descriptor at
   `corvus-webapp/src/main/webapp/WEB-INF/web.xml`. During development, it might
   be handy to "disable" authentication on API temporarily. To do so, just comment
   out the whole `ecurity-constraint` element with web resource name as
   `Restricted API resources` at `corvus-webapp/src/main/webapp/WEB-INF/web.xml`, and
   re-deploy the `corvus` webapp at Tomcat.
1. To test the API, the simplest way is to connect to it using any API client. For
   example, `curl` can be used as a command line client. GUI based client like Postman
   is a useful tool too.
1. API for checking Hermes API server status:
   ```
   $ curl -X GET http://127.0.0.1:18080/corvus/api/status
   ```
   Response:
   ```
   {"status":"healthy","server_time":1479185615}
   ```
1. API for adding partnership:
   ```
   $ curl -X POST \
       --data '{"id":"loopback", "cpa_id":"cpa", "service":"service", "action":"action", "transport_endpoint":"http://127.0.0.1:8080/corvus/httpd/ebms/inbound"}' \
       http://127.0.0.1:18080/corvus/api/partnership/ebms
   ```
   Response:
   ```
   {"id":"loopback"}
   ```
1. API for querying partnerships:
   ```
   $ curl -X GET http://127.0.0.1:18080/corvus/api/partnership/ebms
   ```
   Response:
   ```
   {"partnerships":[{"id":"loopback","cpa_id":"cpa","service":"service","action":"action","disabled":false,"transport_endpoint":"http://127.0.0.1:8080/corvus/httpd/ebms/inbound","ack_requested":null,"signed_ack_requested":null,"duplicate_elimination":null,"message_order":null,"retries":-2147483648,"retry_interval":-2147483648,"sign_requested":false,"sign_certicate":null}]}
   ```
1. API for sending message:
   ```
   $ curl -X POST \
       --data '{"partnership_id":"loopback", "from_party_id":"from", "to_party_id":"to", "conversation_id":"conv", "payload":"dGhpcyBpcyBhIHRlc3QK"}' \
       http://127.0.0.1:18080/corvus/api/message/send/ebms
   ```
   Response:
   ```
   {"id":"20161115-053847-08213@127.0.1.1"}
   ```
1. API for checking message status:
   ```
   $ curl -X GET http://127.0.0.1:18080/corvus/api/message/send/ebms?id=20161115-053847-08213@127.0.1.1
   ```
   Response:
   ```
   {"message_id":"20161115-053847-08213@127.0.1.1","status":"DL"}
   ```
1. API for receiving message list:
   ```
   $ curl -X GET http://127.0.0.1:18080/corvus/api/message/receive/ebms?partnership_id=loopback
   ```
   Response:
   ```
   {"message_ids":[{"id":"20161115-053847-08213@127.0.1.1","timestamp":1479188327}]}
   ```
1. API for receiving a message:
   ```
   $ curl -X POST \
       --data '{"message_id":"20161115-053847-08213@127.0.1.1"}' \
       http://127.0.0.1:18080/corvus/api/message/receive/ebms
   ```
   Response:
   ```
   {"id":"20161115-053847-08213@127.0.1.1","cpa_id":"cpa","service":"service","action":"action","from_party_id":"from","to_party_id":"to","conversation_id":"conv","timestamp":1479188327,"status":"DL","payloads":[{"payload":"dGhpcyBpcyBhIHRlc3QK"}]}
   ```
