#!/bin/bash

set -e

while \
    ! kafka-topics --list --bootstrap-server kafka:29092 > /dev/null 2>&1;
  do
    sleep 2;
done

kafka-topics  --create --topic stats.user-actions.v1 \
              --partitions 1 --replication-factor 1 --if-not-exists \
              --bootstrap-server kafka:29092 && \

kafka-topics  --create --topic stats.events-similarity.v1 \
              --partitions 1 --replication-factor 1 --if-not-exists \
              --bootstrap-server kafka:29092
