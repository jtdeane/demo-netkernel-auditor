package cogito.auditing.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;

/**
 * Services for conversion between XML and POJOs using XStream
 * @author jeremydeane
 *
 */
public final class XMLTransreptor {
	
	private final JAXBContext jaxbContext;
	
	private static class XMLTransreptorLoader {
		private static final XMLTransreptor INSTANCE = new XMLTransreptor();
    }
	
	/**
	 * Default constructor
	 */
    private XMLTransreptor() {
    	
    	try {
			
    		jaxbContext = JAXBContext.newInstance (new Class[] 
    				{cogito.auditing.model.AuditEvent.class, 
    				cogito.auditing.model.AuditEvents.class});
    		
    		
		} catch (Exception e) {
			
			throw new IllegalStateException("Unable to initialize JAXB");
		}
       
    	if (XMLTransreptorLoader.INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
    	}
	}
    
    /**
     * Get an XMLTranssreptor Singleton
     * @return XMLTransreptor
     */
	public static XMLTransreptor getInstance() {
	    return XMLTransreptorLoader.INSTANCE;
	}
	
	/**
	 * Convert XML to POJO
	 * @param xml
	 * @return Object
	 * @throws Exception
	 */
	public Object toPOJO (String xml) throws Exception {
		
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
		InputStream stream = IOUtils.toInputStream (xml);
		
		return unmarshaller.unmarshal(stream);
	}
	
	/**
	 * Convert POJO to XML
	 * @param object
	 * @return String
	 * @throws Exception
	 */
	public String toXML (Object object) throws Exception {
		
		Marshaller marsheller = jaxbContext.createMarshaller();
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		
		marsheller.marshal(object, outStream);
		
		return outStream.toString();
	}
}