#!/usr/bin/groovy

/**
 * Wraps the code in a podTemplate with the JNLP container.
 * @param parameters    Parameters to customize the JNLP container.
 * @param body          The code to wrap.
 * @return
 */
def call(Map parameters = [:], body) {

  def defaultLabel = buildId('jnlp')
  def label = parameters.get('label', defaultLabel)
  def name = parameters.get('name', buildId())

  def cloud = parameters.get('cloud', 'openshift')
  def jnlpCommand = parameters.get('jnlpCommand', '/usr/local/bin/run-jnlp-client')
  def inheritFrom = parameters.get('inheritFrom', 'base')
  def namespace = parameters.get('namespace', 'syndesis-ci')
  def serviceAccount = parameters.get('serviceAccount', '')

  def idleMinutes = parameters.get('idle', 10)
  def internalRegistry = parameters.get('internalRegistry', findInternalRegistry(namespace: "$namespace", imagestream: "jenkins-slave-full-centos7"))
  def image = !internalRegistry.isEmpty() ? parameters.get('image', "${internalRegistry}/${namespace}/jenkins-slave-full-centos7:1.0.8") : parameters.get('image', 'syndesis/jenkins-slave-full-centos7:1.0.8')


  podTemplate(cloud: "${cloud}", name: "${name}", namespace: "${namespace}", label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
              idleMinutesStr: "${idleMinutes}",
              containers: [containerTemplate(name: 'jnlp', image: "${image}", command: "${jnlpCommand}", args: '${computer.jnlpmac} ${computer.name}')]) {
    body()
  }
}

