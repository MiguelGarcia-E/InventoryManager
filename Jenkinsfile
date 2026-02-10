pipeline {
    agent none
    stages {
        stage ('Build Backend'){
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-17'
                }
            }
            steps {
                dir ('back-end-inventory-manager') {
                    sh 'mvn -B test'
                }
            }
            post {
                always {
                    dir('back-end-inventory-manager') {
                        // Publica los resultados de las pruebas unitarias
                        junit '**/target/surefire-reports/*.xml'
                    }
                }
            }

        }
    }
}