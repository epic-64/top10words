import scala.concurrent.Await
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

  // Call WebWordCounter to get the word counts for each website
  val wordCountFuture = WebWordCounter.countWords(urls)

  // Await the results and handle potential exceptions
  val results = try
    Await.result(wordCountFuture, 30.seconds) // Wait for up to 30 seconds
  catch
    case e: Exception =>
      println(s"An error occurred while processing the results: ${e.getMessage}")
      return

  // Create an instance of ResultFormatter
  val formatter = ResultFormatter()

  // Get top 10 words and their positions across websites
  val top10Results = WebWordCounter.top10Words(results)
  val top10Positions = WebWordCounter.top10Positions(results)

  // Format and print combined results
  val printableCombined = formatter.formatCombined(top10Results, top10Positions)
  println("Top 10 most used words for each website with their positions on other websites:")
  println(printableCombined)
}
