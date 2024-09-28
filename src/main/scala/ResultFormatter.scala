class ResultFormatter:

    // Function to format combined results into a printable string
    def formatCombined(top10Results: List[(String, List[(String, Int)])], top10Positions: Map[String, Map[String, Int]]): String = {
        top10Results.map { case (url, wordCounts) =>
            val formattedCounts = wordCounts.map { case (word, position) =>
                // Get positions of the word in other websites' top 10
                val otherPositions = top10Positions.filterKeys(_ != url).flatMap { case (otherUrl, posMap) =>
                    posMap.get(word).map(pos => s"$otherUrl: $pos")
                }
                val otherPositionsString = if otherPositions.nonEmpty then otherPositions.mkString("(", ", ", ")") else ""
                f"$word%-15s : $position $otherPositionsString"
            }.mkString("\n")
            s"\n$url:\n$formattedCounts"
        }.mkString("\n")
    }
