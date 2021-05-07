import { nodeEnv, imageVersion, port, dbService, mongoInitUser, mongoInitPass, mongoInitDB } from './config';
import express from 'express';
import { MongoClient } from 'mongodb';

console.log(`NODE_ENV: ${nodeEnv}`);
console.log(`Image version: ${imageVersion}`);
console.log(`dbService: ${dbService} | port: ${port} \nmongoInitUser: ${mongoInitUser} | mongoInitPass: ${mongoInitPass} | mongoInitDB: ${mongoInitDB}\n`);

const app = express();
const PORT = port || 4000;
const uri = `mongodb://${mongoInitUser}:${mongoInitPass}@${dbService}:27017?authSource=admin`;
const optionsMongo = {
    useNewUrlParser: true,
    useUnifiedTopology: true
};
const client = new MongoClient(uri, optionsMongo);

async function run() {
    try {
        await client.connect();
        console.log("Mongo connected: ", client.isConnected() ?? false);

        const database = client.db(mongoInitDB);
        const collection = database.collection('movies');


        // Query for a movie that has the title 'Back to the Future'
        const query = { name: 'Scary Movie' };
        const movie = await collection.findOne(query);
        console.log(movie);

    } finally {
        // Ensures that the client will close when you finish/error
        await client.close();
    }
}
run().catch(console.dir);

app.get('/', (req, res) => {
    res.json({ message: `API build: #${imageVersion} from env: ${nodeEnv}`});
});

app.listen(PORT, () => {
    console.log(`Your server is running on PORT: ${PORT}`);
});
