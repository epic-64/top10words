import scala.concurrent.{Future, ExecutionContext, Await}
import scala.io.Source
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

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

    // Function to process a list of websites and return the top 10 words for each
    def countWords(urls: List[String]): Future[List[(String, List[(String, Int)])]] = {
        // Download and process each website asynchronously
        val futures = urls.map { url =>
            fetchContent(url).flatMap(content => top10Words(content).map(url -> _))
        }

        // Combine all results
        Future.sequence(futures)
    }

    // Function to format results into a printable string
    def formatResults(results: List[(String, List[(String, Int)])]): String = {
        results.map { case (url, wordCounts) =>
            val formattedCounts = wordCounts.map { case (word, count) =>
                f"$word%-15s : $count"
            }.mkString("\n")
            s"\n$url:\n$formattedCounts"
        }.mkString("\n")
    }

    // Function to process and handle results, and return a printable string or an error message
    def processResults(urls: List[String]): String = {
        val wordCountFuture = countWords(urls)

        // Await the results and handle potential exceptions
        try
            val results = Await.result(wordCountFuture, 30.seconds) // Wait for up to 30 seconds
            formatResults(results)
        catch
            case e: Exception =>
                s"An error occurred while processing the results: ${e.getMessage}"
    }
