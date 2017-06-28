package nu.peg.dmarc.xsd;

import java.net.URL;

public class DmarcSchemaXsd {
    public static URL getDmarcSchema() {
        return DmarcSchemaXsd.class.getResource("/schema/dmarc.xsd");
    }
}