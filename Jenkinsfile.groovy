node
{
    stage("checkout"){
        
        git credentialsId: 'a449156b-6917-4a4c-b4b7-4598cbb73788', url: 'https://github.com/balaji2003engg/helloworld.git'
    }
    
    stage("build"){
       sh  "chmod 755 gradlew"
       sh "./gradlew clean build"

    }
    
    stage("publish the artifactory"){
        sh "chmod -R 755 $WORKSPACE"
        def server = Artifactory.newServer url: 'https://chbalaji080186.jfrog.io/artifactory', username: 'sanjeevkhadka86@gmail.com', password: 'Password1!'
        def uploadSpec = """{
              "files": [
            {
                "pattern": "$WORKSPACE/build/libs/HelloWorld-0.0.1-SNAPSHOT.war",
                "target": "libs-snapshot-local/application1/application1-${version}.${BUILD_NUMBER}.war"
           }
                     ]
}"""
server.upload spec: uploadSpec
    }
    stage("deploy via anisble tower"){
        ansibleTower(
            towerServer: 'ansibleTower',
            jobTemplate: 'tomcatdeployment',
            importTowerLogs: true,
            inventory: 'traininginventory',
            jobTags: '',
            limit: '',
            removeColor: false,
            verbose: true,
            credential: '',
            extraVars: '''---
            env: dev
            version: ${version}.${BUILD_NUMBER}'''
        )
    }
    
}
