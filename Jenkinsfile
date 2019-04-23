#!groovy
build('easyway', 'docker-host') {
    checkoutRepo()
    loadBuildUtils()

    String serviceName= env.REPO_NAME
    String mvnArgs = '-DjvmArgs="-Xmx256m"'
    Boolean useJava11 = false
    String registry = "dr2.rbkmoney.com"
    String registryCredentialsId = "jenkins_harbor"

    // service name - usually equals artifactId
    env.SERVICE_NAME = serviceName
    // use java11 or use std JAVA_HOME (java8)
    env.JAVA_HOME = useJava11 ? "JAVA_HOME=/opt/openjdk-bin-11.0.1_p13 " : ""

    // mvnArgs - arguments for mvn. For example: ' -DjvmArgs="-Xmx256m" '
    env.REGISTRY = registry

    // Run mvn and generate docker file
    runStage('Running Maven build') {
        withCredentials([[$class: 'FileBinding', credentialsId: 'java-maven-settings.xml', variable: 'SETTINGS_XML']]) {
            def mvn_command_arguments = ' --batch-mode --settings  $SETTINGS_XML -P ci ' +
                    " -Dgit.branch=${env.BRANCH_NAME} " +
                    " ${mvnArgs}"
            if (env.BRANCH_NAME == 'master') {
                sh env.JAVA_HOME + 'mvn deploy' + mvn_command_arguments
            } else {
                sh env.JAVA_HOME + 'mvn package' + mvn_command_arguments
            }
        }
    }
}
