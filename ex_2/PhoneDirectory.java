/**
 * @file: Book.java
 * @author: mduve
 * @date: 08.10.2014
 * @description: represents a list of entries in a phone directory
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class PhoneDirectory {
	
	private Collection<PhoneDirectoryEntry> phonelist;
	
	/**
	 * creates a new instance of an empty phone book
	 * @constructor
	 */
	public PhoneDirectory() {
		this.phonelist = new ArrayList<PhoneDirectoryEntry>();
		PhoneDirectoryEntry[] testEntries = {
				new PhoneDirectoryEntry("Müller", "79123"),
				new PhoneDirectoryEntry("Meier", "79297"),
				new PhoneDirectoryEntry("Peter", "79267"),
				new PhoneDirectoryEntry("Kalle", "45664"),
				new PhoneDirectoryEntry("Erna", "45669"),
				new PhoneDirectoryEntry("ÄÖÜäöß", "45667"),
				new PhoneDirectoryEntry("von Testleertaste", "79092"),
				new PhoneDirectoryEntry("Müller", "79447"),
				new PhoneDirectoryEntry("Müller", "79123"),
				new PhoneDirectoryEntry("Meier", "46789"),
				new PhoneDirectoryEntry("Vater", "79267"),
				new PhoneDirectoryEntry("Muter", "45664"),
				new PhoneDirectoryEntry("Testname", "45669"),
				new PhoneDirectoryEntry("KeineUmlaute", "45667"),
				new PhoneDirectoryEntry("von Testleertaste", "79092"),
				new PhoneDirectoryEntry("Müller", "79123")
		};
		
		// add entries to phonebook
		this.addAllEntries(testEntries);
	}
	
	/**
	 * creates a new instance of a phone book from the Collection
	 * @param list a Collection containing PhoneDirectoryEntries
	 * @constructor
	 */
	public PhoneDirectory(Collection<PhoneDirectoryEntry> list) {
		this.phonelist = list;
	}
	
	/**
	 * creates a new instance of a phone book from the Collection
	 * @param list a Collection containing PhoneDirectoryEntries
	 * @constructor
	 */
	public PhoneDirectory(String pathToXML) {
		try {
			this.phonelist = this.createPhoneDirectoryFromXML(pathToXML);
		} catch (Exception e) {
			this.phonelist = new ArrayList<PhoneDirectoryEntry>();
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * creates a new instance of a phone book from the Array
	 * @param list a Array containing PhoneDirectoryEntries
	 * @constructor
	 */
	public PhoneDirectory(PhoneDirectoryEntry[] list) {
		this.phonelist = new ArrayList<PhoneDirectoryEntry>(Arrays.asList(list));
	}

	public Collection<PhoneDirectoryEntry> getPhonelist() {
		return phonelist;
	}

	private Collection<PhoneDirectoryEntry> createPhoneDirectoryFromXML(String path) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse( new File(path) );
	    
	    Collection<PhoneDirectoryEntry> newBook = new ArrayList<PhoneDirectoryEntry>();
	    
	    document.getDocumentElement().normalize();
	    NodeList nList = document.getElementsByTagName("entry");
	    
	    for(int i = 0; i < nList.getLength(); i++) {
		    
	    	Node currentNode = nList.item(i);
	    	
	    	if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	    		
	    		Element currentElem = (Element) currentNode;
	    		
	    		String name = currentElem.getElementsByTagName("name").item(0).getTextContent();
	    		String number = currentElem.getElementsByTagName("number").item(0).getTextContent();
	    			    		
	    		PhoneDirectoryEntry newEntry = new PhoneDirectoryEntry(name, number);
			    newBook.add(newEntry);
	    	}	    
		    
	    }
	    
	    return newBook;
	}
	
	public void setPhonelist(Collection<PhoneDirectoryEntry> phonelist) {
		this.phonelist = phonelist;
	}

	public void addEntry(PhoneDirectoryEntry newEntry) {
		this.phonelist.add(newEntry);
	}
	
	public void addAllEntries(PhoneDirectoryEntry[] newEntries) {
		this.phonelist.addAll(Arrays.asList(newEntries));
	}
	
	public void addAllEntries(Collection<PhoneDirectoryEntry> newEntries) {
		this.phonelist.addAll(newEntries);
	}
	
	/**
	 * look for a name
	 */
	public ThreadedSearch[] searchForName(String name) {
		ThreadedSearch<PhoneDirectoryEntry> searcher = new ThreadedSearch<PhoneDirectoryEntry>(this.phonelist, name);
		try {
			searcher.start();
			searcher.join();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		ThreadedSearch[] results = {searcher};
		PhoneDirectory.printResults(results);
		return results;
	}
	
	/**
	 * look for a number
	 */
	public ThreadedSearch[] searchForNumber(String number) {
		ThreadedSearch<PhoneDirectoryEntry> searcher = new ThreadedSearch<PhoneDirectoryEntry>(this.phonelist, number);
		try {
			searcher.start();
			searcher.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ThreadedSearch[] results = {searcher};
		PhoneDirectory.printResults(results);
		return results;
	}
	
	/**
	 * look for both in different threads
	 */
	public ThreadedSearch[] searchForBoth(String name, String number) {
		ThreadedSearch<PhoneDirectoryEntry> searcherNumber = new ThreadedSearch<PhoneDirectoryEntry>(this.phonelist, number);
		ThreadedSearch<PhoneDirectoryEntry> searcherName = new ThreadedSearch<PhoneDirectoryEntry>(this.phonelist, name);

		try {
			searcherNumber.start();
			searcherName.start();
			searcherNumber.join();
			searcherName.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ThreadedSearch[] results = {searcherName, searcherNumber};
		PhoneDirectory.printResults(results);
		return results;
	}
	
	/**
	 * a better way to print out the results
	 */
	public static void printResults(ThreadedSearch<PhoneDirectoryEntry>[] searcher) {
		for (ThreadedSearch<PhoneDirectoryEntry> thread : searcher) {
			PhoneDirectory result = thread.getResults();
			String name = thread.getName();
			String searchedFor = thread.whatWasSearched();
			if (result.phonelist.size() == 0) {
				System.out.println("No results for your search '" + searchedFor + "' in Thread '" + name + "' were found");
			} else {
				System.out.println(result + ", from " + name + " searched for: '" + searchedFor + "'" );
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((phonelist == null) ? 0 : phonelist.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhoneDirectory other = (PhoneDirectory) obj;
		if (phonelist == null) {
			if (other.phonelist != null)
				return false;
		} else if (!phonelist.equals(other.phonelist))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = "{\n";
		for (PhoneDirectoryEntry entry : phonelist) {
			str += "    " + entry + "\n";
		}
		str += "}";
		return str;
	}
	
	
	
}