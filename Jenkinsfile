#!groovy
build('easyway', 'docker-host') {
    checkoutRepo()
    loadBuildUtils()

    env.skipDtrack = true
    
    def javaLibPipeline
    runStage('load javaLib pipeline') {
        javaLibPipeline = load("build_utils/jenkins_lib/pipeJavaLibOutsideImage.groovy")
    }

    def buildImageTag = "fcf116dd775cc2e91bffb6a36835754e3f2d5321" //not used, why we didn’t remove the build image?
    javaLibPipeline(buildImageTag)
}
