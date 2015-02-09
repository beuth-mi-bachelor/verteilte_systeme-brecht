/**
 * @file: RMIServerInterface.java
 * @author: mduve
 * @date: 12.01.2015
 */

import java.rmi.*;
import java.util.ArrayList;

public interface ServerInterface extends Remote {
	/**
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
    public ArrayList<String> search(String name, String number) throws Exception;
    
    /**
     * 
     * @throws RemoteException
     */
    public void quit() throws RemoteException;
}
