/**
 * @file: RMIClient.java
 * @author: mduve
 * @date: 12.01.2015
 */

import java.io.*;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client extends Thread implements Remote {
    
	private static String rmiIp;
    private ServerInterface rmiServer;
    
    private Socket connectedClient = null;
    private ServerSocket server = null;
    private BufferedReader inFromClient = null;
    private DataOutputStream outToClient = null;
    
	private int status;

    public Client(Socket client,ServerSocket server, String ip) {
        connectedClient = client;
        this.server = server;
		
        try {
            Registry registry = LocateRegistry.getRegistry(ip, Registry.REGISTRY_PORT);
            rmiServer = (ServerInterface) registry.lookup("RMIServer");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * runnable
     */
    public void run() {

        try {
            System.out.println("The Client is connected on port " + connectedClient.getPort() + " is connected");
            
            // Schafft einen  BufferReader zum lesen des Client Inputs
            inFromClient = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
            // Schafft einen  DataOutputStream zum schreiben zum Client
            outToClient = new DataOutputStream(connectedClient.getOutputStream());

            // Liest die gesendeten Daten des Clients ein
            ResponseHandler responseHandler = Parser.parse(inFromClient.readLine().trim());
            
            if (responseHandler != null && responseHandler.isValid()) {
        		
    			// Serves the shutdown request
    			if (responseHandler.getPath().equals("/server/shutdown")) {
    				String html = readResource("end.html");
    				write(200, html);
    				System.out.println("Received shutdown signal.");
    				try {
    					server.close();
                        rmiServer.quit();
    				} catch (IOException e) {
    					e.printStackTrace();
    				} finally {
    					System.out.println("Server has been shut down successfully");
    					System.exit(0);
    				}
    			}
    			
    			// Request for the form page (index.html)
    			if (responseHandler.getPath().equals("/") && responseHandler.getBasefile().equals("index.html")) {
    				String html = readResource(responseHandler.getBasefile());
    				write(200, html);
    				return;
    			}
    			
    			// Request for the actual search (search.html)
    			if (responseHandler.getPath().equals("/") && responseHandler.getBasefile().equals("search.html")) {
    				List<PhoneDirectoryEntry> result = new LinkedList<PhoneDirectoryEntry>();
    				
    				String html = readResource(responseHandler.getBasefile());
    				String name = responseHandler.getParameters().get("name");
    				String number = responseHandler.getParameters().get("number");
    				
    				System.err.println(name);
    				System.err.println(number);
    				
    				String input = "";
    				
    				if (number != null) {
    					number = number.trim();
    					input += number + " ";
    				}
    				if (name != null) {
    					name = name.trim();
    					input += name;
    				}
    				    				
    				if (input.matches("\\s*") || input.length() == 0) {
    					html = html.replaceAll("\\{searchResult\\}", "No search has been made, since request was empty.");
    					write(200, html);
    					return;
    				}

    				// If no data has been provided, return empty search
    				if (name == null && number == null) {
    					html = html.replaceAll("\\{searchResult\\}", "No search has been made, since request was empty.");
    					write(200, html);
    					return;
    				}
    				    				
                    ArrayList<String> results = rmiServer.search(name, number);
    				                    
    				// replace the output in the template
    				if (results.size() > 0) {
    					StringBuffer b = new StringBuffer();
    					b.append("<ul>");
    					for (String e : results) 
    						b.append("<li>"+ e +"</li>");
    					b.append("</ul>");
    					html = html.replaceAll("\\{searchResult\\}", b.toString());
    					
    				} else {
    					html = html.replaceAll("\\{searchResult\\}", "");
    				}
    				
    				write(200, html);
    				return;
    			}
    			
    			// no matching file could be found. Return 404
    			String html = readResource("404.html");
    			html = html.replaceAll("\\{reqFile\\}", responseHandler.getPath() +"/"+ responseHandler.getBasefile() + responseHandler.getQueryString());
    			write(404, html);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
        /**
    	 * Wraps the given content in a {@link HttpResponse} and writes it to the client.
    	 * @param status The status of the operation
    	 * @param output The content of the operation
    	 */
    	private void write(int status, String output) {

    		try {
    			
    			// create the HttpResponse and write it to the client
    			HttpResponse response = new HttpResponse();
    			response.setStatus(status);
    			response.setContent(output.trim());
    			
    			String message = response.serialize();
    			System.out.println("Writing " + message.length() + " bytes to client.");
    			System.out.println("Header: ");
    			System.out.println(response.getHeader());
    			outToClient.write(message.getBytes());
    			
    		} catch (Exception ex) {
    			System.out.println("Unable to write to client. Cause: " + ex.getMessage());
    		}
    	}
    
	/**
	 * Reads a resource from the classpath directory de.vs.html
	 * @param filename The filename of the resource
	 * @return The raw resource as string
	 */
	private String readResource(String filename) {
		
		try {
			InputStream in = getClass().getResourceAsStream("html/"+filename); 
			StringBuffer b = new StringBuffer();
			byte[] buffer = new byte[1024];
			while (in.read(buffer) > -1) {
				b.append(new String(buffer));
				buffer = new byte[1024];
			}
			return b.toString();
			
		} catch (Exception ex) {
			System.err.println("Unable to read resource : " + filename + ". Cause: " + ex.getMessage());
		}
		return "";
	}

    /**
     * main-Method
     * @param args
     */
    public static void main(String args[])  {
        rmiIp = args[0];
        try {
            //String host = InetAddress.getLocalHost().getHostName();
            int port = 6543;
            ServerSocket server = new ServerSocket(port);
            System.out.println("HTTP Server Waiting for client on port: "+port+" Host: "+rmiIp);
            while (true) {
                Socket connected = server.accept();
                new Client(connected,server,rmiIp).start();
            }
        } catch (Exception e) {
        }

    }

}
