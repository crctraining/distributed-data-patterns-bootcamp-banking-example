#! /bin/bash

docker run ${1:--it} --network=${PWD##*/}_default --rm  mongo:3.6 sh -c "exec /usr/bin/mongo --host mongodb bankingexampledb"
