podTemplate(
    cloud: 'kubernetes',
    namespace: 'jenkins',
    label: 'jenkins-pipeline',
 //   imagePullSecrets: ['dockerhub-statflo-development'],
    containers: [
        containerTemplate(name: 'jnlp', image: 'lachlanevenson/jnlp-slave:3.10-1-alpine', args: '${computer.jnlpmac} ${computer.name}', workingDir: '/home/jenkins', resourceRequestCpu: '200m', resourceLimitCpu: '300m', resourceRequestMemory: '256Mi', resourceLimitMemory: '512Mi'),
        containerTemplate(name: 'maven', image: 'maven:3-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'docker', image: 'docker:stable-git', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.7.10', ttyEnabled: true, command: 'cat')
    ],
    volumes: [
        persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repository-cache', readOnly: false),
        hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock')
    ]
) {

    node('jenkins-pipeline') {
        def image_name
        def image_tag

        stage('Checkout SCM') {
            def scmInfo = checkout(scm)

            image_tag = "${scmInfo.GIT_BRANCH}-${scmInfo.GIT_COMMIT[0..7]}"
        }

        stage('Run Unit Tests') {
            container('maven') {
                sh "mvn clean test"
                junit '**/target/*-reports/TEST-*.xml'
            }
        }

        stage('Compile') {
            container('maven') {
                def mvnInfo = readMavenPom()

                sh 'mvn clean package -DskipTests'

                image_name = "statflo/${mvnInfo.getArtifactId()}"
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
                withDockerRegistry([credentialsId: 'dockerhub-statflo-development']) {
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
