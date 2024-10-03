package composer

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}

class FileReader:
    def get(filePath: String): String =
        String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8)