package nu.peg.dmarc

import nu.peg.dmarc.xsd.Feedback
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import javax.xml.XMLConstants
import javax.xml.bind.JAXBContext
import javax.xml.validation.SchemaFactory

class App {
    fun main(args: Array<String>) {
        val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val schemaUrl = javaClass.getResource("schema/dmarc.xsd")
        val schema = schemaFactory.newSchema(schemaUrl)

        val ctx = JAXBContext.newInstance(Feedback::class.java)
        val unmarshaller = ctx.createUnmarshaller()
        unmarshaller.schema = schema

        val reportsFolder = Paths.get("reports/")
        val xmls = Files.list(reportsFolder)
                .map { Files.readAllLines(it) }
                .map { it.stream().collect(Collectors.joining("\n")) }
                .collect(Collectors.toList())

        for (xml in xmls) {
        }
    }
}