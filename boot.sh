#!/bin/bash

DOCKER_COMPOSE_DEV=./docker-compose-dev.yml
DOCKER_COMPOSE_PROD=./docker-compose-prod.yml
DOCKER_COMPOSE_LOCAL=./docker-compose-local.yml
echo "Close all containers by default to prevent port occupation."
read -p "Please enter the Y/N:" yesNo
case $yesNo in
[yY])
  docker-compose -f $DOCKER_COMPOSE_DEV down
  docker-compose -f $DOCKER_COMPOSE_PROD down
  docker-compose -f $DOCKER_COMPOSE_LOCAL down
  ;;
[nN])
   exit 1
  ;;
esac

# shellcheck disable=SC2162
echo "By default, the local deployment program is built from source code (please change the profile environment of the root directory bootstrap.yml to dev, otherwise change to prod). Are you sure to deploy the program from docker-compose?"

read -p "Please enter the Y/N:" yesNo
case $yesNo in
[yY])
  echo "Enter y/Y to configure using docker-compose-dev.yml; enter n/N to configure using docker-compose-prod.yml."
  read -p "Please enter the Y/N:" yesNo
  case $yesNo in
  [yY])
    echo "execution docker-compose-dev.yml ..."
    ./gradlew clean && ./gradlew bootJar
    docker-compose -f $DOCKER_COMPOSE_DEV build && docker-compose -f $DOCKER_COMPOSE_DEV up
    ;;
  [nN])
    echo "execution docker-compose-prod.yml ..."
    docker-compose -f $DOCKER_COMPOSE_PROD build && docker-compose -f $DOCKER_COMPOSE_PROD up
    ;;
   *)
     echo "Invalid input ..."
     read -p "Please enter any key to exit"
     exit 1
     ;;
  esac
  ;;
[nN])
  docker-compose -f $DOCKER_COMPOSE_LOCAL build && docker-compose -f $DOCKER_COMPOSE_LOCAL up
  ;;
 *)
   echo "Invalid input ..."
   read -p "Please enter any key to exit"
   exit 1
   ;;
esac