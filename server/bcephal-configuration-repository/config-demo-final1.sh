#!/bin/bash

INSTALL_TYPE=DEMO
INSTALL_VERSION=2.0.0

YAML_BCEPHAL_NAME=bcephal-compose-v8-demo.yaml
YAML_TRAEFIK_NAME=traefik-compose.yaml

# config prod
if [[ $INSTALL_TYPE != DEMO ]]; then
	YAML_BCEPHAL_NAME=bcephal-compose-v8-prod.yaml
fi

DOMAINE=0.0.0.0
DOCKER_FILE_NAME=../../../docker-files/V8.0.0/dockerfilev8Service
BCEPHAL_BCEPHAL_BUILD_CONTEXT=.
UBUNTU_IMAGE_FILE_NAME=../../../docker-files/V8.0.0/dockerfilev8

BCEPHAL_DOCKER_IMAGES_REPOSITORY_PATH=../../../docker-images-v8

TRAEFIK_SERVICE_NAME=traefik
TRAEFIK_NAME=traefik_entryPoint
TRAEFIK_VERSION=v2.3
TRAEFIK_NETWORK=webgateway
TRAEFIK_BCEPHAL_RULE='HostSNI(`*`)'
TRAEFIK_BCEPHAL_RULE_REDIRECT='Host(`traefik`) && PathPrefix(`/auth`)'
TRAEFIK_DIR=$TRAEFIK_SERVICE_NAME
TRAEFIK_USER=bdbcephalu05
TRAEFIK_PWD=zbdbceZ7S54DF8HDW*94512pha]klu05
TRAEFIK_ENC_USER=bdbcephalu05:{SHA}ZrKGrJxNw+MiQXGb0xsb6TOLuF0=
TRAEFIK_IMAGE=traefik:$TRAEFIK_VERSION

POSTGRES_SERVICE_NAME=postgresDemoV8
POSTGRES_CONTAINER_NAME=bcephal-postgres-demoV8
POSTGRES_PASSWORD=!StW5DZB3521A523SQrQong78sA?dminP@pksdnbgvfc.kGVB
POSTGRES_MEMORY=2G
POSTGRES_CPUS=1
POSTGRES_USER=bcephalV8



# config prod
if [[ $INSTALL_TYPE != DEMO ]]; then
	POSTGRES_SERVICE_NAME=postgresProdV8
	POSTGRES_CONTAINER_NAME=bcephal-postgres-prodV8
	POSTGRES_PASSWORD=!StWDZB321591SA5WZSrro98Qng78sA?dmKinP@pksdnbgvfc.kGVB
	POSTGRES_MEMORY=2G
	POSTGRES_CPUS=1
	POSTGRES_USER=bcephal
fi


KEYCLOAK_SERVICE_NAME=keycloakDemoV8
KEYCLOAK_CONTAINER_NAME=bcephal-keycloak-demoV8
KEYCLOAK_PASSWORD=!StW5DZB3521A523SQrQLong78sA?dminP@pksdnbgvfc.kGVB
KEYCLOAK_MEMORY=1G
KEYCLOAK_CPUS=1
KEYCLOAK_USER=bcephalV8.
KEYCLOAK_IMPORT=/tmp/bcephal-realm.json
KEYCLOAK_IMAGE=jboss/keycloak:13.0.1
BCEPHAL_DEMO_UBUNTU_KEYCLOAK_PORT=8251
BCEPHAL_PROD_UBUNTU_KEYCLOAK_PORT=8142

# config prod
if [[ $INSTALL_TYPE != DEMO ]]; then
	KEYCLOAK_SERVICE_NAME=keycloakProd
	KEYCLOAK_CONTAINER_NAME=bcephal-keycloak-prod
	KEYCLOAK_PASSWORD=!StWDZB321591SA5WZSrro98Qng78sA?dmEnKinP@pksdnbgvfc.kGVB
	KEYCLOAK_MEMORY=1G
	KEYCLOAK_CPUS=1
	KEYCLOAK_USER=bcephalv8__
	KEYCLOAK_IMPORT=/tmp/bcephal-realm.json
fi


BCEPHAL_GIT_AUTH_USER_NAME=jossongo
BCEPHAL_GIT_AUTH_USER_PWD=.bibi.merci.

POSTGRES_ADRESS=$POSTGRES_SERVICE_NAME:5432

KEYCLOAK_HOST=161.97.125.193
KEYCLOAK_PORT=7492
#BCEPHAL_KEYCLOAK=$KEYCLOAK_HOST:$KEYCLOAK_PORT
BCEPHAL_UBUNTU_KEYCLOAK_PORT=$BCEPHAL_DEMO_UBUNTU_KEYCLOAK_PORT
if [[ $INSTALL_TYPE != DEMO ]]; then
BCEPHAL_UBUNTU_KEYCLOAK_PORT=$BCEPHAL_PROD_UBUNTU_KEYCLOAK_PORT
fi
KEYCLOAK_EXTERNAL_HOSTNAME=$TRAEFIK_SERVICE_NAME
BCEPHAL_KEYCLOAK=$TRAEFIK_SERVICE_NAME:$BCEPHAL_UBUNTU_KEYCLOAK_PORT
KEYCLOAK_FRONTEND_URL=$KEYCLOAK_HOST:$KEYCLOAK_PORT
KEYCLOAK_FRONTEND_URL_=$KEYCLOAK_HOST:$BCEPHAL_UBUNTU_KEYCLOAK_PORT

BCEPHAL_DB_USER_PWD=GATbvcdePK8547Y$POSTGRES_PASSWORD

BCEPHAL_DEMO_PORT=8492
BCEPHAL_DEMO_UBUNTU_SSH_PORT=6723
BCEPHAL_DEMO_UBUNTU_SSH_PWD=bcephalV8302.
BCEPHAL_DEMO_ENTRYPOINT_NAME=webBcephalDemoV8
BCEPHAL_DEMO_TRAEFIK_ROUTE_NAME=bcephal_demoV8_route
BCEPHAL_DEMO_TRAEFIK_SERVICE_NAME=bcephal_demoV8_service

BCEPHAL_PROD_PORT=7641
BCEPHAL_PROD_UBUNTU_SSH_PORT=9369
BCEPHAL_PROD_UBUNTU_SSH_PWD=bcephalV8302.9368ZRP[
BCEPHAL_PROD_ENTRYPOINT_NAME=webBcephalProdV8
BCEPHAL_PROD_TRAEFIK_ROUTE_NAME=bcephal_prodV8_route
BCEPHAL_PROD_TRAEFIK_SERVICE_NAME=bcephal_prodV8_service

BCEPHAL_TRAEFIK_ROUTE_NAME=$BCEPHAL_DEMO_TRAEFIK_ROUTE_NAME
BCEPHAL_TRAEFIK_SERVICE_NAME=$BCEPHAL_DEMO_TRAEFIK_SERVICE_NAME
BCEPHAL_UBUNTU_SSH_PORT=$BCEPHAL_DEMO_UBUNTU_SSH_PORT
BCEPHAL_UBUNTU_SSH_PWD=$BCEPHAL_DEMO_UBUNTU_SSH_PWD
BCEPHAL_ENTRYPOINT_NAME=$BCEPHAL_DEMO_ENTRYPOINT_NAME
# config prod
if [[ $INSTALL_TYPE != DEMO ]]; then
	BCEPHAL_TRAEFIK_ROUTE_NAME=$BCEPHAL_PROD_TRAEFIK_ROUTE_NAME
	BCEPHAL_TRAEFIK_SERVICE_NAME=$BCEPHAL_PROD_TRAEFIK_SERVICE_NAME
	BCEPHAL_UBUNTU_SSH_PORT=$BCEPHAL_PROD_UBUNTU_SSH_PORT
	BCEPHAL_UBUNTU_SSH_PWD=$BCEPHAL_PROD_UBUNTU_SSH_PWD
	BCEPHAL_ENTRYPOINT_NAME=$BCEPHAL_PROD_ENTRYPOINT_NAME
fi


BCEPHAL_SERVICE_NAME=bcephalDemoV8
BCEPHAL_CONTAINER_NAME=bcephalDemoV8Service
BCEPHAL_HOSTNAME=bcephalDemoV8
BCEPHAL_VALUME_BASE=/opt/bcephal-v8/demo
BCEPHAL_INTERNAL_NETWORK=bcephalDemoV8Network
BCEPHAL_MEMORY=7G
BCEPHAL_SERVER_MAX_MEMORY_SIZE=5000m
BCEPHAL_CPUS=2
BCEPHAL_UBUNTU_NAME=ubuntu-bcephal-v8:$INSTALL_VERSION

# config prod
if [[ $INSTALL_TYPE != DEMO ]]; then
	BCEPHAL_SERVICE_NAME=bcephalProd
	BCEPHAL_CONTAINER_NAME=bcephalProdService
	BCEPHAL_HOSTNAME=bcephalProd
	BCEPHAL_VALUME_BASE=/opt/bcephal-v8/prod
	BCEPHAL_INTERNAL_NETWORK=bcephalProdV8Network
	BCEPHAL_MEMORY=7G
	BCEPHAL_SERVER_MAX_MEMORY_SIZE=5000m
	BCEPHAL_CPUS=2
	BCEPHAL_UBUNTU_NAME=ubuntu-bcephal-prod-v8:$INSTALL_VERSION
fi


BCEPHAL_VALUME_HOME=$BCEPHAL_VALUME_BASE/userhome
BCEPHAL_VALUME_DATA=$BCEPHAL_VALUME_BASE/data
BCEPHAL_VALUME_LOGS=$BCEPHAL_VALUME_BASE/logs
BCEPHAL_VALUME_DATABASE=$BCEPHAL_VALUME_BASE/database
BCEPHAL_VALUME_VAR=$BCEPHAL_VALUME_BASE/var
BCEPHAL_VALUME_KEYCLOAK_IMPORT_FILE=/home/jossongo/bcephal-demo-realm.json
BCEPHAL_VALUME_KEYCLOAK_Data=$BCEPHAL_VALUME_BASE/keycloak-Data

BCEPHAL_EXTERNAL_NETWORK="$TRAEFIK_DIR"_"$TRAEFIK_NETWORK"
BCEPHAL_DIR=$BCEPHAL_SERVICE_NAME

BCEPHAL_UBUNTU_SERVICE_NAME=bcephal-ubuntu-service
BCEPHAL_UBUNTU_IMAGE_NAME=bcephal-ubuntu-20.04

buildTraefikYaml(){

echo 'version: "3.8"' > $YAML_TRAEFIK_NAME
echo 'services:' >> $YAML_TRAEFIK_NAME
echo "    $TRAEFIK_SERVICE_NAME:" >> $YAML_TRAEFIK_NAME
echo "        hostname: $TRAEFIK_SERVICE_NAME" >> $YAML_TRAEFIK_NAME
echo "        image: \"$TRAEFIK_IMAGE\"" >> $YAML_TRAEFIK_NAME
echo '        command:' >> $YAML_TRAEFIK_NAME
echo '            - "--api.insecure=true"' >> $YAML_TRAEFIK_NAME
echo '            - "--providers.docker=true"' >> $YAML_TRAEFIK_NAME
echo '            - "--providers.docker.exposedByDefault=false"' >> $YAML_TRAEFIK_NAME
echo "            - \"--entrypoints.$BCEPHAL_DEMO_ENTRYPOINT_NAME.address=:$BCEPHAL_DEMO_PORT\"" >> $YAML_TRAEFIK_NAME
echo "            - \"--entrypoints.$BCEPHAL_PROD_ENTRYPOINT_NAME.address=:$BCEPHAL_PROD_PORT\"" >> $YAML_TRAEFIK_NAME
echo "            - \"--entrypoints.SSH_$BCEPHAL_PROD_ENTRYPOINT_NAME.address=:$BCEPHAL_PROD_UBUNTU_SSH_PORT\"" >> $YAML_TRAEFIK_NAME
echo "            - \"--entrypoints.SSH_$BCEPHAL_DEMO_ENTRYPOINT_NAME.address=:$BCEPHAL_DEMO_UBUNTU_SSH_PORT\"" >> $YAML_TRAEFIK_NAME

#echo "            - \"--entrypoints.KEYCLOAK_$BCEPHAL_PROD_ENTRYPOINT_NAME.address=:$BCEPHAL_PROD_UBUNTU_KEYCLOAK_PORT\"" >> $YAML_TRAEFIK_NAME
#echo "            - \"--entrypoints.KEYCLOAK_$BCEPHAL_DEMO_ENTRYPOINT_NAME.address=:$BCEPHAL_DEMO_UBUNTU_KEYCLOAK_PORT\"" >> $YAML_TRAEFIK_NAME
#echo "            - \"--entrypoints.KEYCLOAK_$BCEPHAL_DEMO_ENTRYPOINT_NAME.forwardedHeaders.insecure=true\"" >> $YAML_TRAEFIK_NAME

echo "            - \"--entrypoints.KEYCLOAK_$BCEPHAL_DEMO_ENTRYPOINT_NAME.forwardedHeaders.insecure=true\"" >> $YAML_TRAEFIK_NAME
echo "            - \"--entrypoints.KEYCLOAK_$BCEPHAL_DEMO_ENTRYPOINT_NAME.proxyProtocol.insecure=true\"" >> $YAML_TRAEFIK_NAME


echo "        container_name: $TRAEFIK_NAME" >> $YAML_TRAEFIK_NAME
#echo "        domainname: $DOMAINE" >> $YAML_TRAEFIK_NAME
echo "        ports:" >> $YAML_TRAEFIK_NAME
echo "            - \"$DOMAINE:8080:8080\"" >> $YAML_TRAEFIK_NAME
echo "            - \"$DOMAINE:$BCEPHAL_DEMO_PORT:$BCEPHAL_DEMO_PORT\"" >> $YAML_TRAEFIK_NAME
echo "            - \"$DOMAINE:$BCEPHAL_PROD_PORT:$BCEPHAL_PROD_PORT\"" >> $YAML_TRAEFIK_NAME

echo "            - \"$DOMAINE:$BCEPHAL_DEMO_UBUNTU_SSH_PORT:$BCEPHAL_DEMO_UBUNTU_SSH_PORT\"" >> $YAML_TRAEFIK_NAME
echo "            - \"$DOMAINE:$BCEPHAL_PROD_UBUNTU_SSH_PORT:$BCEPHAL_PROD_UBUNTU_SSH_PORT\"" >> $YAML_TRAEFIK_NAME

#echo "            - \"$DOMAINE:$BCEPHAL_DEMO_UBUNTU_KEYCLOAK_PORT:$BCEPHAL_DEMO_UBUNTU_KEYCLOAK_PORT\"" >> $YAML_TRAEFIK_NAME
#echo "            - \"$DOMAINE:$BCEPHAL_PROD_UBUNTU_KEYCLOAK_PORT:$BCEPHAL_PROD_UBUNTU_KEYCLOAK_PORT\"" >> $YAML_TRAEFIK_NAME

echo "        restart: unless-stopped" >> $YAML_TRAEFIK_NAME
echo "        networks:" >> $YAML_TRAEFIK_NAME
echo "            - $TRAEFIK_NETWORK" >> $YAML_TRAEFIK_NAME
echo "        labels:" >> $YAML_TRAEFIK_NAME
echo "            - \"traefik.http.middlewares.auth.basicauth.users=$TRAEFIK_ENC_USER\"" >> $YAML_TRAEFIK_NAME

#echo "            - \"traefik.http.routers.$BCEPHAL_TRAEFIK_ROUTE_NAME.rule=$TRAEFIK_BCEPHAL_RULE_REDIRECT\"" >> $YAML_TRAEFIK_NAME
#echo "            - \"traefik.http.routers.$BCEPHAL_TRAEFIK_ROUTE_NAME.service=$KEYCLOAK_SERVICE_NAME\"" >> $YAML_TRAEFIK_NAME
#echo "            - \"traefik.http.services.$KEYCLOAK_SERVICE_NAME=$KEYCLOAK_SERVICE_NAME.loadbalancer.server.url=http://$KEYCLOAK_FRONTEND_URL_/auth\"" >> $YAML_TRAEFIK_NAME

#echo "            - \"traefik.http.middlewares.$BCEPHAL_TRAEFIK_SERVICE_NAME.redirectregex.regex=^http://$BCEPHAL_KEYCLOAK/\"" >> $YAML_TRAEFIK_NAME
#echo "            - \"traefik.http.middlewares.$BCEPHAL_TRAEFIK_SERVICE_NAME.redirectregex.replacement=http://$KEYCLOAK_FRONTEND_URL_/\"" >> $YAML_TRAEFIK_NAME
#echo "            - \"traefik.http.middlewares.$BCEPHAL_TRAEFIK_SERVICE_NAME.redirectregex.permanent=true\"" >> $YAML_TRAEFIK_NAME




#echo "            - \"traefik.http.middlewares.$BCEPHAL_TRAEFIK_SERVICE_NAME.redirectregex.regex=^http://$BCEPHAL_KEYCLOAK/(.*)\"" >> $YAML_TRAEFIK_NAME
#echo "            - \"traefik.http.middlewares.$BCEPHAL_TRAEFIK_SERVICE_NAME.redirectregex.regex=^http://$BCEPHAL_KEYCLOAK/(.*)\"" >> $YAML_TRAEFIK_NAME
#echo "            - \"traefik.http.middlewares.$BCEPHAL_TRAEFIK_SERVICE_NAME.redirectregex.replacement=http://$KEYCLOAK_FRONTEND_URL_/$$1\"" >> $YAML_TRAEFIK_NAME
##echo "            - \"traefik.http.middlewares.$BCEPHAL_TRAEFIK_SERVICE_NAME.redirectregex.regex=^http://$BCEPHAL_KEYCLOAK/(.*)\"" >> $YAML_TRAEFIK_NAME
##echo "            - \"traefik.http.middlewares.$BCEPHAL_TRAEFIK_SERVICE_NAME.forwardauth.address=$BCEPHAL_KEYCLOAK\"" >> $YAML_TRAEFIK_NAME


echo "        volumes:" >> $YAML_TRAEFIK_NAME
echo "            - \"/var/run/docker.sock:/var/run/docker.sock:ro\"" >> $YAML_TRAEFIK_NAME
echo "networks:" >> $YAML_TRAEFIK_NAME
echo "    $TRAEFIK_NETWORK:" >> $YAML_TRAEFIK_NAME
echo "        driver: bridge" >> $YAML_TRAEFIK_NAME
}

buildBcephalYaml(){
echo 'version: "3.8"' > $YAML_BCEPHAL_NAME
echo 'services:' >> $YAML_BCEPHAL_NAME
echo "    $BCEPHAL_SERVICE_NAME:" >> $YAML_BCEPHAL_NAME
echo "        build:" >> $YAML_BCEPHAL_NAME 
echo "            context: $BCEPHAL_BCEPHAL_BUILD_CONTEXT" >> $YAML_BCEPHAL_NAME
echo "            dockerfile: ./$DOCKER_FILE_NAME" >> $YAML_BCEPHAL_NAME
echo "            args:" >> $YAML_BCEPHAL_NAME 
echo "              - BCEPHAL_KEYCLOAK=$BCEPHAL_KEYCLOAK" >> $YAML_BCEPHAL_NAME
echo "              - KEYCLOAK_FRONTEND_URL=$KEYCLOAK_FRONTEND_URL" >> $YAML_BCEPHAL_NAME
echo "              - KEYCLOAK_EXTERNAL_HOSTNAME=$KEYCLOAK_EXTERNAL_HOSTNAME" >> $YAML_BCEPHAL_NAME
echo "              - KEYCLOAK_EXTERNAL_PORT=$BCEPHAL_UBUNTU_KEYCLOAK_PORT" >> $YAML_BCEPHAL_NAME
echo "              - POSTGRES_ADRESS=$POSTGRES_ADRESS" >> $YAML_BCEPHAL_NAME
echo "              - BCEPHAL_MAX_MEMORY_SIZE=$BCEPHAL_SERVER_MAX_MEMORY_SIZE" >> $YAML_BCEPHAL_NAME
echo "              - BCEPHAL_GIT_AUTH_USER_NAME=$BCEPHAL_GIT_AUTH_USER_NAME" >> $YAML_BCEPHAL_NAME
echo "              - BCEPHAL_GIT_AUTH_USER_PWD=$BCEPHAL_GIT_AUTH_USER_PWD" >> $YAML_BCEPHAL_NAME
echo "              - BCEPHAL_DB_USER_NAME=$POSTGRES_USER" >> $YAML_BCEPHAL_NAME
echo "              - BCEPHAL_DB_USER_PWD=$POSTGRES_PASSWORD" >> $YAML_BCEPHAL_NAME
echo "        image: $BCEPHAL_UBUNTU_NAME" >> $YAML_BCEPHAL_NAME
echo "        container_name: $BCEPHAL_CONTAINER_NAME" >> $YAML_BCEPHAL_NAME
echo "        hostname: $BCEPHAL_HOSTNAME" >> $YAML_BCEPHAL_NAME
echo "        depends_on:" >> $YAML_BCEPHAL_NAME
echo "            - $POSTGRES_SERVICE_NAME" >> $YAML_BCEPHAL_NAME
#echo "            - $KEYCLOAK_SERVICE_NAME" >> $YAML_BCEPHAL_NAME
echo "        volumes:" >> $YAML_BCEPHAL_NAME
echo "            - /sys/fs/cgroup:/sys/fs/cgroup:ro" >> $YAML_BCEPHAL_NAME
echo "            - $BCEPHAL_VALUME_HOME:/home/bcephal" >> $YAML_BCEPHAL_NAME
echo "            - $BCEPHAL_VALUME_LOGS:/run/Bcephal" >> $YAML_BCEPHAL_NAME
echo "            - /var/run/docker.sock:/var/run/docker.sock" >> $YAML_BCEPHAL_NAME
echo "        deploy:" >> $YAML_BCEPHAL_NAME
echo "            resources:" >> $YAML_BCEPHAL_NAME
echo "                limits:" >> $YAML_BCEPHAL_NAME
echo "                    cpus: $BCEPHAL_CPUS" >> $YAML_BCEPHAL_NAME
echo "                    memory: $BCEPHAL_MEMORY" >> $YAML_BCEPHAL_NAME
echo "        networks:" >> $YAML_BCEPHAL_NAME
echo "            - $BCEPHAL_INTERNAL_NETWORK" >> $YAML_BCEPHAL_NAME
echo "            - $BCEPHAL_EXTERNAL_NETWORK" >> $YAML_BCEPHAL_NAME
echo "        healthcheck:" >> $YAML_BCEPHAL_NAME
echo "            disable: true" >> $YAML_BCEPHAL_NAME
echo "        labels:" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.enable=true\"" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.docker.network=$BCEPHAL_EXTERNAL_NETWORK\"" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.tcp.routers.$BCEPHAL_TRAEFIK_ROUTE_NAME.rule=$TRAEFIK_BCEPHAL_RULE\"" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.tcp.routers.$BCEPHAL_TRAEFIK_ROUTE_NAME.service=$BCEPHAL_TRAEFIK_SERVICE_NAME\"" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.tcp.routers.$BCEPHAL_TRAEFIK_ROUTE_NAME.entrypoints=$BCEPHAL_ENTRYPOINT_NAME\"" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.http.services.$BCEPHAL_TRAEFIK_SERVICE_NAME.loadbalancer.passhostheader=true\"" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.tcp.services.$BCEPHAL_TRAEFIK_SERVICE_NAME.loadbalancer.server.port=9000\"" >> $YAML_BCEPHAL_NAME


echo "            - \"traefik.tcp.routers.SSH_$BCEPHAL_TRAEFIK_ROUTE_NAME.rule=$TRAEFIK_BCEPHAL_RULE\"" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.tcp.routers.SSH_$BCEPHAL_TRAEFIK_ROUTE_NAME.service=SSH_$BCEPHAL_TRAEFIK_SERVICE_NAME\"" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.tcp.routers.SSH_$BCEPHAL_TRAEFIK_ROUTE_NAME.entrypoints=SSH_$BCEPHAL_ENTRYPOINT_NAME\"" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.http.services.SSH_$BCEPHAL_TRAEFIK_SERVICE_NAME.loadbalancer.passhostheader=true\"" >> $YAML_BCEPHAL_NAME
echo "            - \"traefik.tcp.services.SSH_$BCEPHAL_TRAEFIK_SERVICE_NAME.loadbalancer.server.port=22\"" >> $YAML_BCEPHAL_NAME

#echo "            - \"traefik.tcp.routers.$BCEPHAL_TRAEFIK_ROUTE_NAME.rule=$TRAEFIK_BCEPHAL_RULE_REDIRECT\"" >> $YAML_BCEPHAL_NAME
#echo "            - \"traefik.tcp.routers.$BCEPHAL_TRAEFIK_ROUTE_NAME.service=$KEYCLOAK_SERVICE_NAME\"" >> $YAML_BCEPHAL_NAME

echo "            - \"traefik.http.middlewares.auth.basicauth.users=$TRAEFIK_ENC_USER\"" >> $YAML_BCEPHAL_NAME
echo "        restart: unless-stopped" >> $YAML_BCEPHAL_NAME
echo "        privileged: true" >> $YAML_BCEPHAL_NAME

echo "    $BCEPHAL_UBUNTU_SERVICE_NAME:" >> $YAML_BCEPHAL_NAME
echo "        build:" >> $YAML_BCEPHAL_NAME
echo "            context: $BCEPHAL_BCEPHAL_BUILD_CONTEXT" >> $YAML_BCEPHAL_NAME
echo "            dockerfile: ./$UBUNTU_IMAGE_FILE_NAME" >> $YAML_BCEPHAL_NAME
echo "            args:" >> $YAML_BCEPHAL_NAME
echo "              - BCEPHAL_USER_PWD=$BCEPHAL_UBUNTU_SSH_PWD" >> $YAML_BCEPHAL_NAME
echo "        image: $BCEPHAL_UBUNTU_IMAGE_NAME" >> $YAML_BCEPHAL_NAME
echo "    $POSTGRES_SERVICE_NAME:" >> $YAML_BCEPHAL_NAME
echo "        image: postgres:13" >> $YAML_BCEPHAL_NAME
echo "        container_name: $POSTGRES_CONTAINER_NAME" >> $YAML_BCEPHAL_NAME
echo "        restart: unless-stopped" >> $YAML_BCEPHAL_NAME
echo "        environment:" >> $YAML_BCEPHAL_NAME
echo "            - POSTGRES_USER=$POSTGRES_USER" >> $YAML_BCEPHAL_NAME
echo "            - POSTGRES_PASSWORD=$POSTGRES_PASSWORD" >> $YAML_BCEPHAL_NAME
echo "            - PGDATA=/var/lib/postgresql/data/pgdata" >> $YAML_BCEPHAL_NAME
echo "        volumes:" >> $YAML_BCEPHAL_NAME
echo "            - $BCEPHAL_VALUME_DATABASE:/var/lib/postgresql/data" >> $YAML_BCEPHAL_NAME
echo "        deploy:" >> $YAML_BCEPHAL_NAME
echo "            resources:" >> $YAML_BCEPHAL_NAME
echo "                limits:" >> $YAML_BCEPHAL_NAME
echo "                    cpus: $POSTGRES_CPUS" >> $YAML_BCEPHAL_NAME
echo "                    memory: $POSTGRES_MEMORY" >> $YAML_BCEPHAL_NAME
echo "        networks:" >> $YAML_BCEPHAL_NAME
echo "            - $BCEPHAL_INTERNAL_NETWORK" >> $YAML_BCEPHAL_NAME
#echo "    $KEYCLOAK_SERVICE_NAME:" >> $YAML_BCEPHAL_NAME
#echo "        image: $KEYCLOAK_IMAGE" >> $YAML_BCEPHAL_NAME
#echo "        container_name: $KEYCLOAK_CONTAINER_NAME" >> $YAML_BCEPHAL_NAME
#echo "        restart: unless-stopped" >> $YAML_BCEPHAL_NAME
#echo "        environment:" >> $YAML_BCEPHAL_NAME
#echo "            - KEYCLOAK_USER=$KEYCLOAK_USER" >> $YAML_BCEPHAL_NAME
#echo "            - KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD" >> $YAML_BCEPHAL_NAME
#echo "            - KEYCLOAK_IMPORT=$KEYCLOAK_IMPORT" >> $YAML_BCEPHAL_NAME
#echo "            - PROXY_ADDRESS_FORWARDING=true" >> $YAML_BCEPHAL_NAME
##echo "            - KEYCLOAK_HOSTNAME=$KEYCLOAK_FRONTEND_URL_" >> $YAML_BCEPHAL_NAME
##echo "            - KEYCLOAK_FRONTEND_URL=$KEYCLOAK_FRONTEND_URL_/auth" >> $YAML_BCEPHAL_NAME
#echo "        volumes:" >> $YAML_BCEPHAL_NAME
#echo "            - $BCEPHAL_VALUME_KEYCLOAK_IMPORT_FILE:$KEYCLOAK_IMPORT" >> $YAML_BCEPHAL_NAME
#echo "        depends_on:" >> $YAML_BCEPHAL_NAME
#echo "            - $POSTGRES_SERVICE_NAME" >> $YAML_BCEPHAL_NAME
#echo "        command: [\"-b\", \"0.0.0.0\",\"-Dkeycloak.profile.feature.upload_scripts=enabled\",\"-Dkeycloak.migration.action=import\",\"-Dkeycloak.migration.provider=singleFile\",\"-Dkeycloak.migration.strategy=OVERWRITE_EXISTING\",\"-Dkeycloak.migration.file=$KEYCLOAK_IMPORT\"]" >> $YAML_BCEPHAL_NAME
#echo "        labels:" >> $YAML_BCEPHAL_NAME
#echo "            - \"traefik.enable=true\"" >> $YAML_BCEPHAL_NAME
#echo "            - \"traefik.docker.network=$BCEPHAL_EXTERNAL_NETWORK\"" >> $YAML_BCEPHAL_NAME
#echo "            - \"traefik.tcp.routers.KEYCLOAK_$BCEPHAL_TRAEFIK_ROUTE_NAME.rule=$TRAEFIK_BCEPHAL_RULE\"" >> $YAML_BCEPHAL_NAME
#echo "            - \"traefik.tcp.routers.KEYCLOAK_$BCEPHAL_TRAEFIK_ROUTE_NAME.service=KEYCLOAK_$BCEPHAL_TRAEFIK_SERVICE_NAME\"" >> $YAML_BCEPHAL_NAME
#echo "            - \"traefik.tcp.routers.KEYCLOAK_$BCEPHAL_TRAEFIK_ROUTE_NAME.entrypoints=KEYCLOAK_$BCEPHAL_ENTRYPOINT_NAME\"" >> $YAML_BCEPHAL_NAME
#echo "            - \"traefik.http.services.KEYCLOAK_$BCEPHAL_TRAEFIK_SERVICE_NAME.loadbalancer.passhostheader=true\"" >> $YAML_BCEPHAL_NAME
#echo "            - \"traefik.tcp.services.KEYCLOAK_$BCEPHAL_TRAEFIK_SERVICE_NAME.loadbalancer.server.port=8080\"" >> $YAML_BCEPHAL_NAME
#echo "        deploy:" >> $YAML_BCEPHAL_NAME
#echo "            resources:" >> $YAML_BCEPHAL_NAME
#echo "                limits:" >> $YAML_BCEPHAL_NAME
#echo "                    cpus: $KEYCLOAK_CPUS" >> $YAML_BCEPHAL_NAME
#echo "                    memory: $KEYCLOAK_MEMORY" >> $YAML_BCEPHAL_NAME
#echo "        networks:" >> $YAML_BCEPHAL_NAME
#echo "            - $BCEPHAL_INTERNAL_NETWORK" >> $YAML_BCEPHAL_NAME
#echo "            - $BCEPHAL_EXTERNAL_NETWORK" >> $YAML_BCEPHAL_NAME
echo "networks:" >> $YAML_BCEPHAL_NAME
echo "    $BCEPHAL_EXTERNAL_NETWORK:" >> $YAML_BCEPHAL_NAME
echo "        driver: bridge" >> $YAML_BCEPHAL_NAME
echo "        external: true" >> $YAML_BCEPHAL_NAME
echo "    $BCEPHAL_INTERNAL_NETWORK:" >> $YAML_BCEPHAL_NAME
echo "        driver: bridge" >> $YAML_BCEPHAL_NAME
}

buildServiceItemYaml(){

echo "    $1:" >> $3
echo "        build:" >> $3
echo "            context: $BCEPHAL_BCEPHAL_BUILD_CONTEXT" >> $3
echo "            dockerfile: $5" >> $3
echo "            args:" >> $3
echo "              - BCEPHAL_CONFIG_ADRESS=$BCEPHAL_CONFIG_ADRESS" >> $3
echo "              - BCEPHAL_MAX_MEMORY_SIZE=$BCEPHAL_SERVER_MAX_MEMORY_SIZE" >> $3
echo "              - BCEPHAL_GIT_AUTH_USER_NAME=$BCEPHAL_GIT_AUTH_USER_NAME" >> $3
echo "              - BCEPHAL_GIT_AUTH_USER_PWD=$BCEPHAL_GIT_AUTH_USER_PWD" >> $3
echo "        image: $4" >> $3
echo "        command: /bin/bash -c \"wait-for-it $BCEPHAL_REGISTRATION_STATIC_ADRESS:$BCEPHAL_REGISTRATION_PORT -t $WAIT_FOR_IT_TIMEOUT2 -s -- /usr/sbin/init\"" >> $3
echo "        container_name: $2" >> $3
echo "        hostname: $1" >> $3
echo "        extra_hosts:" >> $3
echo "            - \"$1:127.0.0.1\"" >> $3
echo "        depends_on:" >> $3
echo "            - $CONFIG_SERVICE_NAME" >> $3
echo "            - $REGISTRATION_SERVICE_NAME" >> $3
echo "        volumes:" >> $3
echo "            - /sys/fs/cgroup:/sys/fs/cgroup:ro" >> $3
echo "            - $BCEPHAL_VALUME_HOME:/home/bcephal/" >> $3
echo "            - $BCEPHAL_VALUME_LOGS:/run/Bcephal/" >> $3
echo "            - /var/run/docker.sock:/var/run/docker.sock" >> $3
echo "        deploy:" >> $3
echo "            resources:" >> $3
echo "                limits:" >> $3
echo "                    cpus: $BCEPHAL_CPUS" >> $3
echo "                    memory: $BCEPHAL_MEMORY" >> $3
echo "        networks:" >> $3
#echo "            - $BCEPHAL_INTERNAL_NETWORK" >> $3
echo "            $BCEPHAL_INTERNAL_NETWORK:" >> $YAML_BCEPHAL_NAME
echo "                ipv4_address: $6" >> $YAML_BCEPHAL_NAME
#echo "            $BCEPHAL_INTERNAL_NETWORK:" >> $3
#echo "                aliases:" >> $3
#echo "                    - $1" >> $3
echo "        restart: unless-stopped" >> $3
echo "        privileged: true" >> $3
}

progress() {
    echo "$*" >&2
}

init() {
if [ ! -f "$YAML_TRAEFIK_NAME" ]; then
	progress "TRY TO BUILDING TRAEFIK YAML FILE..."
	buildTraefikYaml &&\
	progress "BUILDED SUCCEFULY STATUS:$?"
	RC=$?
	if [ $RC = 0 ] && [ ! -d "$TRAEFIK_DIR" ]; then
		progress "CREATED $TRAEFIK_DIR DIR"
		mkdir "$TRAEFIK_DIR"
	fi
	mv "$YAML_TRAEFIK_NAME" "$TRAEFIK_DIR/"
fi

if [ ! -f "$YAML_BCEPHAL_NAME" ]; then
	progress "TRY TO BUILDING BCEPHAL YAML FILE..."
	buildBcephalYaml &&\
	progress "BUILDED SUCCEFULY STATUS:$?"
	RC=$?
	if [ $RC = 0 ] && [ ! -d "$BCEPHAL_DIR" ]; then
		progress "CREATED $BCEPHAL_DIR DIR"
		mkdir "$BCEPHAL_DIR"
	fi
	mv "$YAML_BCEPHAL_NAME" "$BCEPHAL_DIR/"
fi

if [ ! -f "/usr/local/bin/ufw-docker" ]; then
	 wget -O /usr/local/bin/ufw-docker \
			https://github.com/chaifeng/ufw-docker/raw/master/ufw-docker
	 chmod +x /usr/local/bin/ufw-docker
	 docker swarm init
fi
}

forwardTraefik(){
	#sudo ufw-docker allow  $TRAEFIK_NAME  8080/tcp || true
	sudo ufw-docker allow  $TRAEFIK_NAME  $BCEPHAL_UBUNTU_SSH_PORT/tcp || true
	#sudo ufw-docker allow  $TRAEFIK_NAME  $BCEPHAL_UBUNTU_KEYCLOAK_PORT/tcp || true
	if [[ $INSTALL_TYPE != DEMO ]]; then
		sudo ufw-docker allow  $TRAEFIK_NAME  $BCEPHAL_PROD_PORT/tcp || true
	else
		sudo ufw-docker allow  $TRAEFIK_NAME  $BCEPHAL_DEMO_PORT/tcp || true
	fi
}

deleteForwardTraefik(){
	#sudo ufw-docker delete allow  $TRAEFIK_NAME  8080/tcp
	if [[ $INSTALL_TYPE != DEMO ]]; then
		sudo ufw-docker delete allow  $TRAEFIK_NAME  $BCEPHAL_PROD_PORT/tcp || true
	else
		sudo ufw-docker delete allow  $TRAEFIK_NAME  $BCEPHAL_DEMO_PORT/tcp || true
	fi
	sudo ufw-docker delete allow  $TRAEFIK_NAME  $BCEPHAL_UBUNTU_SSH_PORT/tcp || true
	#sudo ufw-docker delete allow  $TRAEFIK_NAME  $BCEPHAL_UBUNTU_KEYCLOAK_PORT/tcp || true
}


clean(){
	if [ -f "$YAML_TRAEFIK_NAME" ]; then
		rm "$YAML_TRAEFIK_NAME"
	fi
	if [  -f "$YAML_BCEPHAL_NAME" ]; then
		rm "$YAML_BCEPHAL_NAME"
	fi
	if [  -d "$TRAEFIK_DIR" ]; then
		rm -rf "$TRAEFIK_DIR"
	fi
	if [  -d "$BCEPHAL_DIR" ]; then
		rm -rf "$BCEPHAL_DIR"
	fi
#	deleteForwardTraefik
	progress "ENDED TO CLEAN "
}

del_traefik(){
delete-image-name $TRAEFIK_IMAGE
}

cleanAll(){
clean
delete-image
deleteForwardTraefik
}

store-image(){
	docker save -o "$BCEPHAL_DOCKER_IMAGES_REPOSITORY_PATH/$BCEPHAL_UBUNTU_NAME"".tar" "$BCEPHAL_UBUNTU_NAME"
}

load-image(){
	docker image load "$BCEPHAL_DOCKER_IMAGES_REPOSITORY_PATH/$1"".tar"
}
delete-image(){
	docker stop $(docker ps -a -q --filter ancestor="$BCEPHAL_UBUNTU_NAME")
	docker rm $(docker ps -a -q --filter ancestor="$BCEPHAL_UBUNTU_NAME")
	docker rmi -f "$BCEPHAL_UBUNTU_NAME"
}

delete-image-name(){
	docker stop $(docker ps -a -q --filter ancestor="$1")
	docker rm $(docker ps -a -q --filter ancestor="$1")
	docker rmi -f "$1"
}

build_(){
 PD=$(readlink -f "$0")
 cd "$BCEPHAL_DIR"
 docker-compose -f "$YAML_BCEPHAL_NAME" build
 cd $(dirname "$PD")
}
start_traefik_(){
docker-compose -f "$TRAEFIK_DIR/$YAML_TRAEFIK_NAME" up -d
}
stop_traefik_(){
docker-compose -f "$TRAEFIK_DIR/$YAML_TRAEFIK_NAME" down
}
start_(){
docker-compose -f "$BCEPHAL_DIR/$YAML_BCEPHAL_NAME" up -d
}
stop_(){
docker-compose -f "$BCEPHAL_DIR/$YAML_BCEPHAL_NAME" down
}
startAll_(){
start_traefik_
start_
}
stopAll_(){
stop_
stop_traefik_
}
restartAll_(){
stopAll_
startAll_
}

usage(){
 progress " USAGE: $0 clean|build|start_traefik|stop_traefik|start|stop|startAll|restartAll|stopAll
					  |cleanbuild|cleanstart_traefik|cleanstop_traefik|cleanstart|cleanstartAll
					  |cleanrestartAll|cleanstopAll|cleanAllbuild|cleanAllstart_traefik|cleanAllstop_traefik
					  |cleanAllstart|cleanAllstartAll|cleanAllrestartAll|cleanAllstopAll|del_traefik"
}
main(){
if [ -z "$1" ];then
    echo "No argument supplied"
	return
fi
if [ "$1" = "clean" ]; then
 clean
 return
fi
PARAMS=$1
CLEANAFTERCOMMAND=0
if [[ $PARAMS == clean* ]]; then
	if [[ $PARAMS == cleanAll* ]]; then
		PARAMS=$(echo $PARAMS | cut -c9-${#PARAMS})
		if [[ ! $PARAMS == stop* ]]; then
			cleanAll
		else
			CLEANAFTERCOMMAND=2
		fi
	else
		PARAMS=$(echo $PARAMS | cut -c6-${#PARAMS})
		if [[ ! $PARAMS == stop* ]]; then
			clean
		else
			CLEANAFTERCOMMAND=1
		fi
	fi
fi
init

progress "Exec: $PARAMS"
case "$PARAMS"_ in
	build_|start_|stop_|startAll_|restartAll_|stopAll_)
		"$PARAMS"_
		if [ $? = 0 ]; then
			forwardTraefik
		fi
	;;
	start_traefik|stop_traefik)
		"$PARAMS"
		if [ $? = 0 ]; then
			forwardTraefik
		fi
	;;
	del_traefik)
		"$PARAMS"
	;;
	*)
		usage
	;;
esac
if [ $CLEANAFTERCOMMAND = 1 ]; then
	clean
fi

if [ $CLEANAFTERCOMMAND = 2 ]; then
	cleanAll
fi
}
main "$1"