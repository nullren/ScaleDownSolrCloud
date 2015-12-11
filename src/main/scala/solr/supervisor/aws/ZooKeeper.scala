package solr.supervisor.aws

import net.liftweb.json._
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry

import scala.collection.JavaConversions._

case class ZooKeeper(solrZk: String) {
  lazy val client = {
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val client = CuratorFrameworkFactory.builder().connectString(solrZk).retryPolicy(retryPolicy).build()
    client.start()
    client
  }

  case class Record(node: String, core: String, shard: String, collection: String, replicationFactor: Int)

  // blocking
  def liveNodes = client.getChildren.forPath("/live_nodes").toSet
  def collections = client.getChildren.forPath("/collections")
  def stateBytes(collection: String) = client.getData.forPath(s"/collections/$collection/state.json")
  def stateJson = stateBytes _ andThen (new String(_)) andThen parse

  def statem(c: String) = collectionMap(c, stateJson(c))
  def stateMap = collections.flatMap(statem)

  def extractRepFactor(collection: String, j: JValue) = {
    val JString(rf) = j \ collection \ "replicationFactor"
    rf.toInt
  }
  def extractShards(collection: String, j: JValue) = {
    val JObject(shards) = j \ collection \ "shards"
    shards
  }
  def extractReplicas(shard: JField) = {
    val JObject(replicas) = shard \ "replicas"
    replicas
  }
  def transformReplica(field: JField) = {
    val core = field.name
    val JString(node) = field \ "node_name"
    node -> core
  }
  def getReplicas(s: JField) = extractReplicas(s).map(transformReplica _ andThen (x => (x._1, x._2, s.name)))
  def collectionMap(collection: String, json: JValue) = {
    val rf = extractRepFactor(collection, json)
    extractShards(collection, json).flatMap(getReplicas).map(x => Record(x._1, x._2, x._3, collection, rf))
  }
}