package composer

import upickle.default.read
import upickle.default.ReadWriter
import upickle.default.ReadWriter.join
import upickle.implicits.key

// Types for decoding composer.json files
case class Package(packageName: String, packageVersion: String) derives ReadWriter

case class ComposerFile(
    name: String,
    @key("require") packages: Map[String, String],
    @key("require-dev") dev: Option[Map[String, String]] = None
) derives ReadWriter

object ComposerFile:
  def fromJson(jsonContent: String): ComposerFile = read[ComposerFile](jsonContent)

extension (item: ComposerFile)
  def combinedList: List[Package]           = item.getPackageList ++ item.getDevList
  private def getPackageList: List[Package] = item.packages.map((pkg, version) => Package(pkg, version)).toList
  private def getDevList: List[Package]     =
    item.dev
      .getOrElse(Map.empty)
      .map((pkg, version) => Package(pkg, version))
      .toList

// Types for building reports
type ProjectName    = String
type PackageName    = String
type PackageVersion = String
type PackageList    = List[Package]

case class UsedVersion(projectName: ProjectName, packageName: PackageName, packageVersion: PackageVersion)
case class PackageReport(packageName: PackageName, occurrences: List[UsedVersion])

// Service for building reports
object ComposerReportRepository:
  def getReport(files: List[ComposerFile]): List[PackageReport] =
    val allPackages = files.flatMap { file =>
      file.combinedList.map(pkg =>
        UsedVersion(
          file.name,
          pkg.packageName,
          pkg.packageVersion
        )
      )
    }

    // Group packages by name
    allPackages
      .groupBy(_.packageName)
      .map { case (packageName, versions) =>
        // Group by specific version
        val groupedByVersion: Map[PackageVersion, List[UsedVersion]] =
          versions.groupBy(_.packageVersion)

        // Build the PackageReport with all occurrences for each version
        PackageReport(
          packageName,
          groupedByVersion.flatMap { case (version, usedVersions) =>
            usedVersions.map { uv =>
              uv.copy(packageVersion = version)
            }
          }.toList
        )
      }
      .toList

  def createReportString(reports: List[PackageReport]): String =
    val sb = new StringBuilder
    reports.foreach { report =>
      sb.append(s"${report.packageName}: ${report.occurrences.size}\n")
      val groupedByVersion = report.occurrences.groupBy(_.packageVersion)

      groupedByVersion.foreach { case (version, occurrences) =>
        sb.append(s"  ${report.packageName}: $version -> ${occurrences.size}\n")
        occurrences.foreach { occurrence =>
          sb.append(s"    ${occurrence.projectName}\n")
        }
      }
    }
    sb.toString()
