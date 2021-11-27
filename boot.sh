#!/bin/bash

#
# Copyright (c) 2021 zf1976
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#
#

DOCKER_COMPOSE_DEV=./docker-compose-dev.yml
DOCKER_COMPOSE_PROD=./docker-compose-prod.yml
DOCKER_COMPOSE_LOCAL=./docker-compose-test.yml
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