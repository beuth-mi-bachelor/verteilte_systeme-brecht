/**
 * @file: HttpServer.java
 * @author: mduve
 * @date: 19.11.2014
 */

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * The servers main entry point.
 * The server starts a socket on the given port and binds it to the
 * first available IP-Address that is not the loopback address.
 */

public class Webserver {
	
	/* The underlying socket */
	private ServerSocket 			_sock;
	
	/* The address the server is bound to */
	private InetSocketAddress 		_address;
	
	/* The port the server is bound to */
	private int						_port;
	
	/* States if the server is running */
	private boolean					_running;
	
	/* Crude hack: Contains a map of the last requests for each host */
	private Map<String, ResponseHandler> _history;
	
	
	/**
	 * Default constructor for the server.
	 * @param port The port to start the server on
	 * @throws Exception On any error
	 */
	public Webserver(int port) throws Exception {
		
		this._port = port;
		this._history = new HashMap<String, ResponseHandler>();
		
		System.out.println("Starting HttpServer on port " + _port);
		System.out.print("Trying to determine public INET Adress of this host...");
		
		
		// Try to find the first public network address that is not the loopback address
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {		
			NetworkInterface e = en.nextElement();
			Enumeration<InetAddress> adresses = e.getInetAddresses();
			for (;adresses.hasMoreElements();) {
				InetAddress e2 = adresses.nextElement();
				if (e2 instanceof Inet4Address) {
					if (!e2.getHostAddress().equals("127.0.0.1")) {
						System.out.println(e2.getHostAddress());
						_address = new InetSocketAddress(e2.getHostAddress(), _port);
					}
				}
			}
		}
		
		// if no address could be obtained use loopback
		if (_address == null) {
			System.out.println("non found. Using: 127.0.0.1");
			_address = new InetSocketAddress(InetAddress.getLocalHost(), _port);
		}
		
		// bind the server socket
		_sock = new ServerSocket();
		_sock.bind(_address);
		System.out.println("Server is bound to : " + _sock.getLocalSocketAddress().toString());
		_running = true;
	}
	
	
	/**
	 * Main procedure for the server
	 */
	public void start() {
		
		if (_sock.isBound()) {
			
			System.out.println("Server is ready for requests");
			while (_running) {
				try {
					Socket k = _sock.accept();
					ServerApplication worker = new ServerApplication(k, _history);
					Thread t = new Thread(worker, "workers");
					t.start();
					System.out.println("Assigned work to thread : " + t.getId());
				} catch (Exception ex) {
					System.err.println("Error during main execution loop. Cause: " + ex.getMessage());
				}
			}
			
			// shutdown the server
			try {
				System.out.print("Shutting down HTTP Server...");
				_sock.close();
			} catch (Exception ex) {
				System.out.println("failed");
				System.err.println("Unable to shutdown the server. Cause:" + ex.getMessage());
			}
			System.out.println("done!");
		}
	}
}

