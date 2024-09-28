class ResultFormatter:
    // Function to format results into a printable string
    def format(results: List[(String, List[(String, Int)])]): String = {
        results.map { case (url, wordCounts) =>
            val formattedCounts = wordCounts.map { case (word, count) =>
                f"$word%-15s : $count"
            }.mkString("\n")
            s"\n$url:\n$formattedCounts"
        }.mkString("\n")
    }
