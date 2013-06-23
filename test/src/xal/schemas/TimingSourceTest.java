package xal.schemas;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;

import org.w3c.dom.Document;

/**
 * Unit test case for <code>xdxf.xsd</code> XML schema using <code>timing_pvs.tim</code> structure.
 * @author <a href='jakob.battelino@cosylab.com'>Jakob Battelino Prelog</a>
 */
public class TimingSourceTest extends AbstractXMLValidationTest {
	
	@Override
	public void progressiveSchemaValidation() {
		//TODO
	}
	
	@Override
	protected Document getTestDocument() throws Exception {
		return readDocument(DIR_TEST_XMLS+"timingsource_test.xml");
	}
	
	@Override
	protected Schema getSchema() throws Exception {
		return readSchema(DIR_SCHEMAS+"xdxf.xsd", XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}
	
	@Override
	protected Document getExternalDocument() throws Exception {
		return readDocument(DIR_TEST_XMLS+"timingsource_test.xml");
	}
}
