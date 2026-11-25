
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

        // Stage 1: Checkout source code
        stage('Git Checkout') {
            steps {
                https://github.com/chaitan28/kubernetes-devops-security.git
            }
        }

        // Stage 2: Check Java, Maven and Docker
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

        // Stage 3: Compile application
        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        // Stage 4: Execute unit tests
        stage('Unit Testing') {
            steps {
                sh 'mvn test'
            }
        }

        // Stage 5: Generate JaCoCo coverage report
        stage('Code Coverage') {
            steps {
                sh 'mvn jacoco:report'
            }
        }

        // Stage 6: Package Spring Boot application
        stage('Build Artifact') {
            steps {
                sh 'mvn package -DskipTests'

                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true
                )
            }
        }

        // Stage 7: Verify Maven build
        stage('Verify') {
            steps {
                sh 'mvn verify -DskipTests'
            }
        }

        // Stage 8: Build Docker image
        stage('Docker Build') {
            steps {
                script {
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
        }

        // Stage 9: Push Docker image to Docker Hub
        stage('Docker Push to Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKERHUB_USERNAME',
                        passwordVariable: 'DOCKERHUB_PASSWORD'
                    )
                ]) {
                    sh '''
                        set +x

                        echo "$DOCKERHUB_PASSWORD" | docker login \
                            --username "$DOCKERHUB_USERNAME" \
                            --password-stdin

                        # Remove Docker login credentials after this stage
                        trap 'docker logout >/dev/null 2>&1 || true' EXIT

                        docker push ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE}:${BUILD_NUMBER}

                        docker push ${DOCKERHUB_USERNAME}/${DOCKER_IMAGE}:latest
                    '''
                }
            }
        }
    }

    post {

        // Always publish test and coverage reports
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
            echo '''
                ==========================================
                BUILD SUCCESSFUL!
                Spring Boot Java 21
                Unit Testing Passed
                JaCoCo Coverage Generated
                Docker Image Built
                Docker Image Pushed to Docker Hub
                ==========================================
            '''
        }

        failure {
            echo 'BUILD FAILED! Check Jenkins Console Output.'
        }

        cleanup {
            echo 'Jenkins pipeline execution completed.'
        }
    }
}
