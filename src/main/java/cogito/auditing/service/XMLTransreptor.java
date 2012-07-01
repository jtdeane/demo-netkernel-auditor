package cogito.auditing.service;

import cogito.auditing.model.AuditEvent;
import cogito.auditing.model.AuditEventURL;
import cogito.auditing.model.AuditEvents;

import com.thoughtworks.xstream.XStream;

/**
 * Services for conversion between XML and POJOs using XStream
 * @author jeremydeane
 *
 */
public final class XMLTransreptor {
	
	private final XStream xstream;
	
	private static class XMLTransreptorLoader {
		private static final XMLTransreptor INSTANCE = new XMLTransreptor();
    }
	
	/**
	 * Default constructor
	 */
    private XMLTransreptor() {
    	
    	xstream = new XStream();
    	
    	xstream.alias("event", AuditEventURL.class);
    	xstream.alias("audit-event", AuditEvent.class);
    	xstream.alias("audit-events", AuditEvents.class);
       
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
		return xstream.fromXML(xml);
	}
	
	/**
	 * Convert POJO to XML
	 * @param object
	 * @return String
	 * @throws Exception
	 */
	public String toXML (Object object) throws Exception {
		return xstream.toXML(object);
	}
}