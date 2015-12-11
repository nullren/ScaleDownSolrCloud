package solr.supervisor.aws

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.ec2.AmazonEC2Client

trait AWSClient {
  val region: String

  lazy val ec2 = {
    val client = new AmazonEC2Client()
    client.setEndpoint(s"ec2.$region.amazonaws.com")
    client
  }

  lazy val as = {
    val client = new AmazonAutoScalingClient()
    client.setEndpoint(s"autoscaling.$region.amazonaws.com")
    client
  }
}
