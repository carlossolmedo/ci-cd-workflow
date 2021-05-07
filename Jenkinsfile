#!/usr/bin/env groovy

def gv

pipeline {
    agent any
    options {
        timestamps()
    }
    environment {
        DOMAIN = 'thefornax.fr'
        REGISTRY = 'nexus.thefornax.fr:8082'
        IMAGE_NAME = 'api_fatboar'
        MONGO_DATA_DIR = '/data/db'
        DIR_IMAGE = './docker/api/Dockerfile'
        DIR_DOCKER_DEV = './docker/docker-compose.dev.yml'
        DIR_DOCKER_STAGE = './docker/docker-compose.stage.yml'
        DIR_DOCKER_PROD = './docker/docker-compose.prod.yml'
        DIR_DEPLOY_DEV = './deploy/docker-compose.dev.yml'
        DIR_DEPLOY_STAGE = './deploy/docker-compose.stage.yml'
        DIR_DEPLOY_PROD = './deploy/docker-compose.prod.yml'
    }

    stages {
        stage("Init") {
            steps {
                script {
                    gv = load "scripts/workflow.groovy"
                }
            }
        }
        stage("Build") {
            steps {
                script {
                    gv.buildApp()
                }
            }
        }
        stage("Tests") {
            steps {
                script {
                    gv.testApp()
                }
            }
        }
        stage("Deploy") {
            steps {
                script {
                    gv.deployApp()
                }
            }
        }
    }

    post {
        always {
            echo 'Process finished'
        }
        success {
            deleteDir()
            echo 'Build Succeed ! 🥳'
        }
        failure {
            sh 'scripts/delete-current-image.sh'
            deleteDir()
            echo 'Build failed 🤐'
        }
        aborted {
            sh 'scripts/delete-current-image.sh'
            deleteDir()
            echo 'Build aborted'
        }
        unstable {
            echo 'Build unstable'
        }
        changed {
            echo 'There have been changes in this version'
        }
    }
}
