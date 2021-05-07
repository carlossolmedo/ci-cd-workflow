#!/usr/bin/env sh

set -x

IMAGE="$1"

echo "Delete old images"

docker rmi -f $(docker images | awk '/'$IMAGE'/ {print $2,$3}' | awk '{if ($1 != "latest" && $1 != "latest-dev" && $1 != "latest-stage" ) print $2 }')

exit 0

