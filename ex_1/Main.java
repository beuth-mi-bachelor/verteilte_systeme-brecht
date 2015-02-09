/**
 * @file: Main.java
 * @author: mduve
 * @date: 08.10.2014
 * @description: main app for exercise 1 - phone directory search
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		PhoneDirectory book = new PhoneDirectory();
		
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
		book.addAllEntries(testEntries);
		
		// is scanner active or quit
		Boolean isActive = true;
		
		// create a new Scanner for scanning input
		Scanner scanInput = new Scanner(System.in);
		
		// as long as scanner listens do
		while (isActive) {
			System.out.print("Enter number, name or both, type 'quit' for leaving application: ");
			
			// scan next line
			String input = scanInput.nextLine();
			// if user types 'quit' only, end program
			if (input.equals("quit")) {
				isActive = false;
				System.out.println("  - you have quit the program");
				continue;
			} else {
				// remove empty spaces in front and at end
				input = input.trim();
				
				// if user only entered spaces, tabs or nothing
				if (input.matches("\\s*")) {
					System.out.println("  - you have entered an invalid search, pls try again");
					continue;
				}
				// splitt string at single spaces and all parts of them to a Collection
				Collection<String> splittedString = new ArrayList<String>();
				splittedString.addAll(Arrays.asList(input.split(" ")));
				
				// initial the user is looking for nothing
				String number = null;
				String name = null;
				
				// walk through splittes array
				for (String str : splittedString) {
					// if regex finds a number, set number
					if (str.matches("\\d*")) {
						// if a second number is found, give the user an error
						if (number != null) {
							System.out.println("  - you have entered two numbers or a number with a whitespace between. please try again");
							continue;
						}
						number = str;
					} 
					// if string is non digit
					else if (str.matches("\\D*")) {
						// if name was not set before, initialize
						if (name == null) {
							name = "";
						}
						name += (" " + str);
					}
				}
				// if both is entered
				if (name != null && number != null) {
					book.searchForBoth(name.trim(), number);
				}
				// if name is only entered
				else if (name != null) {
					book.searchForName(name.trim());
				}					
				// if number is only entered
				else if (number != null) {
					book.searchForNumber(number.trim());
				}
				
			}
			
		}
		
		// close the input
		scanInput.close();
		
	}

}
