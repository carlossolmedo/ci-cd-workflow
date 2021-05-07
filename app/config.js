"use strict";

import { config } from 'dotenv';

config();

// Load environment
//dotenv.config({ path: `.env.${NODE_ENV}` });

module.exports = {
    imageVersion: process.env.IMAGE_VERSION,
    port: process.env.PORT,
    dbService: process.env.DB_SERVICE,
    mongoInitUser: process.env.MONGO_INITDB_ROOT_USERNAME,
    mongoInitPass: process.env.MONGO_INITDB_ROOT_PASSWORD,
    mongoInitDB: process.env.MONGO_INITDB_DATABASE,
    nodeEnv: process.env.NODE_ENV
};
