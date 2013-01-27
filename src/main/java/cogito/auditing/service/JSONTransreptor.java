package cogito.auditing.service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Services for conversion between JSON and POJOs
 * @author jeremydeane
 */
public final class JSONTransreptor {
	
	private final ObjectMapper objectMapper;
	
	private static class JSONTransreptorLoader {
		private static final JSONTransreptor INSTANCE = new JSONTransreptor();
    }
	
	/**
	 * Default constructor
	 */
    private JSONTransreptor() {
    	
    	objectMapper = new ObjectMapper();
       
    	if (JSONTransreptorLoader.INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
    	}
	}
    
    /**
     * Get an JSONTransreptor Singleton
     * @return JSONTransreptor
     */
	public static JSONTransreptor getInstance() {
	    return JSONTransreptorLoader.INSTANCE;
	}
	
	/**
	 * Convert POJO to JSON
	 * @param object
	 * @return String
	 * @throws Exception
	 */
	public String toJSON (Object object) throws Exception {
		return objectMapper.writeValueAsString(object);
	}
	
	/**
	 *  Concert JSON to POJO
	 * @param json
	 * @param valueType
	 * @return Object
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object toPOJO (String json, Class valueType) throws Exception {
		return objectMapper.readValue(json, valueType);
	}

}