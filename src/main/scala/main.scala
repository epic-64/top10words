import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

@main def main(): Unit = {
  // List of websites to crawl
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

  // Call WebWordCounter to get the top words for each website
  val wordCountFuture = WebWordCounter.countWords(urls)

  // Await the results and print them
  try
    val results = Await.result(wordCountFuture, 30.seconds) // Wait for up to 30 seconds
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