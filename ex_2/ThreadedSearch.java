/**
 * @file: ThreadedSearch.java
 * @author: mduve
 * @date: 08.10.2014
 * @description: starts a thread to look for all items in a Collection
 */

import java.util.ArrayList;
import java.util.Collection;

public class ThreadedSearch<E extends PhoneDirectoryEntry> extends Thread {
	
	private final Collection<E> sourceToSearchIn;
	private final String itemToSearchFor;
	
	private Collection<E> listOfFoundItems;
	
	/**
	 * @param sourceToSearchIn a collection of Entries where to search in
	 * @param itemToSearchFor look for name or number
	 * @constructor
	 */
	public ThreadedSearch(Collection<E> sourceToSearchIn, String itemToSearchFor) {
		this.sourceToSearchIn = sourceToSearchIn;
		this.itemToSearchFor = itemToSearchFor;
		this.listOfFoundItems = new ArrayList<E>();
	}
	
	@Override
	/**
	 * runnable task of thread
	 */
	public void run() {
		try {
			searchForNameInCollection();
		} catch (final Exception e) {
			System.out.println("Error occured: " + e.getMessage());
		}
	}
	
	/**
	 * walks through collection and adds item
	 * @returns a new Collection of found items
	 */
	private Collection<E> searchForNameInCollection() {
		for (E item : this.sourceToSearchIn) {
			if (item.getName().equals(this.itemToSearchFor) || item.getNumber().equals(this.itemToSearchFor)) {
				System.out.println("    *debug-message* " + item + ", from: " + this.getName());
				listOfFoundItems.add(item);
			}
		}
		return listOfFoundItems;
	}

	@SuppressWarnings("unchecked")
	public PhoneDirectory getResults() {
		return new PhoneDirectory((Collection<PhoneDirectoryEntry>) this.listOfFoundItems);
	}
	
	public String whatWasSearched() {
		return this.itemToSearchFor;
	}

}
