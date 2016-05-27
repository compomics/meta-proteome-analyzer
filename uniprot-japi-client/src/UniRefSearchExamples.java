package uk.ac.ebi.uniprot.dataservice.client.examples;

import uk.ac.ebi.kraken.interfaces.uniref.UniRefDatabaseType;
import uk.ac.ebi.kraken.interfaces.uniref.UniRefEntry;
import uk.ac.ebi.kraken.interfaces.uniref.member.UniRefMember;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefService;
import uk.ac.ebi.uniprot.dataservice.document.uniref.UniRefDocumentHelper;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.*;

import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printExampleHeader;
import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printSearchResults;

public class UniRefSearchExamples {

	/**
	 * Indicates the number of entry results that should be retrieved from the
	 * search
	 */
	private static final int DISPLAY_ENTRY_SIZE = 10;

	public static void main(String[] args) throws ServiceException {
		driveExamples();
	}

	public static void driveExamples() {

		ServiceFactory serviceFactoryInstance = Client
				.getServiceFactoryInstance();
		UniRefService uniRefService = serviceFactoryInstance
				.getUniRefQueryService();

		System.out.println("Staring up search service");
		uniRefService.start();

		try {

			searchForUniRefClusterIdentifier(uniRefService);
			searchForUniRefClusterName(uniRefService);
			searchForUniRefDatabaseType(uniRefService);
			searchForUniRefRepresentiveId(uniRefService);
			searchForRepresentativeAccession(uniRefService);
			searchForRepresentativeUpi(uniRefService);
			searchForRepresentativeProteionName(uniRefService);
			searchForMemberProteionName(uniRefService);
			searchForMemberOrganismName(uniRefService);
			searchForMemberTaxIdWithinOneDatabase(uniRefService);

		} catch (ServiceException e) {
			System.err.println("Error occurred whilst executing search"
					+ e.getMessage());
		} finally {
			// always remember to stop the service
			uniRefService.stop();
			System.out.println("service now stopped.");
		}
	}

	public static void searchForUniRefClusterIdentifier(
			UniRefService uniRefService) throws ServiceException {

		printExampleHeader("UniRef identifier search");
		String uniRefClusterId = "UniRef100_P99999";
		System.out.printf(
				"Search for UniRef entry with the given identifier: %s%n",
				uniRefClusterId);

		Query query = UniRefQueryBuilder.clusterId(uniRefClusterId);
		SearchExecutor searchExecutor = new SearchExecutor(uniRefService) {
			@Override
			List<String> extractValues(UniRefEntry entry) {
				String uniRefId = entry.getUniRefEntryId().getValue();
				return Collections.singletonList(uniRefId);
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query);
		printSearchResults(entries);
	}

	public static void searchForUniRefClusterName(UniRefService uniRefService)
			throws ServiceException {

		printExampleHeader("UniRef cluster name search");
		String uniRefClusterName = "Cytochrome c";
		System.out.printf("Search for UniRef entry with the given name: %s%n",
				uniRefClusterName);

		Query query = UniRefQueryBuilder.clusterName(uniRefClusterName);
		SearchExecutor searchExecutor = new SearchExecutor(uniRefService) {
			@Override
			List<String> extractValues(UniRefEntry entry) {
				String uniRefName = entry.getName().getValue();
				return Collections.singletonList(uniRefName);
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query,
				DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForUniRefDatabaseType(UniRefService uniRefService)
			throws ServiceException {

		printExampleHeader("UniRef database type search");
		UniRefDatabaseType uniRefDatabaseType = UniRefDatabaseType.UniRef100;
		System.out.printf(
				"Search for UniRef entry with the given database type: %s%n",
				uniRefDatabaseType.name());

		Query query = UniRefQueryBuilder
				.uniRefClusterDatabase(uniRefDatabaseType);

		SearchExecutor searchExecutor = new SearchExecutor(uniRefService) {
			@Override
			List<String> extractValues(UniRefEntry entry) {
				String uniRefDatabaseType = entry.getUniRefDatabase().getType()
						.name();
				return Collections.singletonList(uniRefDatabaseType);
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query,
				DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForUniRefRepresentiveId(
			UniRefService uniRefService) throws ServiceException {

		printExampleHeader("UniRef representative id search");
		String uniRefRepId = "CYC_HUMAN";
		System.out
				.printf("Search for UniRef entry with the given representative id: %s%n",
						uniRefRepId);

		Query query = UniRefQueryBuilder.representativeId(uniRefRepId);
		SearchExecutor searchExecutor = new SearchExecutor(uniRefService) {
			@Override
			List<String> extractValues(UniRefEntry entry) {
				String uniRefName = entry.getRepresentativeMember()
						.getMemberId().getValue();
				return Collections.singletonList(uniRefName);
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query,
				DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForRepresentativeAccession(
			UniRefService uniRefService) throws ServiceException {

		printExampleHeader("UniRef representative UniProt accession search");
		String uniRefRepUniProtAccession = "P99999";
		System.out
				.printf("Search for UniRef entry with the given representative UniProt accession: %s%n",
						uniRefRepUniProtAccession);

		Query query = UniRefQueryBuilder
				.representativeAccession(uniRefRepUniProtAccession);
		SearchExecutor searchExecutor = new SearchExecutor(uniRefService) {
			@Override
			List<String> extractValues(UniRefEntry entry) {
				String uniRefRepresentative = UniRefDocumentHelper
						.memberToString(entry.getRepresentativeMember());
				return Collections.singletonList(uniRefRepresentative);
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query,
				DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForRepresentativeUpi(UniRefService uniRefService)
			throws ServiceException {

		printExampleHeader("UniRef representive UPI search");
		String uniRefRepUpi = "UPI000013EAA0";
		System.out
				.printf("Search for UniRef entry with the given representative UPI: %s%n",
						uniRefRepUpi);

		Query query = UniRefQueryBuilder.representativeAccession(uniRefRepUpi);
		SearchExecutor searchExecutor = new SearchExecutor(uniRefService) {
			@Override
			List<String> extractValues(UniRefEntry entry) {
				String uniRefRepresentative = UniRefDocumentHelper
						.memberToString(entry.getRepresentativeMember());
				return Collections.singletonList(uniRefRepresentative);
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query,
				DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForRepresentativeProteionName(
			UniRefService uniRefService) throws ServiceException {

		printExampleHeader("UniRef representative protein name search");
		String uniRefRepProteinName = " Cytochrome c";
		System.out
				.printf("Search for UniRef entry with the given representative protein name: %s%n",
						uniRefRepProteinName);

		Query query = UniRefQueryBuilder
				.representativeProteinName(uniRefRepProteinName);
		SearchExecutor searchExecutor = new SearchExecutor(uniRefService) {
			@Override
			List<String> extractValues(UniRefEntry entry) {
				String uniRefRepresentative = UniRefDocumentHelper
						.memberToString(entry.getRepresentativeMember());
				return Collections.singletonList(uniRefRepresentative);
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query,
				DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForMemberProteionName(UniRefService uniRefService)
			throws ServiceException {

		printExampleHeader("UniRef member protein name search");
		String uniRefMemberProteinName = "Cytochrome c";
		System.out
				.printf("Search for UniRef entry with the given member protein name: %s%n",
						uniRefMemberProteinName);

		Query query = UniRefQueryBuilder
				.memberProteinName(uniRefMemberProteinName);
		SearchExecutor searchExecutor = new SearchExecutor(uniRefService) {
			@Override
			List<String> extractValues(UniRefEntry entry) {

				List<String> membersString = new ArrayList<>();
				String uniRefRepresentative = UniRefDocumentHelper
						.memberToString(entry.getRepresentativeMember());
				membersString.add(uniRefRepresentative);

				List<UniRefMember> members = entry.getMembers();
				for (UniRefMember member : members) {
					String uniRefMember = UniRefDocumentHelper
							.memberToString(member);
					membersString.add(uniRefMember);
				}
				return membersString;
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query,
				DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForMemberOrganismName(UniRefService uniRefService)
			throws ServiceException {

		printExampleHeader("UniRef member organism name search");
		String uniRefMemberOrganismName = "Gorilla gorilla gorilla";
		System.out
				.printf("Search for UniRef entry with the given member organism name: %s%n",
						uniRefMemberOrganismName);

		Query query = UniRefQueryBuilder
				.memberProteinName(uniRefMemberOrganismName);
		SearchExecutor searchExecutor = new SearchExecutor(uniRefService) {
			@Override
			List<String> extractValues(UniRefEntry entry) {

				List<String> membersString = new ArrayList<>();
				String uniRefRepresentative = UniRefDocumentHelper
						.memberToString(entry.getRepresentativeMember());
				membersString.add(uniRefRepresentative);

				List<UniRefMember> members = entry.getMembers();
				for (UniRefMember member : members) {
					String uniRefMember = UniRefDocumentHelper
							.memberToString(member);
					membersString.add(uniRefMember);
				}
				return membersString;
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query,
				DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	public static void searchForMemberTaxIdWithinOneDatabase(
			UniRefService uniRefService) throws ServiceException {

		printExampleHeader("UniRef member taxon ID search within one UniRef database");
		int uniRefMemberTaxId = 9606;
		UniRefDatabaseType uniRefDatabaseType = UniRefDatabaseType.UniRef50;

		System.out
				.printf("Search for UniRef entry with the given member taxId: %s within one UniRef databae%s%n",
						uniRefMemberTaxId, uniRefDatabaseType.name());

		Query query = UniRefQueryBuilder.memberTaxonId(uniRefMemberTaxId).and(
				UniRefQueryBuilder.uniRefClusterDatabase(uniRefDatabaseType));
		SearchExecutor searchExecutor = new SearchExecutor(uniRefService) {
			@Override
			List<String> extractValues(UniRefEntry entry) {

				List<String> membersString = new ArrayList<>();
				String uniRefRepresentative = UniRefDocumentHelper
						.memberToString(entry.getRepresentativeMember());
				membersString.add(uniRefRepresentative);

				List<UniRefMember> members = entry.getMembers();
				for (UniRefMember member : members) {
					String uniRefMember = UniRefDocumentHelper
							.memberToString(member);
					membersString.add(uniRefMember);
				}
				return membersString;
			}
		};

		Map<String, List<String>> entries = searchExecutor.executeSearch(query,
				DISPLAY_ENTRY_SIZE);
		printSearchResults(entries);
	}

	/**
	 * Class that given a query executes the search, and returns the results as
	 * a Map
	 */
	private abstract static class SearchExecutor {

		private static final int NO_LIMIT = Integer.MAX_VALUE;

		private final UniRefService uniRefService;

		public SearchExecutor(UniRefService uniRefService) {
			this.uniRefService = uniRefService;
		}

		Map<String, List<String>> executeSearch(Query query)
				throws ServiceException {
			return executeSearch(query, NO_LIMIT);
		}

		Map<String, List<String>> executeSearch(Query query, int limit)
				throws ServiceException {

			QueryResult<UniRefEntry> searchResult = uniRefService
					.getEntries(query);

			Map<String, List<String>> entries = new HashMap<>();

			while (searchResult.hasNext() && limit > 0) {
				UniRefEntry entry = searchResult.next();
				String uniRefId = entry.getUniRefEntryId().getValue();

				entries.put(uniRefId, extractValues(entry));
				limit--;
			}

			return entries;
		}

		abstract List<String> extractValues(UniRefEntry entry);
	}
}
