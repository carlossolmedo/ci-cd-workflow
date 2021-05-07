FROM node:alpine

# Prepare the directory and install dependences
WORKDIR /usr/src/app
RUN pwd
RUN ls -alt
COPY ./app/package*.json /usr/src/app/
RUN npm install

# Install only production dependencies
# ARG NODE_ENV

# RUN if [ "$NODE_ENV" = "develop" ]; \
#         then npm install; \
#         else npm install  --only=production; \
#         fi

# Copy the whole application to docker
COPY ./app /usr/src/app/

RUN ls -alt
ENV PORT=$PORT

EXPOSE $PORT
CMD [ "npm", "start" ]
