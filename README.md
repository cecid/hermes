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
