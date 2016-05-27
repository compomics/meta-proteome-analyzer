package uk.ac.ebi.uniprot.dataservice.client.examples;

import uk.ac.ebi.kraken.interfaces.uniparc.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniparc.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniparc.UniParcEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniparc.UniParcQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniparc.UniParcService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.*;
import java.util.stream.Collectors;

import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printExampleHeader;
import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printSearchResults;

public class UniParcSearchExamples {
	/**
	 * Indicates the number of entry results that should be retrieved from the search
	 */
	private static final int DISPLAY_ENTRY_SIZE = 10;

	public static void main(String[] args) throws ServiceException {
		driveExamples();
	}

	public static void driveExamples() {
		ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
		UniParcService uniParcService = serviceFactoryInstance.getUniParcQueryService();

		System.out.println("Staring up search service");
		uniParcService.start();

		try {
			searchForUniParcIdentifier(uniParcService);

			searchForDatabaseType(uniParcService);

			searchForDatabaseAccession(uniParcService);

			searchForUniProtDatabaseAccession(uniParcService);

			searchForGeneName(uniParcService);

			searchForTaxonomicIdentifier(uniParcService);

			searchForOrganismName(uniParcService);

		} catch (ServiceException e) {
			System.err.println("Error occurred whilst executing search" + e.getMessage());
		} finally {
			//always remember to stop the service
			uniParcService.stop();
			System.out.println("service now stopped.");
		}
	}

	public static void searchForUniParcIdentifier(UniParcService uniParcService) throws ServiceException {
		printExampleHeader("UniParc identifier search");

		String uniParcId = "UPI0000000001";
		System.out.printf("Search for UniParc entry with the given identifier: %s%n", uniParcId);

		Query query = UniParcQueryBuilder.id(uniParcId);

		SearchExecutor searchExecutor = new SearchExecutor(uniParcService) {
			@Override
			List<String> extractValues(UniParcEntry entry) {
				String uniParcId = entry.getUniParcId().getValue();
				return Collections.singletonList(uniParcId);
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query);
		printSearchResults(entries);
	}

	public static void searchForDatabaseType(UniParcService uniParcService) throws ServiceException {
		printExampleHeader("Active database type search");

		DatabaseType dbType = DatabaseType.EMBL;
		System.out.printf("Search for UniParc entries that contain at least one active database cross-reference of " +
				"type: %s%n", dbType);
		Query query = UniParcQueryBuilder.database(dbType);

		SearchExecutor searchExecutor = new SearchExecutor(uniParcService) {
			@Override
			List<String> extractValues(UniParcEntry entry) {
				Set<DatabaseCrossReference> dbXrefs = entry.getActiveDatabaseCrossReferences();

				return dbXrefs.stream()
						.map(xref -> xref.getDatabase().getName())
						.distinct()
						.collect(Collectors.toList());
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForDatabaseAccession(UniParcService uniParcService) throws ServiceException {
		printExampleHeader("Active database with accession search");

		String dbAcc = "AAF63732";
		System.out.printf("Search for UniParc entry which contains the database accession: %s one of its xrefs%n",
				dbAcc);
		Query query = UniParcQueryBuilder.accession(dbAcc);

		SearchExecutor searchExecutor = new SearchExecutor(uniParcService) {
			@Override
			List<String> extractValues(UniParcEntry entry) {
				Set<DatabaseCrossReference> dbXrefs = entry.getActiveDatabaseCrossReferences();

				return dbXrefs.stream()
						.map(DatabaseCrossReference::getAccession)
						.collect(Collectors.toList());
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query);
		printSearchResults(entries);
	}

	public static void searchForUniProtDatabaseAccession(UniParcService uniParcService) throws ServiceException {
		printExampleHeader("UniProt database accession search");

		String uniProtAcc = "P99999";
		System.out.printf("Search for UniParc entry that contains UniProt accession: %s within a UniProt " +
				"database cross-reference (Swiss-Prot/TrEMBL)%n", uniProtAcc);
		Query query = UniParcQueryBuilder.uniProtAccession(uniProtAcc);

		SearchExecutor searchExecutor = new SearchExecutor(uniParcService) {
			@Override
			List<String> extractValues(UniParcEntry entry) {
				Set<DatabaseCrossReference> dbXrefs = entry.getActiveDatabaseCrossReferences();

				return dbXrefs.stream()
						.map(dbXref -> dbXref.getAccession() + " : " + dbXref.getDatabase().getName())
						.collect(Collectors.toList());
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query);
		printSearchResults(entries);
	}

	public static void searchForGeneName(UniParcService uniParcService) throws ServiceException {
		printExampleHeader("Active database with gene name search");

		String geneName = "CYCS";
		System.out.printf("Search for first %d UniParc entries that contain the given gene name: %s within at least " +
				"one of their database cross-references%n", DISPLAY_ENTRY_SIZE, geneName);
		Query query = UniParcQueryBuilder.gene(geneName);

		SearchExecutor searchExecutor = new SearchExecutor(uniParcService) {
			@Override
			List<String> extractValues(UniParcEntry entry) {
				Set<DatabaseCrossReference> dbXrefs = entry.getActiveDatabaseCrossReferences();

				return dbXrefs.stream()
						.map(DatabaseCrossReference::getGeneName)
						.filter(xref -> !xref.isEmpty())
						.distinct()
						.collect(Collectors.toList());
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForTaxonomicIdentifier(UniParcService uniParcService) throws ServiceException {
		printExampleHeader("Active database with taxonomic identifier search");

		int taxonId = 9606;
		System.out.printf("Search for first %d UniParc entries that contain the given taxonomic identifier: %d within" +
				" at least one of their database cross-references%n", DISPLAY_ENTRY_SIZE, taxonId);
		Query query = UniParcQueryBuilder.taxonId(taxonId);

		SearchExecutor searchExecutor = new SearchExecutor(uniParcService) {
			@Override
			List<String> extractValues(UniParcEntry entry) {
				Set<DatabaseCrossReference> dbXrefs = entry.getActiveDatabaseCrossReferences();

				return dbXrefs.stream()
						.map(xref -> String.valueOf(xref.getTaxonomyId()))
						.distinct()
						.collect(Collectors.toList());
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForOrganismName(UniParcService uniParcService) throws ServiceException {
		printExampleHeader("Active database with organism name search");

		String organismName = "Homo sapiens";
		int taxonId = 9606;
		System.out.printf("Search for first %d UniParc entries that contain the given organism name: %s (%d) within " +
				"at least one of their database cross-references%n", DISPLAY_ENTRY_SIZE, organismName, taxonId);
		Query query = UniParcQueryBuilder.organismName(organismName);

		SearchExecutor searchExecutor = new SearchExecutor(uniParcService) {
			@Override
			List<String> extractValues(UniParcEntry entry) {
				Set<DatabaseCrossReference> dbXrefs = entry.getActiveDatabaseCrossReferences();

				return dbXrefs.stream()
						.filter(xref -> xref.getTaxonomyId() > 0)
						.map(xref -> String.valueOf(xref.getTaxonomyId()))
						.distinct()
						.collect(Collectors.toList());
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	/**
	 * Class that given a query executes the search, and returns the results as a Map
	 */
	private abstract static class SearchExecutor {
		private static final int NO_LIMIT = Integer.MAX_VALUE;

		private final UniParcService uniParcService;

		public SearchExecutor(UniParcService uniParcService) {
			this.uniParcService = uniParcService;
		}

		Map<String, List<String>> executeSearch(Query query) throws ServiceException {
			return executeSearch(query, NO_LIMIT);
		}

		Map<String, List<String>> executeSearch(Query query, int limit) throws ServiceException {
			QueryResult<UniParcEntry> searchResult = uniParcService.getEntries(query);

			Map<String, List<String>> entries = new HashMap<>();

			while (searchResult.hasNext() && limit > 0) {
				UniParcEntry entry = searchResult.next();
				String accession = entry.getUniParcId().getValue();

				entries.put(accession, extractValues(entry));
				limit--;
			}

			return entries;
		}

		abstract List<String> extractValues(UniParcEntry entry);
	}
}