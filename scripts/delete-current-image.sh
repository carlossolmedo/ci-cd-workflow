#!/usr/bin/env sh

set -x

# docker rm -f docker_db_1 docker_api_1
docker rmi -f $(docker images | awk '/'$IMAGE_NAME'/ { print $2,$3 }' | awk '{ if ($1 == '$BUILD_NUMBER') print $2 }')

exit 0
