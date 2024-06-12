package org.example

import slick.jdbc.PostgresProfile.api._
import com.typesafe.config.ConfigFactory
import scalaj.http._
import scala.concurrent.Await
import scala.concurrent.duration._
import org.slf4j.LoggerFactory
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.Json
import io.circe._

case class LogEntry(id: Int, url: String, summary: String)

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

//  def getHistory(): Seq[(String, String, String)] = {
//    // Retrieve history from the database
//    val query = requestLogs.map(log => (log.id.toString(), log.url, log.summary)).result
//    Await.result(db.run(query), 30.seconds)
//  }

//  def getHistory(): String = {
//    // Retrieve history from the database
//    val query = requestLogs.map(log => (log.id.toString(), log.url, log.summary)).result
//    val result = Await.result(db.run(query), 30.seconds)
//
//    // Convert the sequence of tuples to a sequence of LogEntry objects
//    val logEntries = result.map { case (id, url, summary) => LogEntry(id, url, summary) }
//
//    // Convert the sequence of LogEntry objects to JSON
//    val json = Json.toJson(logEntries)
//
//    // Return the JSON as a string
//    json.toString()
//  }
  def getHistory(): Seq[String] = {
    // Retrieve history from the database
    val query = requestLogs.map(log => (log.id, log.url, log.summary)).result
    val result = Await.result(db.run(query), 30.seconds)

    // Convert the sequence of tuples to a sequence of LogEntry objects
    val logEntries = result.map { case (id, url, summary) => LogEntry(id, url, summary) }

    // Convert the result to JSON
    logEntries.map(_.asJson.noSpaces)
  }

  def updateSummary(id: Int, newSummary: String): Int = {
    val query = requestLogs.filter(_.id === id)
      .map(_.summary)
      .update(newSummary)
    db.run(query)
    val done: Int = 1
    done
  }
}