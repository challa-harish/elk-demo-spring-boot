podTemplate(
    cloud: 'kubernetes-development',
    namespace: 'jenkins-blue',
    label: 'jenkins-slave-java',
    imagePullSecrets: ['acr-credentials'],
    containers: [
        containerTemplate(name: 'maven', image: 'maven:3.5.4-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
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
            echo "scmInfo : ${scmInfo}"  
            echo "Branch Name : ${scmInfo.GIT_BRANCH}"
            image_tag = "${scmInfo.GIT_BRANCH}-${scmInfo.GIT_COMMIT[0..7]}"
            echo "image_tag : ${image_tag}"

            
        }

       // stage('Run Unit Tests') {
         //   container('maven') {
            //    sh "mvn clean test"
            //    junit '**/target/*-reports/TEST-*.xml'
           // }
        //}

        stage('Compile') {
            container('maven') {
                def mvnInfo = readMavenPom()
                echo "ArtifactId : ${mvnInfo.getArtifactId()}"
                sh 'mvn clean package'
                echo "Docker Registry : ${ACR_LOGINSERVER}"  
                 
                image_name = "${ACR_LOGINSERVER}/${mvnInfo.getArtifactId()}"
            }
        }

        stage('Build Docker Image') {
            container('docker') {
                // Cannot use: docker.build(mvnInfo.getArtifactId())
                // Because: https://issues.jenkins-ci.org/browse/JENKINS-46447
                sh "docker build -t ${image_name} ."
                sh "docker tag ${image_name} ${image_name}:${image_tag}"
            }
        }

        stage('Push Docker Image') {
            container('docker') {
                withDockerRegistry([credentialsId: 'acr-credentials']) {
                    sh "docker push ${image_name}:${image_tag}"
                }
            }
        }

        stage('Preparing Deployment scripts') {
            container('kubectl') {
                // Inject image and tag values in deployment scripts
                withEnv(["IMAGE_NAME=${image_name}", "IMAGE_TAG=${image_tag}"]) {
                    def files = findFiles(glob: 'infrastructure/kubernetes/**/*.yaml')

                    for (def file : files) {
                        sh "sed -i 's,\${IMAGE_NAME},${IMAGE_NAME},g;s,\${IMAGE_TAG},${IMAGE_TAG},g' ${file.path}"
                    }
                }
            }
        }

        stage('Deploy to Development') {
            container('kubectl') {
                withCredentials([file(credentialsId: 'kube-config', variable: 'KUBE_CONFIG')]) {
                    def kubectl = "kubectl --kubeconfig=${KUBE_CONFIG} --context=kubernetes-development"

                    sh "${kubectl} apply -f ./infrastructure/kubernetes/development"

                    // Consider verifying if at least deployment got successfully done.
                    // Example: kubectl rollout status -n <namespace> deployment/<deployment_name>
                }
            }
        }

        stage('Deploy to Canary') {
            // Only deploy if image_tag is a git tag (regex?)
            echo 'Canary deployment!'
        }

        stage('Deploy to Production') {
            timeout(time: 4, unit:'HOURS') {
                input(message: 'Approve deployment to production?')
            }

            // Only deploy if image_tag is a git tag
            echo 'Production deployment approved!'
        }
    }
}
