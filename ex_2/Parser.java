/**
 * @file: HttpParser.java
 * @author: mduve
 * @date: 19.11.2014
 */

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Rudimental HTTP Parser.
 * The parser only supports the HTTP Methods GET, POST, PUT, DELETE
 * Further it is able to recognize HTTP Header fields as well as the
 * query parameter string.
 * After parsing the result is returned as {@link ResponseHandler}.
 */
public class Parser {

	/**
	 * Parse the incoming String
	 * @param requestString The string as read from a socket
	 * @return A parsed representation of the incoming string
	 * @throws Exception On any errors
	 */
	public static ResponseHandler parse(String requestString) throws Exception {
		
		String[] lines = requestString.split("\n");
		if (lines.length == 0)
			throw new Exception("The request was empty");
		
		ResponseHandler retval = new ResponseHandler();
		
		
		
		// check if the first line contains a valid HTTP request
		String[] tokens = lines[0].split("\\s");
		if (tokens[0].equalsIgnoreCase("GET"))
			retval.setMethod(ResponseHandler.HttpMethod.GET);
		
		if (tokens[0].equalsIgnoreCase("POST"))
			retval.setMethod(ResponseHandler.HttpMethod.POST);
		
		if (tokens[0].equalsIgnoreCase("DELETE"))
			retval.setMethod(ResponseHandler.HttpMethod.DELETE);
		
		if (tokens[0].equalsIgnoreCase("PUT"))
			retval.setMethod(ResponseHandler.HttpMethod.PUT);
		
		
		// prepare the path, basefile and map for the request parameters and HTTP Version
		String pathPart = tokens[1].split("\\?")[0];
		String path   = pathPart.substring(0, pathPart.lastIndexOf("/")+1);
		String base   = pathPart.substring(pathPart.lastIndexOf("/")+1);
		
		
		// query parameters are optional
		if (tokens[1].split("\\?").length > 1) {
			String query   = tokens[1].split("\\?")[1];
			
			Map<String, String> requestParams = new HashMap<String, String>();
			String[] paramPairs = query.split("&");
			for (String t : paramPairs) {
				String[] keyValue = t.split("=");
				if (keyValue.length == 1) {
					String key = URLDecoder.decode(keyValue[0], "UTF-8");
					requestParams.put(key, null);
				} else if (keyValue.length == 2) {
					String key = URLDecoder.decode(keyValue[0], "UTF-8");
					String val = URLDecoder.decode(keyValue[1], "UTF-8");
					requestParams.put(key, val);
				}
			}
			retval.setParameters(requestParams);
		}
		
		// remove trailing slash from path
		if (path.startsWith("/") && path.endsWith("/") && path.length() > 1)
			path = path.substring(0, path.lastIndexOf("/"));
		
		
		retval.setPath(path);
		retval.setBasefile(base);
	
		if (tokens[2].equalsIgnoreCase("HTTP/1.0") || tokens[2].equalsIgnoreCase("HTTP/1.1"))
			retval.setVersion(tokens[2]);
		
		
		// all subsequent lines are considered as HTTP Request Headers
		Map<String, String> headers = new HashMap<String, String>();
		for (int i=1; i < lines.length; i++) {
			if (lines[i].contains(":")) {
				tokens = lines[i].split(":");
				if (tokens.length > 2) {
					String[] value = Arrays.copyOfRange(tokens, 1, tokens.length-1);
					String val = new String();
					for (String s : value)
						val += ":" + s;
					headers.put(tokens[0], val);
				} else if (tokens.length == 2) {
					headers.put(tokens[0], tokens[1]);
				} else {
					
				}
			}
		}
		retval.setHeaders(headers);
		
		if (!retval.isValid())
			throw new Exception("The request could not be parsed.");
		
		return retval;
	}
}
