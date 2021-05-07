#!/usr/bin/env sh

set -x


echo 'Build for development'

# echo "Stop containers and delete images"
# sh 'docker rm -f $(docker ps | awk \'/api_/ {print $1}\') || true

docker build -t "$IMAGE_NAME":"$BUILD_NUMBER" -f "$DIR_IMAGE" --no-cache .
docker tag "$IMAGE_NAME":"$BUILD_NUMBER" "$REGISTRY"/"$IMAGE_NAME":"$IMAGE_TAG"

echo 'Run the current version'

docker-compose -f "$DIR_COMPOSE" up -d

sleep 5
docker logs "$CONTAINER_CLIENT"

echo "Current API Host: https//$API_HOST"

exit 0
