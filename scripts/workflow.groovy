def buildApp() {
    echo "Build image"
    sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} -f ${DIR_IMAGE} --no-cache ."
}

def testApp() {
    echo "Check branch"
    def envVariables = [:]

    switch (env.BRANCH_NAME) {
        case "develop":
            envVariables.imageVersion = "${BUILD_NUMBER}"
            envVariables.imageTag = "${BUILD_NUMBER}"
            envVariables.dbService = "db-dev"
            envVariables.apiHost = "`api.dev.fatboar-${BUILD_NUMBER}.${DOMAIN}`"
            envVariables.port = 4113
            envVariables.mongoInitDB = "fatboar_dev"
            envVariables.credentialsId = "mongo-user-develop"
            break

        case "stage":
            envVariables.imageVersion = "${BUILD_NUMBER}"
            envVariables.imageTag = "${BUILD_NUMBER}"
            envVariables.dbService = "db-stage"
            envVariables.apiHost = "`api.stage.fatboar-${BUILD_NUMBER}.${DOMAIN}`"
            envVariables.port = 4112
            envVariables.mongoInitDB = "fatboar_stage"
            envVariables.credentialsId = "mongo-user-stage"
            break

        default:
            envVariables.imageVersion = "${BUILD_NUMBER}"
            envVariables.imageTag = "${BUILD_NUMBER}"
            envVariables.dbService = "db"
            envVariables.apiHost = "`api.fatboar-${BUILD_NUMBER}.${DOMAIN}`"
            envVariables.port = 4111
            envVariables.mongoInitDB = "fatboar"
            envVariables.credentialsId = "mongo-user-prod"
    }

    env.IMAGE_VERSION = envVariables.imageVersion
    env.IMAGE_TAG = envVariables.imageTag
    env.DB_SERVICE = envVariables.dbService
    env.API_HOST = envVariables.apiHost
    env.PORT = envVariables.port
    env.MONGO_INITDB_DATABASE = envVariables.mongoInitDB

    withCredentials([
        usernamePassword(credentialsId: envVariables.credentialsId, usernameVariable: 'MONGO_INITDB_ROOT_USERNAME', passwordVariable: 'MONGO_INITDB_ROOT_PASSWORD')
    ]) {
        echo "Run tests"

        switch(env.BRANCH_NAME) {
            case "develop":
                sh "docker-compose -f ${DIR_DOCKER_DEV} up -d"
                sleep(5)
                sh "docker logs jenkins_api-dev"
                sh "docker-compose -f ${DIR_DOCKER_DEV} down -v"
            break
            case "stage":
                sh "docker-compose -f ${DIR_DOCKER_STAGE} up -d"
                sleep(5)
                sh "docker logs jenkins_api-stage"
                sh "docker-compose -f ${DIR_DOCKER_STAGE} down -v"
            break
            default:
                sh "docker-compose -f ${DIR_DOCKER_PROD} up -d"
                sleep(5)
                sh "docker logs jenkins_api"
                sh "docker-compose -f ${DIR_DOCKER_PROD} down -v"
        }
    }
}

def deployApp() {
    switch(env.BRANCH_NAME) {
        case "develop":
            echo "Deploy for development"
            input message: "Do you want to push this version into development?", ok: "Next", id: "deploy-dev"

            env.IMAGE_TAG = "latest-dev"
            env.API_HOST = "`api.dev.fatboar.${DOMAIN}`"
            env.PORT = 4002

            docker.withRegistry("http://${REGISTRY}", "nexus-developer") {
                def apiFatboarDev = docker.image("${IMAGE_NAME}:${BUILD_NUMBER}")
                apiFatboarDev.push('latest-dev')
            }

            withCredentials([
                usernamePassword(credentialsId: 'mongo-user-develop', usernameVariable: 'MONGO_INITDB_ROOT_USERNAME', passwordVariable: 'MONGO_INITDB_ROOT_PASSWORD')
            ]) {
                sh "docker-compose -f ${DIR_DEPLOY_DEV} up -d --build"
                sleep(5)
                sh "docker logs deploy-dev_api"
            }

            echo "Delete old images"
            sh "scripts/delete-images.sh ${IMAGE_NAME}"
        break

        case "stage":
            echo "Deploy for stage"
            input message: "Do you want to push this version into stage?", ok: "Next", id: "deploy-stage"

            env.IMAGE_TAG = "latest-stage"
            env.API_HOST = "`api.stage.fatboar.${DOMAIN}`"
            env.PORT = 4001

            docker.withRegistry("http://${REGISTRY}", "nexus-developer") {
                def apiFatboarStage = docker.image("${IMAGE_NAME}:${BUILD_NUMBER}")
                apiFatboarStage.push('latest-stage')
            }

            withCredentials([
                usernamePassword(credentialsId: 'mongo-user-stage', usernameVariable: 'MONGO_INITDB_ROOT_USERNAME', passwordVariable: 'MONGO_INITDB_ROOT_PASSWORD')
            ]) {
                sh "docker-compose -f ${DIR_DEPLOY_STAGE} up -d"
                sleep(5)
                sh "docker logs deploy-stage_api"
            }

            echo "Delete old images"
            sh "scripts/delete-images.sh ${IMAGE_NAME}"
        break

        default:
            echo "Deploy for production"
            input message: "Do you want to push this version into production?", ok: "Next", id: "deploy-prod"

            env.IMAGE_TAG = "latest"
            env.API_HOST = "`api.fatboar.${DOMAIN}`"
            env.PORT = 4000

            docker.withRegistry("http://${REGISTRY}", "nexus-developer") {
                def apiFatboar = docker.image("${IMAGE_NAME}:${BUILD_NUMBER}")
                apiFatboar.push('latest')
            }

            withCredentials([
                usernamePassword(credentialsId: 'mongo-user-prod', usernameVariable: 'MONGO_INITDB_ROOT_USERNAME', passwordVariable: 'MONGO_INITDB_ROOT_PASSWORD')
            ]) {
                sh "docker-compose -f ${DIR_DEPLOY_PROD} up -d"
                sleep(5)
                sh "docker logs fatboar-api"
            }

            echo "Delete old images"
            sh "scripts/delete-images.sh ${IMAGE_NAME}"
    }
}

return this
