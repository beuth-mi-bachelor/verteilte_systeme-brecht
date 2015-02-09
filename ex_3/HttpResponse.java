/**
 * @file: HttpResponse.java
 * @package: de.vs.webserver
 * @author: mduve
 * @date: 19.11.2014
 */


import java.util.Date;

/**
 * Model class to be served as HTTP Response
 */
public class HttpResponse {
	
	/* The status code for the response */
	private int			 status;
	
	/* The current date when the response was generated */
	private Date		 date;
	
	/* The contents of the response */
	private String		 content;
	
	/* Delimeter constant to mark the end of a line */
	private final String EOL;
	
	
	/**
	 * Default constructor
	 */
	public HttpResponse() {
		this.date = new Date();
		
		byte[] b = new byte[2];
		b[0] = 13;
		b[1] = 10;
		EOL = new String(b);
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getContentLength() {
		return this.content.length();
	}
	
	
	/**
	 * Creates a string representation of the integer status code
	 * @return The status as string representation
	 */
	private String mapStatus() {
		
		if (status == 200)
			return "200 OK";
		
		if (status == 201)
			return "201 Created";
		
		if (status == 404)
			return "404 Not Found";
		
		if (status == 500)
			return "500 Internal Server Error";
		
		return "";
	}
	
	
	/**
	 * Serializes the HTTP Response in such a way that it is readable by clients
	 * @return String representation of the response
	 */
	public String serialize() {
		StringBuffer b = new StringBuffer(getHeader());
		b.append(this.content + EOL);
		b.append(EOL);
		return b.toString();
	}
	
	
	/**
	 * Returns a string representation of the headers
	 * @return The headers as string representation
	 */
	public String getHeader() {
		
		StringBuffer b = new StringBuffer();
		
		b.append("HTTP/1.1 "+mapStatus()+" " + EOL);
		b.append("Date:" + date.toString() + EOL);
		b.append("Content-length:" + this.content.length() + EOL);
		b.append("Connection:close" + EOL);
		b.append("Content-Type:text/html;charset=utf-8" + EOL);
		b.append(EOL);
		return b.toString();
	}
}
