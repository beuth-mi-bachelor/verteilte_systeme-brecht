/**
 * @file: Book.java
 * @author: mduve
 * @date: 08.10.2014
 * @description: represents a list of entries in a phone directory
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PhoneDirectory {
	
	private Collection<PhoneDirectoryEntry> phonelist;
	
	/**
	 * creates a new instance of an empty phone book
	 * @constructor
	 */
	public PhoneDirectory() {
		this.phonelist = new ArrayList<PhoneDirectoryEntry>();
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
	public void searchForName(String name) {
		ThreadedSearch<PhoneDirectoryEntry> searcher = new ThreadedSearch<PhoneDirectoryEntry>(this.phonelist, name);
		try {
			searcher.start();
			searcher.join();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		ThreadedSearch[] results = {searcher};
		PhoneDirectory.printResults(results);
	}
	
	/**
	 * look for a number
	 */
	public void searchForNumber(String number) {
		ThreadedSearch<PhoneDirectoryEntry> searcher = new ThreadedSearch<PhoneDirectoryEntry>(this.phonelist, number);
		try {
			searcher.start();
			searcher.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ThreadedSearch[] results = {searcher};
		PhoneDirectory.printResults(results);
	}
	
	/**
	 * look for both in different threads
	 */
	public void searchForBoth(String name, String number) {
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