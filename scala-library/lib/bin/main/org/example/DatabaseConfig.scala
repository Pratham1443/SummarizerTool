package org.example

import slick.jdbc.PostgresProfile.api._
import com.typesafe.config.ConfigFactory
import scalaj.http._
import scala.concurrent.Await
import scala.concurrent.duration._
import org.slf4j.LoggerFactory

object DatabaseConfig {
  val db = Database.forConfig("mydb", ConfigFactory.load())
  val logger = LoggerFactory.getLogger(this.getClass)

  // Define the request log table
  class RequestLog(tag: Tag) extends Table[(Int, String, String)](tag, "REQUEST_LOG") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def url = column[String]("URL")
    def summary = column[String]("SUMMARY")
    def * = (id, url, summary)
  }

  val requestLogs = TableQuery[RequestLog]

  def createSchema(): Unit = {
    val schema = requestLogs.schema
    Await.result(db.run(schema.createIfNotExists), 30.seconds)
  }

  def summarizeUrl(url: String): String = {
    // Call the Python FastAPI service to get the summary
    logger.info(s"Sending request to summarize URL: $url")
    val response = Http("http://pythonfastapi-service:8000/summarize")
      .postData(s"""{"url": "$url"}""")
      .header("content-type", "application/json")
      .timeout(connTimeoutMs = 20000, readTimeoutMs = 120000)
      .asString

    if (response.is2xx) {
      logger.info(s"Received successful response: ${response.body}")
      val summary = response.body
      // Log the request and summary to the database
      createSchema()
      val logAction = requestLogs += (0, url, summary)
      Await.result(db.run(logAction), 30.seconds)
      summary
    } else {
      logger.error(s"Failed to summarize URL: ${response.code} ${response.body}")
      throw new RuntimeException("Failed to summarize URL")
    }
  }

  def getHistory(): Seq[(String, String)] = {
    // Retrieve history from the database
    val query = requestLogs.map(log => (log.url, log.summary)).result
    Await.result(db.run(query), 30.seconds)
  }
}