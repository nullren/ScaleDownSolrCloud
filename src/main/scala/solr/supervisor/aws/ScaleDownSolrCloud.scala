package solr.supervisor.aws

import scala.util.control.Exception._

object ScaleDownSolrCloud {

  def main(args: Array[String]): Unit = {
    val opts = parseOpts(Map.empty[String, String], args.toList)
    val fields = for {
      regn <- opts.get("-r")
      zook <- opts.get("-z")
      coll <- opts.get("-c")
      nrep <- opts.get("-n").flatMap(n => allCatch.opt(n.toInt))
    } yield (regn, zook, coll, nrep)

    if (opts.get("-h").isDefined || fields.isEmpty) usage()
    else {
      val results = terminate _ tupled fields.get
      println(results.toString)
    }
  }

  // you really like this
  def usage() = List(
    "",
    "  DESCRIPTION",
    "",
    "This utility will remove replicas from each shard in a Solr Cloud",
    "collection. It does so looking up the AWS instance IDs for replics",
    "listed in ZooKeeper for a given collection then sends the",
    "`--should-decrement-desired-capacity` flag when terminating the",
    "instance in an autoscaling group.",
    "",
    "  FLAGS",
    "",
    "    -h",
    "         This helpful output.",
    "",
    "    -z ZOOKEEPER",
    "         ZooKeeper connect string. (Required)",
    "         Eg, 10.0.0.1:2181,10.0.0.2:2181,10.0.0.3:2181/solr",
    "",
    "    -r REGION",
    "         AWS Region to connect to. (Required)",
    "         Eg, us-west-1",
    "",
    "    -n NUMBER",
    "         The number of replicas to remove from each shard.",
    "         (Required)",
    "",
    "    -c COLLECTION",
    "         Name of Solr Cloud collection to remove shards from.",
    "         (Required)",
    ""
  ).foreach(println)

  // you also really like this
  def parseOpts(m: Map[String, String], args: List[String]): Map[String, String] = args match {
    case Nil => m
    case "-z" :: value :: tail => parseOpts(m ++ Map("-z" -> value), tail)
    case "-r" :: value :: tail => parseOpts(m ++ Map("-r" -> value), tail)
    case "-n" :: value :: tail => parseOpts(m ++ Map("-n" -> value), tail)
    case "-c" :: value :: tail => parseOpts(m ++ Map("-c" -> value), tail)
    case "-h" :: tail => parseOpts(m ++ Map("-h" -> "yup"), tail)
    case "--help" :: tail => parseOpts(m ++ Map("-h" -> "yup"), tail)
    case _ :: tail => parseOpts(m, tail)
  }

  case class AWSThing(region: String) extends AWSLookup with AWSTerminate

  def terminate(region: String, zookeeper: String, collection: String, nodesPerShard: Int) = {
    val aws = AWSThing(region)
    val zk = ZooKeeper(zookeeper)

    val solrNodes = zk.stateMap.filter(_.collection == collection).groupBy(_.shard)
    zk.client.close()

    // for each shard, take nodesPerShard always leaving at least 1 (why the init is used), flatten
    // into single list of the chosen ones.
    val chosenOnes = solrNodes.values
      .flatMap(b => scala.util.Random.shuffle(b.toSeq).init.take(nodesPerShard))
      .map(_.node.split(":").head)
      .flatMap(aws.byIp)

    // TODO: sometimes not every shard has chosen ones?

    println(chosenOnes)
    chosenOnes.map(aws.terminate)
  }
}
