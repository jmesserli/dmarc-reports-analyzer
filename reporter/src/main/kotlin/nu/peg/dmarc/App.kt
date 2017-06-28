package nu.peg.dmarc

import nu.peg.dmarc.xsd.DmarcSchemaXsd
import nu.peg.dmarc.xsd.schema.Feedback
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.XMLFilterImpl
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.XMLConstants
import javax.xml.bind.JAXBContext
import javax.xml.parsers.SAXParserFactory
import javax.xml.validation.SchemaFactory

const val NAMESPACE = "http://dmarc.org/dmarc-xml/0.1/rua.xsd"

fun main(args: Array<String>) {
    val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schemaUrl = DmarcSchemaXsd.getDmarcSchema()
    val schema = schemaFactory.newSchema(schemaUrl)

    val context = JAXBContext.newInstance(Feedback::class.java)
    val namespaceFilter = NamespaceFilter(NAMESPACE)
    namespaceFilter.parent = SAXParserFactory.newInstance().newSAXParser().xmlReader

    // Set UnmarshallerHandler as ContentHandler on XMLFilter
    val unmarshaller = context.createUnmarshaller()
    unmarshaller.schema = schema
    namespaceFilter.contentHandler = unmarshaller.unmarshallerHandler

    val reportsFolder = Paths.get("reports/")
    val feedbackList = Files.list(reportsFolder).map {
        namespaceFilter.parse(InputSource(Files.newInputStream(it)))
        unmarshaller.unmarshallerHandler.result as Feedback
    }

    feedbackList.forEach { println(it) }
}

class NamespaceFilter(val namespace: String) : XMLFilterImpl() {
    override fun startElement(uri: String?, localName: String?, qName: String?, atts: Attributes?) {
        super.startElement(namespace, localName, qName, atts)
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        super.endElement(namespace, localName, qName)
    }
}
