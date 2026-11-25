
pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    environment {
        DOCKER_IMAGE = 'numeric'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Git Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Java and Maven Versions') {
            steps {
                sh '''
                    java -version
                    javac -version
                    mvn -version
                    docker --version
                '''
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Testing') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Code Coverage') {
            steps {
                sh 'mvn jacoco:report'
            }
        }

        stage('Build Artifact') {
            steps {
                sh 'mvn package -DskipTests'

                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true
                )
            }
        }

        stage('Verify') {
            steps {
                sh 'mvn verify -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )
                ]) {
                    sh '''
                        docker build \
                            -t ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE}:${BUILD_NUMBER} \
                            -t ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE}:latest .
                    '''
                }
            }
        }

        stage('Docker Push to Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    ]) {
                    sh '''
                        set +x

                        echo "$DOCKERHUB_PASSWORD" | docker login \
                            --username "$DOCKERHUB_USERNAME" \
                            --password-stdin

                        trap 'docker logout >/dev/null 2>&1 || true' EXIT

                        docker push ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE}:${BUILD_NUMBER}

                        docker push ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE}:latest
                    '''
                }
            }
        }
    }

    post {
        always {
            junit(
                allowEmptyResults: true,
                testResults: 'target/surefire-reports/*.xml'
            )

            archiveArtifacts(
                allowEmptyArchive: true,
                artifacts: 'target/site/jacoco/**'
            )
        }

        success {
            echo 'BUILD SUCCESSFUL: Java 21, Maven, Tests, JaCoCo and Docker Push completed!'
        }

        failure {
            echo 'BUILD FAILED! Check Jenkins Console Output.'
        }

        cleanup {
            echo 'Pipeline execution completed.'
        }
    }
}
