package cogito.auditing.endpoints;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

import cogito.auditing.model.AuditEvents;
import cogito.auditing.service.AuditingServices;
import cogito.auditing.service.JSONTransreptor;
import cogito.auditing.service.XMLTransreptor;

/**
 * Handles processing of audit events
 */
public class EventsAccessor extends StandardAccessorImpl {
	
	/**
     * Default constructor
     */
	public EventsAccessor() {	
		this.declareThreadSafe();
	}
	
	@Override
	public void onSource(INKFRequestContext context) throws Exception {
		
		//get the application name
        String application = context.getThisRequest().getArgumentValue
        		("application");

        //retrieve the audit events for given applicaiton
		AuditEvents auditEvents = AuditingServices.retrieveAuditEvents
				(application, "localhost", 8080, "/cogito");
		
        //get the mime type
		String mimeType = context.getThisRequest().getArgumentValue("mime-type");	

        if (mimeType.contains("application/json")) {
        	
        	String body = JSONTransreptor.getInstance().toJSON(auditEvents);
        	
            INKFResponse response = context.createResponseFrom(body);
            response.setExpiry(INKFResponse.EXPIRY_ALWAYS);
            response.setMimeType(mimeType);
        	
        } else {
        	
        	//default to XML
			String body = XMLTransreptor.getInstance().toXML(auditEvents);
        	
            INKFResponse response = context.createResponseFrom(body);
            response.setExpiry(INKFResponse.EXPIRY_ALWAYS);
            response.setMimeType(mimeType);
        }
	}
}