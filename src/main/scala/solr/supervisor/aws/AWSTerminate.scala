package solr.supervisor.aws

import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest

trait AWSTerminate extends AWSClient {
  def terminate(id: String) = {
    val treq = new TerminateInstanceInAutoScalingGroupRequest()
      .withShouldDecrementDesiredCapacity(true)
      .withInstanceId(id)
    as.terminateInstanceInAutoScalingGroup(treq)
  }
}
