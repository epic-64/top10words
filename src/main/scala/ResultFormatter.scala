class ResultFormatter:

  // Function to format combined results into a printable string
  def formatCombined(
      top10Results: List[(String, List[(String, Int)])],
      top10Positions: Map[String, Map[String, Int]],
      top10Occurrences: Map[String, Int]
  ): String =
    top10Results
      .map { case (url, wordCounts) =>
        val formattedCounts = wordCounts.zipWithIndex
          .map { case ((word, count), rank) =>
            // Get positions and occurrences of the word in other websites' top 10
            val otherPositions = top10Positions.filterKeys(_ != url).flatMap {
              case (otherUrl, posMap) =>
                posMap
                  .get(word)
                  .map(pos => s"$otherUrl: ($pos|${top10Occurrences(word)})")
            }
            val otherPositionsString =
              if otherPositions.nonEmpty then
                otherPositions.mkString("(", ", ", ")")
              else ""
            f"${rank + 1}. $word%-15s : $count $otherPositionsString"
          }
          .mkString("\n")
        s"\n$url:\n$formattedCounts"
      }
      .mkString("\n")

  def formatCombinedHtml(top10Results: List[(String, List[(String, Int)])], top10Positions: Map[String, Map[String, Int]], top10Occurrences: Map[String, Int]): String = {
    val top10Html = top10Results.map { case (url, wordCounts) =>
      val rows = wordCounts.zipWithIndex.map { case ((word, count), rank) =>
        // Get positions and occurrences of the word in other websites' top 10
        val otherPositions = top10Positions.filterKeys(_ != url).flatMap { case (otherUrl, posMap) =>
          posMap.get(word).map(pos => s"$otherUrl: ($pos|${top10Occurrences(word)})")
        }
        val otherPositionsString = if otherPositions.nonEmpty then otherPositions.mkString("(", ", ", ")") else ""
        s"""
          <tr>
            <td>${rank + 1}</td>
            <td>$word</td>
            <td>$count</td>
            <td>$otherPositionsString</td>
          </tr>
          """
      }.mkString("\n")
      s"""
        <h2>$url</h2>
        <table border="1" cellpadding="5" cellspacing="0">
          <thead>
            <tr>
              <th>Rank</th>
              <th>Word</th>
              <th>Count</th>
              <th>Positions in Other Websites</th>
            </tr>
          </thead>
          <tbody>
            $rows
          </tbody>
        </table>
        """
    }.mkString("\n")

    s"""
      <html>
      <head>
        <title>Top 10 Words Analysis</title>
        <style>
          body {
            font-family: Arial, sans-serif;
            margin: 20px;
          }
          table {
            width: 100%;
            border-collapse: collapse;
          }
          th, td {
            padding: 8px;
            text-align: left;
          }
          th {
            background-color: #f2f2f2;
          }
        </style>
      </head>
      <body>
        <h1>Top 10 Words Analysis for Each Website</h1>
        $top10Html
      </body>
      </html>
      """
  }