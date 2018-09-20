podTemplate(
    cloud: 'kubernetes-development',
    namespace: 'jenkins-blue',
    label: 'jenkins-slave-java',
    imagePullSecrets: ['acr-credentials'],
    containers: [
       containerTemplate(name: 'git', image: 'alpine/git', ttyEnabled: true, command: 'cat')
    ],
    volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repository-cache', readOnly: false),
        hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
    ]
) {

    node('jenkins-slave-java') {
       
		 stage('Clone repository') {

            container('git') {

                sh 'whoami'

                sh 'hostname -i'

          //      sh 'git clone -b master https://github.com/lvthillo/hello-world-war.git'

            }

        }
       

        
    }
}
