podTemplate(
    cloud: 'kubernetes-development',
    namespace: 'jenkins-blue',
    label: 'jenkins-slave-java',
    imagePullSecrets: ['acr-credentials'],
    containers: [
        containerTemplate(name: 'maven', image: 'maven:3-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'docker', image: 'docker:stable-git', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.7.10', ttyEnabled: true, command: 'cat')
    ],
    volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repository-cache', readOnly: false),
        hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
    ]
) {

    node('jenkins-slave-java') {
        def image_name
        def image_tag

        stage('Checkout SCM') {
            def scmInfo = checkout(scm)

            echo "scm info : ${scmInfo} "
            image_tag = "${scmInfo.GIT_BRANCH}-${scmInfo.GIT_COMMIT[0..7]}"
        }

    }
}
