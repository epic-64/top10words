import scala.concurrent.Await
import scala.concurrent.duration._
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

@main def main(): Unit = {
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

  // Get top 10 words, their positions across websites, and their occurrences
  val top10Results = WebWordCounter.top10Words(results)
  val top10Positions = WebWordCounter.top10Positions(results)
  val top10Occurrences = WebWordCounter.top10Occurrences(results)

  // Format and print combined results
  val printableCombined = formatter.formatCombined(top10Results, top10Positions, top10Occurrences)
  println("Top 10 most used words for each website with their positions on other websites:")
  println(printableCombined)

  // Format and save results as HTML
  val htmlCombined = formatter.formatCombinedHtml(top10Results, top10Positions, top10Occurrences)
  Files.write(Paths.get("word_analysis.html"), htmlCombined.getBytes(StandardCharsets.UTF_8))
  println("\nResults have been saved as 'word_analysis.html'.")
}
