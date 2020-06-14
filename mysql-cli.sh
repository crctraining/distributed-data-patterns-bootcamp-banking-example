#! /bin/bash -e

docker run $* \
   --name mysqlterm --network=${PWD##*/}_default --rm \
   mysql:5.7.13 \
   sh -c 'exec mysql -hmysql  -uroot -prootpassword -o eventuate'
