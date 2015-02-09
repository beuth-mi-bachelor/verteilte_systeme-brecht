/**
 * @file: HttpRequest.java
 * @package: de.vs.webserver
 * @author: mduve
 * @date: 19.11.2014
 */


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Model class to represent a HTTP Request.
 */
public class ResponseHandler {

	/* Possible HTTP Methods */
	public enum HttpMethod {
		GET,
		POST,
		PUT,
		DELETE
	}
	
	/* Contains the GET Parameters of the request */
	private Map<String, String> parameters;
	
	/* Contains the Header Fields of the Request */
	private Map<String, String> headers;
	
	/* Contains the requested path */
	private String path;
	
	/* Contains the requested Base file */
	private String basefile;
	
	/* Contains the HTTP Version of the request */
	private String version;
	
	/* Contains the HTTP Method of the request */
	private HttpMethod method;
	
	/* contains the validation state of the request */
	private boolean valid = false;


	/**
	 * Default constructor
	 */
	public ResponseHandler() {
		this.parameters = new HashMap<String, String>();
		this.headers    = new HashMap<String, String>();
	}

	
	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
		checkState();
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
		checkState();
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
		checkState();
	}

	/**
	 * @return the basefile
	 */
	public String getBasefile() {
		return basefile;
	}

	/**
	 * @param basefile the basefile to set
	 */
	public void setBasefile(String basefile) {
		this.basefile = basefile;
		checkState();
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
		checkState();
	}
	
	/**
	 * States whether the request was valid or not
	 * @return True if the request seems valid, false otherwise
	 */
	public boolean isValid() {
		return this.valid;
	}


	/**
	 * @param get
	 */
	public void setMethod(HttpMethod method) {
		this.method = method;
		checkState();
	}
	
	/**
	 * Returns the method
	 * @return
	 */
	public HttpMethod getMethod() {
		return this.method;
	}
	
	
	/**
	 * Checks the state of the HttpRequest
	 */
	private void checkState() {
		
		boolean is = true;
		
		// check that the Http Method is set
		is = is && (this.method != null);
		
		// check if the path is valid
		is = is && (this.path != null && !this.path.equals(""));
		
		// check the version
		is = is && (this.version != null && !this.version.equals(""));
		
		
		this.valid = is;
	}
	
	
	/**
	 * Returns all HTTP Parameters as query string
	 * @return All HTTP Parameters as query string
	 */
	public String getQueryString() {
		Set<String> keys = this.parameters.keySet();
		Iterator<String> it = keys.iterator();
		StringBuffer b = new StringBuffer("?");
		while (it.hasNext()) {
			String k = it.next();
			String v = this.parameters.get(k);
			b.append(k + "=" + v + "&");
		}
		return b.toString();
	}

	
	/**
	 * Returns a human readable string representation of the request
	 */
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("[" + this.method  + "]  ");
		if (!this.path.equals("/"))
			b.append(this.path + "/" + this.basefile);
		else
			b.append("/" + this.basefile);
		
		b.append("\nQuery Parameters\n");
		Set<String> keys = this.parameters.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			b.append("\t{" + key + "} => ");
			b.append(this.parameters.get(key));
			b.append("\n");
		}
		b.append("\nHTTP Request Headers\n");
		keys = this.headers.keySet();
		it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			b.append("\t{" + key + "} => ");
			b.append(this.headers.get(key));
			b.append("\n");
		}
		return b.toString();
	}
}
