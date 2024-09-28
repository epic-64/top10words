import scala.concurrent.{Future, ExecutionContext}
import scala.io.Source
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

object WebWordCounter:

    // Function to fetch the content of a website
    def fetchContent(url: String): Future[String] = Future {
        Source.fromURL(url).mkString
    }

    // Function to remove HTML tags using a regular expression
    def removeHtmlTags(content: String): String = {
        content.replaceAll("<[^>]*>", " ")
    }

    // Function to compute all word counts
    def wordCounts(content: String): Future[Map[String, Int]] = Future {
        val wordCounts = mutable.Map[String, Int]()

        // Remove HTML tags and split into words
        val cleanedContent = removeHtmlTags(content)
        val words = cleanedContent.toLowerCase
            .split("\\W+")
            .filter(word => word.nonEmpty && word.length > 2) // Remove short words and empty strings

        words.foreach { word =>
            wordCounts(word) = wordCounts.getOrElse(word, 0) + 1
        }

        wordCounts.toMap
    }

    // Function to process a list of websites and return the word counts for each
    def countWords(urls: List[String]): Future[List[(String, Map[String, Int])]] = {
        // Download and process each website asynchronously
        val futures = urls.map { url =>
            fetchContent(url).flatMap(content => wordCounts(content).map(url -> _))
        }

        // Combine all results
        Future.sequence(futures)
    }

    // Function to get top 10 words for each website from the full word counts
    def top10Words(results: List[(String, Map[String, Int])]): List[(String, List[(String, Int)])] = {
        results.map { case (url, counts) =>
            val top10 = counts.toList.sortBy(-_._2).take(10)
            url -> top10
        }
    }

    // Function to get the position of each word in the top 10 for all websites
    def top10Positions(results: List[(String, Map[String, Int])]): Map[String, Map[String, Int]] = {
        results.map { case (url, counts) =>
            val top10WithPositions = counts.toList.sortBy(-_._2).take(10).zipWithIndex.map {
                case ((word, _), index) => word -> (index + 1)
            }.toMap
            url -> top10WithPositions
        }.toMap
    }
