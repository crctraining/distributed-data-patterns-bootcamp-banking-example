#! /bin/bash -e

SERVICE_HOST_IP=localhost ./gradlew -a :end-to-end-tests:cleanTest :end-to-end-tests:test
