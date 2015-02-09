/**
 * @file: ServerSearch.java
 * @author: mduve
 * @date: 19.11.2014
 */

import java.util.Scanner;

/**
 * Main class of the phone book server application.
 */
public class ServerSearch {
	
	/**
	 * Main routine for the application
	 * @param args 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		Scanner sc = new Scanner(System.in);
	    System.out.print("Which port should the server use?");
	    int port = Integer.parseInt(sc.next());
		
	    Webserver server = new Webserver(port);
		server.start();
	    
		// print the applications greeter
		System.out.println("**********************************************");
		System.out.println("* Welcome									 *");
		System.out.println("* Enter a name or number or both             *");
		System.out.println("**********************************************");		

	}
}
