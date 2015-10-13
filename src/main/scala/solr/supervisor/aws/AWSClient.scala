package solr.supervisor.aws

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.ec2.AmazonEC2Client

/**
 * Created by ren on 10/13/15.
 */
trait AWSClient {

  lazy val ec2 = {
    val client = new AmazonEC2Client()
    client.setEndpoint("ec2.us-west-1.amazonaws.com")
    client
  }

  lazy val as = {
    val client = new AmazonAutoScalingClient()
    client.setEndpoint("autoscaling.us-west-1.amazonaws.com")
    client
  }
}
