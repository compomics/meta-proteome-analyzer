package uk.ac.ebi.uniprot.dataservice.client.examples;

import uk.ac.ebi.kraken.interfaces.common.Sequence;
import uk.ac.ebi.kraken.interfaces.uniparc.UniParcEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniparc.UniParcComponent;
import uk.ac.ebi.uniprot.dataservice.client.uniparc.UniParcQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniparc.UniParcService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.List;

import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printExampleHeader;

public class UniParcRetrievalExamples {
    /**
     * Indicates the number of entry results that should be retrieved from the search
     */
    private static final int DISPLAY_ENTRY_SIZE = 10;

    public static void main(String[] args) {
        driveExamples();
    }

    public static void driveExamples() {
        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniParcService uniParcService = serviceFactoryInstance.getUniParcQueryService();

        try {
            // start the service
            uniParcService.start();

            retrieveEntryUsingEntryIdentifier(uniParcService);

            retrieveEntryUsingUniProtAccession(uniParcService);

            retrieveEntriesUsingQuery(uniParcService);

            retrieveUniParcSequencesUsingQuery(uniParcService);

        } catch (ServiceException e) {
            System.err.println("Error occurred whilst executing retrieval: " + e.getMessage());
        } finally {
            // always remember to stop the service
            uniParcService.stop();
            System.out.println("service now stopped.");
        }
    }

    public static void retrieveEntryUsingEntryIdentifier(UniParcService uniParcService) throws ServiceException {
        printExampleHeader("Retrieve UniParc entry using the UniParc entry identifier");

        String identifier = "UPI0000000001";

        System.out.println("Retrieve entry with id: " + identifier);

        UniParcEntry entry = uniParcService.getEntry(identifier);

        System.out.println(entry.getUniParcId().getValue());
    }

    public static void retrieveEntryUsingUniProtAccession(UniParcService uniParcService) throws ServiceException {
        printExampleHeader("Retrieve UniParc entry with given UniProt accession");

        String accession = "P99999";

        System.out.println("Retrieve UniParc entry containing UniProt accession: " + accession);

        UniParcEntry entry = uniParcService.getEntryFromUniProtAccession(accession);

        System.out.println(entry.getUniParcId().getValue());
    }

    public static void retrieveEntriesUsingQuery(UniParcService uniParcService) throws ServiceException {
        printExampleHeader("Retrieve a set of entries that match the query criteria");

        String organismName = "Homo sapiens";

        System.out.printf("Retrieve first %d UniParc entries that contain the given organism name: %s within at least" +
                "one of their database cross-references%n", DISPLAY_ENTRY_SIZE, organismName);

        Query query = UniParcQueryBuilder.organismName(organismName);

        QueryResult<UniParcEntry> queryResult = uniParcService.getEntries(query);

        int entryCounter = 0;
        while (queryResult.hasNext() && entryCounter < DISPLAY_ENTRY_SIZE) {
            UniParcEntry entry = queryResult.next();
            System.out.println(entry.getUniParcId().getValue());

            entryCounter++;
        }
    }

    public static void retrieveUniParcSequencesUsingQuery(UniParcService uniParcService) throws ServiceException {
        printExampleHeader("Retrieve a set of UniParc sequences that match the query criteria");

        String organismName = "Homo sapiens";

        System.out.printf("Retrieve first %d UniParc entries that contain the given organism name: %s within at least " +
                "one of their database cross-references%n", DISPLAY_ENTRY_SIZE, organismName);

        Query query = UniParcQueryBuilder.organismName(organismName);

        QueryResult<UniParcComponent<Sequence>> queryResult = uniParcService.getSequences(query);

        int sequenceCounter = 0;
        while (queryResult.hasNext() && sequenceCounter < DISPLAY_ENTRY_SIZE) {
            UniParcComponent<Sequence> entrySequence = queryResult.next();
            printEntrySequence(entrySequence);
            sequenceCounter++;
        }
    }

    public static void printEntrySequence(UniParcComponent<Sequence> entrySequence) {
        Sequence sequence = returnFirstSequence(entrySequence.getComponents());

        System.out.println("Entry {");
        System.out.println("\tId: " + entrySequence.getEntryIdentifier().getValue());
        System.out.println("\tSequence {");
        System.out.println("\t\tSequence: " + sequence.getValue());
        System.out.println("\t\tLength: " + sequence.getLength());
        System.out.println("\t\tChecksum: " + sequence.getCRC64());
        System.out.println("\t}");
        System.out.println("}");
    }

    public static Sequence returnFirstSequence(List<Sequence> sequences) {
        return sequences.get(0);
    }
}
