package solr.supervisor.aws

import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest

/**
 * Created by ren on 10/13/15.
 */
class AWSTerminate extends AWSClient {
  def terminate(id: String) = {
    val treq = new TerminateInstanceInAutoScalingGroupRequest()
      .withShouldDecrementDesiredCapacity(true)
      .withInstanceId(id)
    as.terminateInstanceInAutoScalingGroup(treq)
  }
}
