package uk.ac.ebi.uniprot.dataservice.client.examples;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains several utility methods for printing the results of a search result
 */
public final class PrintUtils {
	public static void printExampleHeader(String headerTitle) {
		System.out.printf("=========== %s ==========%n", headerTitle);
	}

	public static void printSearchResults(Map<String, List<String>> results) {
		System.out.println(String.format("Found %d entries:", results.size()));

		Set<String> entries = results.keySet();

		for (String entry : entries) {
			System.out.println("entry: " + entry);
			printEntryComponents(results.get(entry));
		}
	}

	private static void printEntryComponents(List<String> entryComponents) {
		for (String entryComponent : entryComponents) {
			System.out.println("   " + entryComponent);
		}
	}
}
