pipeline {
    agent none
    stages {
        stage ('Build Backend'){
            steps {
                sh 'docker version'
                //Entra a la carpeta especificada
                // dir('back-end-inventory-manager'){
                //     // Ejecuta comando de contruccion de Maven
                //     // sh 'mvn -B test'
                //     sh 'docker version'
                }
            }
            post {
                always {
                    // dir('back-end-inventory-manager') {
                    //     // Publica los resultados de las pruebas unitarias
                    //     // junit '**/target/surefire-reports/*.xml'
                    // }
                }
            }
        }
    }
}