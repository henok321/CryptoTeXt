def gitUrl = 'git@github.com:henok321/CryptoTeXt.git'
def branch = 'master'

stage('scm') {
    node {
        git branch: branch, credentialsId: 'd304b5f9-e406-4e65-a876-43f95fcf84df', url: gitUrl
    }
}

stage('build') {
    node {
        def mvnHome = tool name: 'Maven 3', type: 'maven'
        sh "${mvnHome}/bin/mvn clean jfx:jar"
    }
}
