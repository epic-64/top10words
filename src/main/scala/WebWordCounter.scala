import scala.concurrent.{Future, Await, ExecutionContext}
import scala.io.Source
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.collection.mutable

object WebWordCounter:
  // Function to fetch the content of a website
  def fetchContent(url: String): Future[String] = Future {
    Source.fromURL(url).mkString
  }

  // Function to remove HTML tags using a regular expression
  def removeHtmlTags(content: String): String = {
    content.replaceAll("<[^>]*>", " ")
  }

  // Function to compute the top 10 most used words
  def top10Words(content: String): Future[List[(String, Int)]] = Future {
    val wordCounts = mutable.Map[String, Int]()

    // Remove HTML tags and split into words
    val cleanedContent = removeHtmlTags(content)
    val words = cleanedContent.toLowerCase
        .split("\\W+")
        .filter(word => word.nonEmpty && word.length > 2) // Remove short words and empty strings

    words.foreach { word =>
      wordCounts(word) = wordCounts.getOrElse(word, 0) + 1
    }

    wordCounts.toList.sortBy(-_._2).take(10)
  }

  // Main function to combine all results
  def main(args: Array[String]): Unit = {
    val urls = List(
      "https://example.com",
      "https://www.scala-lang.org",
      "https://www.wikipedia.org",
      "https://www.github.com",
      "https://www.stackoverflow.com",
      "https://www.reddit.com",
      "https://www.bbc.com",
      "https://www.cnn.com",
      "https://www.nytimes.com",
      "https://news.ycombinator.com"
    )

    // Download and process each website asynchronously
    val futures = urls.map { url =>
      fetchContent(url).flatMap(content => top10Words(content).map(url -> _))
    }

    // Combine all results and print them
    val combinedResults = Future.sequence(futures)

    // Await the results and handle them
    try
      val results = Await.result(combinedResults, 30.seconds) // Wait for up to 30 seconds
      println("Top 10 most used words for each website:")
      results.foreach { case (url, wordCounts) =>
        println(s"\n$url:")
        wordCounts.foreach { case (word, count) =>
          println(f"$word%-15s : $count")
        }
      }
    catch
      case e: Exception =>
        println(s"An error occurred: ${e.getMessage}")
  }
