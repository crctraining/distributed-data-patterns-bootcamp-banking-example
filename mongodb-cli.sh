#! /bin/bash

. ./_network-env.sh

docker run ${1:--it} --network=${NETWORK_NAME?} --rm \
  mongo:5.0.6 sh -c "exec /usr/bin/mongo --host mongodb bankingexampledb"
