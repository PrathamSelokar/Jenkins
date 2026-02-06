pipeline {
    agent { label 'slave' }  

    stages {

        stage('git-pull') {
            steps {
                    git branch: 'main', url: 'https://github.com/Anilbamnote/student-ui-app.git'
            }
        }

        stage('Build') {
            steps {
                sh '/opt/maven/bin/mvn clean package'
            }
        }

        stage('Test') {
            steps {
                sh '/opt/maven/bin/mvn test'
            }
        }

        
        stage('SonarQube Analysis') {
    steps {
        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
            withSonarQubeEnv('sonar-server') {
                sh '''
                /opt/maven/bin/mvn sonar:sonar \
                -Dsonar.projectKey=my-student1 \
                -Dsonar.host.url=http://13.251.81.239:9000 \
                -Dsonar.login=$SONAR_TOKEN
                '''
            }

            timeout(time: 10, unit: 'MINUTES') {
                waitForQualityGate abortPipeline: false
            }
        }
    }
}


        stage('Artifact_upload to_s3') {
           steps {
        sh '''
        aws s3 cp target/studentapp-2.2-SNAPSHOT.war s3://sonar-bucket-123/
        '''
          }
       }


        stage('Deploy') {
            steps {
                echo "deploy success"
            }
        }
    }
}




