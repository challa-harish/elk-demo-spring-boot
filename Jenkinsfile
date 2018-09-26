def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(
    cloud: 'kubernetes',
    namespace: 'development',
    label: label,
 //   imagePullSecrets: ['dockerhub-statflo-development'],
    containers: [
        containerTemplate(name: 'maven', image: 'wsibprivateregistry.azurecr.io/maven:3-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'docker', image: 'wsibprivateregistry.azurecr.io/docker:stable-git', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'kubectl', image: 'wsibprivateregistry.azurecr.io/k8s-kubectl:v1.7.10', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'helm', image: 'wsibprivateregistry.azurecr.io/k8s-helm:latest', ttyEnabled: true, command: 'cat')
        
    ],
    volumes: [
        hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock'),
        hostPathVolume(hostPath: '/root/.m2/repository', mountPath: '/root/.m2/repository')
    ]
) {

    node(label) {
      def myRepo = checkout scm
      sh 'pwd'
      def gitCommit = myRepo.GIT_COMMIT
      def gitBranch = myRepo.GIT_BRANCH
 //     def shortGitCommit = "${gitCommit[0..10]}"
 //     def previousGitCommit = sh(script: "git rev-parse ${gitCommit}~", returnStdout: true)

        stage('Compile') {
            container('maven') {
                sh 'pwd' 
                def mvnInfo = readMavenPom()
               echo "maven info ${mvnInfo}"
                sh 'mvn clean install -DskipTests'

                image_name = "wsibprivateregistry.azurecr.io/${mvnInfo.getArtifactId()}"
                echo image_name
            }
        }

        stage('Build Docker Image') {
            container('docker') {
                echo 'docker'
                // Cannot use: docker.build(mvnInfo.getArtifactId())
                // Because: https://issues.jenkins-ci.org/browse/JENKINS-46447
                //sh "docker build -t ${image_name} ."
               // sh "docker tag ${image_name} ${image_name}:${image_tag}"
            }
        }

        stage('Push Docker Image') {
            container('docker') {
                echo 'push'
                //withDockerRegistry([credentialsId: 'acr-credentials']) {
                    //sh "docker push ${image_name}:${image_tag}"
              //  }
            }
        }

        stage('Preparing Deployment scripts') {
            container('kubectl') {
                 echo 'preparation of deployment scripts!'
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
                //withCredentials([file(credentialsId: 'kube-config', variable: 'KUBE_CONFIG')]) {
                    //def kubectl = "kubectl --kubeconfig=${KUBE_CONFIG} --context=kubernetes-development"
                     echo 'deploy to deployment!'

                    //sh "${kubectl} apply -f ./infrastructure/kubernetes/development"

                    // Consider verifying if at least deployment got successfully done.
                    // Example: kubectl rollout status -n <namespace> deployment/<deployment_name>
               
            //}
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
