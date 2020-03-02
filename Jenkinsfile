#!groovy
build('easyway', 'docker-host') {
    checkoutRepo()
    loadBuildUtils()

    def javaLibPipeline
    runStage('load javaLib pipeline') {
        javaLibPipeline = load("build_utils/jenkins_lib/pipeJavaLibOutsideImage.groovy")
    }

    def serviceName = env.REPO_NAME
    def mvnArgs = '-DjvmArgs="-Xmx256m"'
    def useJava11 = true
    def registry = 'dr2.rbkmoney.com'
    def registryCredsId = 'jenkins_harbor'

    javaLibPipeline(serviceName, useJava11, mvnArgs, registry, registryCredsId)
}