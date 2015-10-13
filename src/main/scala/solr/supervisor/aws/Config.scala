package solr.supervisor.aws

/**
 * Created by ren on 10/13/15.
 */
object Config {
  lazy val solrZk = Option(System.getProperty("solr.zk")).getOrElse("127.0.0.1:2181/solr")
  lazy val solrHost = Option(System.getProperty("solr.host")).getOrElse("127.0.0.1:8983")
}