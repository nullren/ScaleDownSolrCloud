package solr.supervisor.aws

/**
 * Created by ren on 10/13/15.
 */
object ScaleDownSolrCloud {
  def main(args: Array[String]): Unit = {
    val results = terminate("items_4s2r_2015093001", 1)
    println(results.toString)
  }

  def terminate(collection: String, nodesPerShard: Int) = {
    val awsClient = new AWSLookup
    val zk = ZooKeeper(Config.solrZk)
    val solrNodes = zk.stateMap.filter(_.collection == collection).groupBy(_.shard)
    zk.client.close()

    val chosenOnes = solrNodes.values
      .flatMap(b => scala.util.Random.shuffle(b.toSeq).init.take(nodesPerShard))
      .map(_.node.split(":").head)
      .flatMap(awsClient.byIp)

    println(chosenOnes)

    val terminator = new AWSTerminate
    chosenOnes.map(terminator.terminate)
  }
}
