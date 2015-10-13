package solr.supervisor.aws

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by ren on 10/13/15.
 */
class AWSLookup extends AWSClient {

  lazy val instancesByIp = ec2.describeInstances().getReservations.toSeq
    .flatMap(_.getInstances.toSeq)
    .map(i => i.getPrivateIpAddress -> i.getInstanceId).toMap

  def byIp(ip: String): Option[String] = instancesByIp.get(ip)
  def asyncByIp(ip: String): Future[Option[String]] = Future { instancesByIp.get(ip) }
}
