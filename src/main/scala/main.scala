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

  // Format and print top 10 words for each website
  val top10Results = WebWordCounter.top10Words(results)
  val printableTop10 = formatter.formatTop10(top10Results)
  println("Top 10 most used words for each website:")
  println(printableTop10)

  // Format and print words that are in the top 10 of multiple websites
  val multiTop10Results = WebWordCounter.multiTop10Words(results)
  val printableMultiTop10 = formatter.formatMultiTop10(multiTop10Results)
  println("\nWords that are in the top 10 on multiple websites:")
  println(printableMultiTop10)
}
