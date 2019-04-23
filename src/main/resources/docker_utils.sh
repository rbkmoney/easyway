#!/usr/bin/env bash

docker volume prune
docker network prune
docker rm -f $(docker ps -a -q)
