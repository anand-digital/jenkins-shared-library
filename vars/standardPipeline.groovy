def call(body) {

	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
	body.delegate = config
	body()

	node {
		// Clean workspace before doing anything
		deleteDir()

		try {
			stage ('Checkout SCM') {   
				checkout scm
			}		
			stage ('Compile Stage') {   
				sh "echo 'building ${config.projectName} ...'"
				withMaven(maven : 'maven-3.9.1') {
					sh 'mvn clean compile'
				}
			}
			stage ('Testing Stage') {
				withMaven(maven : 'maven-3.9.1') {
					sh 'mvn test'
				}
			}
			stage ('Deployment Stage') {
				withMaven(maven : 'maven-3.9.1') {
					sh 'mvn deploy'
				}
			}			       
		} catch (err) {
			currentBuild.result = 'FAILED'
			throw err
		}
	}
}
