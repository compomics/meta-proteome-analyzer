package uk.ac.ebi.uniprot.dataservice.client.examples;

import uk.ac.ebi.kraken.interfaces.uniref.UniRefDatabaseType;
import uk.ac.ebi.kraken.interfaces.uniref.UniRefEntry;
import uk.ac.ebi.kraken.interfaces.uniref.UniRefEntryName;
import uk.ac.ebi.kraken.interfaces.uniref.member.UniRefRepresentativeMember;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefComponent;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefField;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniref.UniRefService;
import uk.ac.ebi.uniprot.dataservice.document.uniref.UniRefDocumentHelper;
import uk.ac.ebi.uniprot.dataservice.query.Query;

public class UniRefRetrievalExamples {
	/**
	 * Indicates the number of entry results that should be retrieved from the
	 * search
	 */
	private static final int DISPLAY_ENTRY_SIZE = 10;

	public static void main(String[] args) throws ServiceException {
		ServiceFactory serviceFactoryInstance = Client
				.getServiceFactoryInstance();
		UniRefService service = serviceFactoryInstance.getUniRefQueryService();
		service.start();

		try {
			retrieveTaxAndMember(service);
			retrieveEntryById(service);
			retrieveEntryByUniProtAccession(service);
			retrieveEntryByUniParcId(service);
			retrieveRepresentativeByTaxIdWithinOneDatabase(service);
			retrieveEntryNameByDatabaseType(service);
		} catch (ServiceException e) {
			System.err.println("Error occurred whilst executing retrieval"
					+ e.getMessage());
		}finally{
			service.stop();
			System.out.println("service now stopped.");
		}
	}

	private static void printInfoToStdOut(String msg) {
		System.out.println("[INFO] " + msg);
	}

	private static void printOutEntry(UniRefEntry entry) {
		printInfoToStdOut("> UniRef entry name = " + entry.getName().getValue());
		printInfoToStdOut("  UniRef entry ID   = " + entry.getUniRefEntryId().getValue());
		printInfoToStdOut("  Database Type     = "
				+ entry.getUniRefDatabase().getType().getIdentity());
	}

	public static void retrieveTaxAndMember(UniRefService service) throws ServiceException {

		printInfoToStdOut("=========== Retrieve entries by taxon and member ==========");
		Query query = UniRefQueryBuilder.query(
				UniRefField.Search.rep_member_tax_id, "500485").and(
				UniRefQueryBuilder.query(UniRefField.Search.member_id,
						"B6HP37_PENCW"));

		printInfoToStdOut("executing query: " + query);

		QueryResult<UniRefEntry> entries = service.getEntries(query);
		while (entries.hasNext()) {
			UniRefEntry entry = entries.next();
			printOutEntry(entry);
		}
	}

	public static void retrieveEntryById(UniRefService service) throws ServiceException {
		printInfoToStdOut("=========== Retrieve entry by UniRef entry id ==========");
		String id = "UniRef50_A0A0G4JWV1";
		UniRefEntry entry = service.getEntry(id);
		printOutEntry(entry);
	}

	public static void retrieveEntryByUniProtAccession(UniRefService service)
			throws ServiceException {
		printInfoToStdOut("=========== Retrieve entry by UniProt accession within UniRef 100 database ==========");
		String accession = "B6HP37";
		UniRefDatabaseType uniRefDatabaseType = UniRefDatabaseType.UniRef100;
		UniRefEntry entry = service.getEntryByUniProtAccession(accession,
				uniRefDatabaseType);
		printOutEntry(entry);
	}

	public static void retrieveEntryByUniParcId(UniRefService service) throws ServiceException {
		printInfoToStdOut("=========== Retrieve entry by UniProt accession within UniRef 90 database ==========");
		String upi = "UPI0004359BFA";
		UniRefDatabaseType uniRefDatabaseType = UniRefDatabaseType.UniRef90;
		UniRefEntry entry = service.getEntryByUniProtAccession(upi, uniRefDatabaseType);
		printOutEntry(entry);
	}

	public static void retrieveRepresentativeByTaxIdWithinOneDatabase(UniRefService service)
			throws ServiceException {

		printInfoToStdOut("=========== Retrieve entry by UniRef member taxon ID within UniRef 50 database ==========");
		int uniRefMemberTaxId = 9606;
		UniRefDatabaseType uniRefDatabaseType = UniRefDatabaseType.UniRef50;

		Query query = UniRefQueryBuilder.memberTaxonId(uniRefMemberTaxId).and(
				UniRefQueryBuilder.uniRefClusterDatabase(uniRefDatabaseType));

		QueryResult<UniRefComponent<UniRefRepresentativeMember>> repList = service
				.getUniRefRepresentative(query);

		int entryCount = 0;
		while (repList.hasNext() && entryCount < DISPLAY_ENTRY_SIZE) {
			UniRefComponent<UniRefRepresentativeMember> rep = repList.next();
			System.out.println(rep.getUniRefEntryId().getValue()
					+ " "
					+ UniRefDocumentHelper.memberToString(rep.getComponent()
							.get(0)));
			entryCount++;
		}
	}

	public static void retrieveEntryNameByDatabaseType(UniRefService service)
			throws ServiceException {

		printInfoToStdOut("=========== Retrieve entry by UniRef database type ==========");
		UniRefDatabaseType uniRefDatabaseType = UniRefDatabaseType.UniRef100;

		Query query = UniRefQueryBuilder
				.uniRefClusterDatabase(uniRefDatabaseType);

		QueryResult<UniRefComponent<UniRefEntryName>> entryNameList = service
				.getUniRefEntryName(query);

		int entryCount = 0;
		while (entryNameList.hasNext() && entryCount < DISPLAY_ENTRY_SIZE) {
			UniRefComponent<UniRefEntryName> entryName = entryNameList.next();
			System.out.println(entryName.getUniRefEntryId().getValue() + " "
					+ entryName.getComponent().get(0));
			entryCount++;
		}
	}

}
