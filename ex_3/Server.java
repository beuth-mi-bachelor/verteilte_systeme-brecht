/**
 * @file: RMIServer.java
 * @author: mduve
 * @date: 12.01.2015
 */

import java.rmi.Naming;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.rmi.RemoteException;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private static final long serialVersionUID = 1L;
    public  Server() throws  RemoteException{}

    public ArrayList<String> search(String name, String number) throws Exception {
        System.err.println("RMI suche läuft...");
        PhoneDirectory phonebook = new PhoneDirectory("book.xml");
		
		ArrayList<PhoneDirectoryEntry> res = phonebook.search(name, number);
		ArrayList<String> results = new ArrayList<String>();
		
		for(PhoneDirectoryEntry item : res) {
			results.add(item.toString());
		}
		
        return results;
    }

    public static void main (String[] argv) {

        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            System.out.println("RMI: Registry wurde auf Port " + Registry.REGISTRY_PORT + " erzeugt.");

            Naming.rebind("RMIServer",new Server());
            System.out.println("RMI: Abteilungsserver registriert");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void quit() throws RemoteException {
        System.out.println("RMI Server herrunter gefahren!");
        Registry registry = LocateRegistry.getRegistry();
        try {
            registry.unbind("RMIServer");
        } catch (Exception e) {
            throw new RemoteException("Could not unregister service, quiting anyway", e);
        }
        new Thread() {
            @Override
            public void run() {

                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    // I don't care
                }
                System.out.println("done");
                System.exit(0);
            }

        }.start();

    }
}