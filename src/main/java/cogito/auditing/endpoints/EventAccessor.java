package cogito.auditing.endpoints;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

import cogito.auditing.infrastructure.AccessorUtility;
import cogito.auditing.model.AuditEvent;
import cogito.auditing.service.AuditingServices;
import cogito.auditing.service.JSONTransreptor;
import cogito.auditing.service.XMLTransreptor;

/**
 * Handles processing of audit events
 */
public class EventAccessor extends StandardAccessorImpl {
	
	/**
     * Default constructor
     */
	public EventAccessor() {	
		this.declareThreadSafe();
	}
	
	@Override
	public void onSource(INKFRequestContext context) throws Exception {

		//get the audit event key
        String auditEventKey = context.getThisRequest().getArgumentValue("eventKey");
        
        //retrieve the audit event
        AuditEvent auditEvent = AuditingServices.retrieveAuditEvent(auditEventKey);       
		
        //perform content negotiation
        if (auditEvent != null) {
        	
            //get the mime type 
    		String mimeType = context.getThisRequest().getArgumentValue("mime-type");	
            
            if (mimeType.contains("application/json")) {
            	
            	String body = JSONTransreptor.getInstance().toJSON(auditEvent);
            	
                INKFResponse response = context.createResponseFrom(body);
                response.setExpiry(INKFResponse.EXPIRY_ALWAYS);
                response.setMimeType(mimeType);
            	
            } else {
            	
            	//default to XML
    			String body = XMLTransreptor.getInstance().toXML(auditEvent);
            	
                INKFResponse response = context.createResponseFrom(body);
                response.setExpiry(INKFResponse.EXPIRY_ALWAYS);
                response.setMimeType("application/xml");
            }
        	
        } else {
        	
        	//handle error
        	AccessorUtility.handleError(context, "Unable to find audit event " 
        			+ auditEventKey);
        	
        }

	}
	
	@Override
	public void onNew(INKFRequestContext context) throws Exception {
		
		String body = (String)context.getThisRequest().getPrimary();
		
		String mimeType = context.getThisRequest().getArgumentValue("mime-type");		
		
		//transform to Java Bean
		AuditEvent auditEvent = convert(body, mimeType);
		
		//get the audit event key
        String auditEventKey = context.getThisRequest().getArgumentValue("eventKey");
		
        //store the event
		AuditingServices.storeAuditEvent(auditEvent, auditEventKey);
		
        INKFResponse response = context.createResponseFrom(body);       
        response.setExpiry(INKFResponse.EXPIRY_ALWAYS);
        response.setMimeType(mimeType);
	}
	
	@Override
	public void onSink(INKFRequestContext context) throws Exception {
		
		String body = (String)context.getThisRequest().getPrimary();
		
		String mimeType = context.getThisRequest().getArgumentValue("mime-type");		
		
		//transform to Java Bean
		AuditEvent auditEvent = convert(body, mimeType);
		
		//store the audit event
		AuditingServices.storeAuditEvent(auditEvent);
		
        INKFResponse response = context.createResponseFrom(body);
        response.setHeader("httpResponse:/header/Location", AuditingServices.
        		createResponseURL(auditEvent.getAuditEventKey()));
        response.setExpiry(INKFResponse.EXPIRY_ALWAYS);
        response.setMimeType(mimeType);
	}	

	@Override
	public void onDelete(INKFRequestContext context) throws Exception {
		
		//get the audit event key
        String auditEventKey = context.getThisRequest().getArgumentValue("eventKey");
        
        //delete audit event
        AuditingServices.deleteAuditEvent(auditEventKey);
        
        AccessorUtility.returnMessage(context, "Audit Event " + 
        		auditEventKey + " Deleted");
	}
	
	/**
	 * Converts Representation to POJO based on mime-type
	 * @param entity
	 * @return AuditEvent
	 * @throws Exception
	 */
	private AuditEvent convert(String body, String mimeType) 
			throws Exception {
	
		//method variables
		AuditEvent auditEvent = null;
		
		
        if (mimeType.equals("application/json")) {
        	
        	auditEvent = (AuditEvent) JSONTransreptor.getInstance().toPOJO
        			(body, AuditEvent.class);

        } else {
        	
        	auditEvent = (AuditEvent) XMLTransreptor.getInstance().toPOJO(body);
		}
        
        return auditEvent;
	}
}