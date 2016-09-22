## H2O Development Environment

1. Install [Vagrant](https://www.vagrantup.com)
1. Install [Virtualbox](https://www.virtualbox.org)
1. Clone repository
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


## Create Docker Container for Hermes Database

1. Install [Docker](https://www.docker.com)
1. Clone repository
   ```
   git clone git@gitlab.com:cecid/hermes.git
   ```
1. Build Hermes Database image
   ```
   cd hermes
   docker build --tag "hermesdb:1.0" -f deploy/db/Dockerfile .
   ```
1. Run Docker Container for Hermes Database
   ```
   docker run --name hermesdb -e MYSQL_ROOT_PASSWORD=<ROOT_PASSWORD> -d hermesdb:1.0
   ```
   Two databases (`ebms` and `as2`) and a user (`corvus` with password `corvus`) will be created.
1. Connect to databases
   ```
   docker run -it --link hermesdb:db --rm hermesdb:1.0 mysql -hdb -P3306 -ucorvus -p ebms
   docker run -it --link hermesdb:db --rm hermesdb:1.0 mysql -hdb -P3306 -ucorvus -p as2
   ```
