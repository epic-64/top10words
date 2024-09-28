class ResultFormatter:

    // Function to format combined results into a printable string
    def formatCombined(top10Results: List[(String, List[(String, Int)])], top10Positions: Map[String, Map[String, Int]], top10Occurrences: Map[String, Int]): String = {
        top10Results.map { case (url, wordCounts) =>
            val formattedCounts = wordCounts.zipWithIndex.map { case ((word, count), rank) =>
                // Get positions and occurrences of the word in other websites' top 10
                val otherPositions = top10Positions.filterKeys(_ != url).flatMap { case (otherUrl, posMap) =>
                    posMap.get(word).map(pos => s"$otherUrl: ($pos|${top10Occurrences(word)})")
                }
                val otherPositionsString = if otherPositions.nonEmpty then otherPositions.mkString("(", ", ", ")") else ""
                f"${rank + 1}. $word%-15s : $count $otherPositionsString"
            }.mkString("\n")
            s"\n$url:\n$formattedCounts"
        }.mkString("\n")
    }
