package nu.peg.dmarc.parse

import nu.peg.dmarc.xsd.DmarcSchemaXsd
import nu.peg.dmarc.xsd.schema.Feedback
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException
import org.xml.sax.helpers.XMLFilterImpl
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.XMLConstants
import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller
import javax.xml.parsers.SAXParserFactory
import javax.xml.validation.SchemaFactory

object DmarcReportUnmarshaller {
    const val DMARC_NAMESPACE = "http://dmarc.org/dmarc-xml/0.1/rua.xsd"

    private val namespaceFilter: NamespaceFilter
    private val unmarshaller: Unmarshaller

    init {
        val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val schemaUrl = DmarcSchemaXsd.getDmarcSchema()
        val schema = schemaFactory.newSchema(schemaUrl)

        val context = JAXBContext.newInstance(Feedback::class.java)
        namespaceFilter = NamespaceFilter(DMARC_NAMESPACE)
        namespaceFilter.parent = SAXParserFactory.newInstance().newSAXParser().xmlReader

        // Set UnmarshallerHandler as ContentHandler on XMLFilter
        unmarshaller = context.createUnmarshaller()
        unmarshaller.schema = schema
        namespaceFilter.contentHandler = unmarshaller.unmarshallerHandler
    }

    fun unmarshal(reportPath: Path): UnmarshallerResult {
        try {
            namespaceFilter.parse(InputSource(Files.newInputStream(reportPath)))
        } catch(e: SAXParseException) {
            return UnmarshallerResult(reportPath, error = e.message)
        }

        return UnmarshallerResult(reportPath, unmarshaller.unmarshallerHandler.result as Feedback)
    }
}

data class UnmarshallerResult(val sourceFile: Path, val result: Feedback? = null, val error: String? = null) {
    val successful
        get() = result != null && error == null
}

class NamespaceFilter(val namespace: String) : XMLFilterImpl() {
    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        super.startElement(namespace, localName, qName, attributes)
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        super.endElement(namespace, localName, qName)
    }
}
