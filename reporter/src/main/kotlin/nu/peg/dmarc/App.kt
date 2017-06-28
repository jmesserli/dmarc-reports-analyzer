package nu.peg.dmarc

import nu.peg.dmarc.parse.DmarcReportUnmarshaller
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val reportsFolder = Paths.get("reports/")

    val matcher = reportsFolder.fileSystem.getPathMatcher("glob:**/*.xml")
    Files.list(reportsFolder)
            .filter(matcher::matches)
            .map(DmarcReportUnmarshaller::unmarshal)
            .peek { if (!it.successful) println("Could not parse file <${it.sourceFile.fileName}>. Error: ${it.error}") }
            .filter { it.successful }
            .forEach {
                println(it)
            }
}