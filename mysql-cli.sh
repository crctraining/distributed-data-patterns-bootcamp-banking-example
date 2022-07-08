#! /bin/bash -e

. ./_network-env.sh

docker run ${1:--it} \
   --name mysqlterm --network=${NETWORK_NAME?} --rm \
   mysql/mysql-server:8.0.27-1.2.6-server \
   sh -c 'exec mysql -hmysql  -uroot -prootpassword -o eventuate'
