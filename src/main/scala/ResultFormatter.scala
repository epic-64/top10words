class ResultFormatter:
    // Function to format top 10 results into a printable string
    def formatTop10(results: List[(String, List[(String, Int)])]): String = {
        results.map { case (url, wordCounts) =>
            val formattedCounts = wordCounts.map { case (word, count) =>
                f"$word%-15s : $count"
            }.mkString("\n")
            s"\n$url:\n$formattedCounts"
        }.mkString("\n")
    }

    // Function to format multiTop10 results into a printable string
    def formatMultiTop10(results: List[(String, Set[String])]): String = {
        results.map { case (url, words) =>
            val formattedWords = words.mkString(", ")
            s"\n$url:\n$formattedWords"
        }.mkString("\n")
    }
