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

  // Call WebWordCounter to process and print the results
  val printableResult = WebWordCounter.processResults(urls)

  // Print the final result
  println("Top 10 most used words for each website:")
  println(printableResult)
}
