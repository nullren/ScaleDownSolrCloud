package solr.supervisor.aws

import scala.collection.JavaConversions._

trait AWSLookup extends AWSClient {

  lazy val instancesByIp = ec2.describeInstances().getReservations.toSeq
    .flatMap(_.getInstances.toSeq)
    .map(i => i.getPrivateIpAddress -> i.getInstanceId).toMap

  def byIp(ip: String): Option[String] = instancesByIp.get(ip)
}
