package uk.ac.ebi.uniprot.dataservice.client.examples;

import uk.ac.ebi.kraken.interfaces.common.Value;
import uk.ac.ebi.kraken.interfaces.uniprot.*;
import uk.ac.ebi.kraken.interfaces.uniprot.citationsNew.Citation;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Comment;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.go.Go;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Field;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Section;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureLocation;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.QuerySpec;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtField;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printExampleHeader;
import static uk.ac.ebi.uniprot.dataservice.client.examples.PrintUtils.printSearchResults;

/**
 * This example shows how to create and use a basic UniProt {@link uk.ac.ebi.uniprot.dataservice.query.Query} with a
 * {@link uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService} and subsequently retrieve various parts of the
 * entries that satisfy the query.
 */
public class UniProtSearchExamples {
    /**
     * Indicates the number of entry results that should be retrieved from the search
     */
    private static final int DISPLAY_ENTRY_SIZE = 10;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void main(String[] args) {
        driveExamples();
    }

    public static void driveExamples() {
        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniProtService uniProtService = serviceFactoryInstance.getUniProtQueryService();

        try {
            System.out.println("Staring up search service");

            uniProtService.start();

            searchByProteinID(uniProtService);

            searchIsoformByProteinID(uniProtService);

            searchByPrimaryAccession(uniProtService);

            searchIsoformByPrimaryAccession(uniProtService);

            searchBySecondaryAccession(uniProtService);

            searchForSwissProt(uniProtService);

            searchForEntriesCreatedBetween(uniProtService);

            searchForEntriesCreatedAfter(uniProtService);

            searchForEntriesWithPublicationDatesBetween(uniProtService);

            searchForPublicationPubmedId(uniProtService);

            searchForPublicationTitle(uniProtService);

            searchForComments(uniProtService);

            searchForIsoformComments(uniProtService);

            searchForFeatures(uniProtService);

            searchForGoTerm(uniProtService);

            searchForDatabaseCrossReference(uniProtService);

            searchForProteinExistence(uniProtService);

            searchForEntriesWithProteinName(uniProtService);

            searchForEntriesWithEcNumber(uniProtService);

            searchForFragmentedProteinEntries(uniProtService);

            searchForEntriesWithGeneName(uniProtService);

            searchForEntriesWithOrganismName(uniProtService);

            searchForEntriesWithOrganismTaxonomy(uniProtService);

            searchForEntriesWithLineage(uniProtService);

            searchForEntriesWithTaxonIdInLineage(uniProtService);

            searchForEntriesWithKeywords(uniProtService);

            searchForEntriesWithOrganelle(uniProtService);

            searchOnlyIsoformEntries(uniProtService);

            searchIncludeIsoformEntries(uniProtService);

            getEntryByIsoformAccession(uniProtService);

            orExample(uniProtService);

            andExample(uniProtService);
        } catch (ServiceException e) {
            System.err.println("Error occurred whilst executing search" + e.getMessage());
        } finally {
            //always remember to stop the service
            uniProtService.stop();
            System.out.println("service now stopped.");
        }
    }

    public static void searchByProteinID(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search by protein ID");

        String searchTerm = "CYC_HUMAN";

        System.out.println("Search for entry with id: " + searchTerm);
        Query query = UniProtQueryBuilder.id(searchTerm);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                return Collections.singletonList(entry.getUniProtId().getValue());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query);
        printSearchResults(entries);
    }

    public static void searchByPrimaryAccession(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search by primary accessions");

        Set<String> searchTerms = asSet("P99999", "P99998");

        System.out.println("Search for entry with primary accession: " + searchTerms);

        Query query = UniProtQueryBuilder.accessions(searchTerms);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                return Collections.singletonList(entry.getPrimaryUniProtAccession().getValue());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query);
        printSearchResults(entries);
    }

    public static void searchIsoformByProteinID(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search isoform entries by protein ID");

        String searchTerm = "14310_ARATH_2";

        System.out.println("Search for isoform entry with id: " + searchTerm);
        Query query = UniProtQueryBuilder.id(searchTerm);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService, EnumSet.of(QuerySpec.WithIsoform)) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                return Collections.singletonList(entry.getUniProtId().getValue());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query);
        printSearchResults(entries);
    }

    public static void searchIsoformByPrimaryAccession(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search isofrom entries by primary accessions");

        Set<String> searchTerms = asSet("P48347", "P48347-1", "P48347-2");

        System.out.println("Search for isoform entry with primary accession: " + searchTerms);

        Query query = UniProtQueryBuilder.accessions(searchTerms);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService, EnumSet.of(QuerySpec.WithIsoform)) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                return Collections.singletonList(entry.getPrimaryUniProtAccession().getValue());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query);
        printSearchResults(entries);
    }

    public static void getEntryByIsoformAccession(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("get entry by accession support isoform");

        UniProtEntry entry = uniProtService.getEntry("P48347-1");

        System.out.println(entry.getPrimaryUniProtAccession().getValue());

    }

    public static void searchBySecondaryAccession(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search by secondary accessions");

        Set<String> searchTerms = asSet("A4D166", "P00001");

        System.out.println("Search for entry with secondary accession: " + searchTerms);

        Query query = UniProtQueryBuilder.accessions(searchTerms);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<SecondaryUniProtAccession> secAccs = entry.getSecondaryUniProtAccessions();
                return secAccs.stream().map(Value::getValue).collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query);
        printSearchResults(entries);
    }

    public static void searchForSwissProt(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for Swiss-Prot entries");
        System.out.printf("Search for first %d Swiss-Prot entries%n", DISPLAY_ENTRY_SIZE);

        Query query = UniProtQueryBuilder.swissprot();

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                UniProtEntryType entryType = entry.getType();
                return Collections.singletonList(entryType.getValue());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForEntriesCreatedBetween(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for first " + DISPLAY_ENTRY_SIZE + " protein entries created between");

        LocalDate startDate = LocalDate.of(2008, 1, 1);
        LocalDate endDate = LocalDate.of(2008, 12, 1);

        System.out.printf("Search for first %d entries created between %s and %s%n", DISPLAY_ENTRY_SIZE,
                startDate.toString(), endDate.toString());

        Query query = UniProtQueryBuilder.created(convertToDate(startDate), convertToDate(endDate));

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                Date creationDate = entry.getEntryAudit().getFirstPublicDate();
                return Collections.singletonList(convertToString(creationDate));
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForEntriesCreatedAfter(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for protein entries created after");

        LocalDate startDate = LocalDate.of(2008, 1, 1);

        System.out.printf("Search for first %d entries created after %s%n", DISPLAY_ENTRY_SIZE, startDate.toString());

        Query query = UniProtQueryBuilder.after(UniProtField.SearchDate.created, convertToDate(startDate));

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                Date creationDate = entry.getEntryAudit().getFirstPublicDate();
                return Collections.singletonList(convertToString(creationDate));
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForEntriesWithPublicationDatesBetween(UniProtService uniProtService)
            throws ServiceException {
        printExampleHeader("Search for protein entries with publications published between date interval");

        LocalDate startDate = LocalDate.of(2008, 1, 1);
        LocalDate endDate = LocalDate.of(2008, 12, 1);

        System.out.printf("Search for first %d protein entries with publications published between %s and %s%n",
                DISPLAY_ENTRY_SIZE, startDate.toString(), endDate.toString());
        Query query = UniProtQueryBuilder.between(UniProtField.SearchDate.lit_pubdate, convertToDate(startDate),
                convertToDate(endDate));

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            public String formatCitation(Citation citation) {
                String citationText = "Title:" + citation.getTitle().getValue() + "\n";
                citationText += "Published: " + citation.getPublicationDate().getValue();
                return citationText;
            }

            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<Citation> citations = entry.getCitationsNew();
                return citations.stream().map(this::formatCitation).collect(Collectors.toList());
            }

        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForPublicationPubmedId(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search by publication pubmed ID");

        String pubmedId = "8905231";

        System.out.println("Searching for entries with publication pubmed ID: " + pubmedId);
        Query query = UniProtQueryBuilder.pubmed(pubmedId);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {

                List<Citation> citations = entry.getCitationsNew();
                return citations.stream().filter(Citation::hasTitle).map(c -> c.getTitle().getValue())
                        .collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    public static void searchForPublicationTitle(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search by publication title");

        String title = "Sequence analysis of the genome of the unicellular cyanobacterium " +
                "Synechocystis sp. strain PCC6803. II. Sequence determination of the " +
                "entire genome and assignment of potential protein-coding regions.";

        System.out.println("Searching for entries with publication title: " + title);
        Query query = UniProtQueryBuilder.title(title);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {

                List<Citation> citations = entry.getCitationsNew();
                return citations.stream().filter(Citation::hasTitle).map(c -> c.getTitle().getValue())
                        .collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    //By default, the isoform entries is not included in the search result.
    //to specify you want to search for isoform, use the isoformOnly() query in the QueryBuilder.
    public static void searchOnlyIsoformEntries(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for IsoForm entries only");

        String isoformSearch = "L-fuconate dehydratase";
        System.out.println("Searching for isoform entries with protein name: " + isoformSearch);
        Query query = UniProtQueryBuilder.proteinName(isoformSearch);
        Query and = query.and(UniProtQueryBuilder.isoformOnly());

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                return Collections.singletonList(entry.getPrimaryUniProtAccession().getValue());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(and, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    //By default, the isoform entries is not included in the search result.
    //to indicate you want the isoform entries, using the QuerySpec.withisoform.
    public static void searchIncludeIsoformEntries(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search including IsoForm entries");

        String isoformSearch = "L-fuconate dehydratase";
        System.out.println("Searching for isoform entries with protein name: " + isoformSearch);
        Query query = UniProtQueryBuilder.proteinName(isoformSearch);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService, EnumSet.of(QuerySpec.WithIsoform)) {
            @Override
            List<String> extractValues(UniProtEntry entry) {

                return Collections.singletonList(entry.getPrimaryUniProtAccession().getValue());

            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }


    public static void searchForComments(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for comments by comment type");

        CommentType ccType = CommentType.DISEASE;

        System.out.println("Searching for entries with comment type: " + ccType.toDisplayName());
        Query query = UniProtQueryBuilder.commentsType(ccType);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                Collection<Comment> comments = entry.getComments();
                List<String> commentList = new ArrayList<>();

                for (Comment comment : comments) {
                    StringBuilder commentSB = new StringBuilder();

                    commentSB.append(comment.getCommentType().toDisplayName())
                            .append(": ")
                            .append(comment.toString())
                            .append(", ")
                            .append(comment.getCommentStatus().getValue());

                    commentList.add(commentSB.toString());
                }

                return commentList;
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    public static void searchForIsoformComments(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for isoforms in comment block's alternative products");

        CommentType ccType = CommentType.ALTERNATIVE_PRODUCTS;

        System.out.println("Searching for isoform P48347-1 in: " + ccType.toDisplayName());
        Query query = UniProtQueryBuilder.comments(ccType, "P48347-1");

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                Collection<Comment> comments = entry.getComments();
                List<String> commentList = new ArrayList<>();

                for (Comment comment : comments) {
                    StringBuilder commentSB = new StringBuilder();

                    commentSB.append(comment.getCommentType().toDisplayName())
                            .append(", ")
                            .append(comment.getCommentStatus().getValue());

                    commentList.add(commentSB.toString());
                }

                return commentList;
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    public static void searchForFeatures(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for features by feature type");

        FeatureType ftType = FeatureType.DISULFID;

        System.out.println("Searching for entries with feature type: " + ftType.getDisplayName());
        Query query = UniProtQueryBuilder.featuresType(ftType);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<String> featureList = new ArrayList<>();
                List<Feature> features = entry.getFeatures();

                for (Feature feature : features) {
                    StringBuilder featureSB = new StringBuilder();
                    FeatureLocation featureLocation = feature.getFeatureLocation();
                    featureSB.append(feature.getType().getName())
                            .append(": ")
                            .append(", ")
                            .append(featureLocation.getStartModifier().toString())
                            .append(featureLocation.getStart())
                            .append(", ")
                            .append(featureLocation.getEnd())
                            .append(", ")
                            .append(featureLocation.getEndModifier());
                    featureList.add(featureSB.toString());
                }

                return featureList;
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    public static void searchForGoTerm(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search by Gene Ontology (GO) term");

        String goTerm = "DNA metabolic process"; // GO:0006259

        System.out.println("Searching for entries with GO term: " + goTerm);
        Query query = UniProtQueryBuilder.goTerm(goTerm);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<Go> goTerms = entry.getGoTerms();
                return goTerms.stream().filter(Go::hasGoId).map(g -> g.getGoId().getValue())
                        .collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    public static void searchForDatabaseCrossReference(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search by database cross-reference");

        DatabaseType dbTypeInterPro = DatabaseType.INTERPRO;

        System.out.println("Searching for entries containing database cross-references of type: " + dbTypeInterPro
                .getName());
        Query query = UniProtQueryBuilder.xref(dbTypeInterPro);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<String> dbxStringList = new ArrayList<>();
                for (DatabaseCrossReference dbx : entry.getDatabaseCrossReferences()) {
                    StringBuilder dbxResultSB = new StringBuilder();
                    dbxResultSB.append(dbx.getDatabase())
                            .append(", ").append(dbx.getPrimaryId())
                            .append(", ").append(dbx.getDescription());
                    if (dbx.hasThird()) {
                        dbxResultSB.append(", ").append(dbx.getThird());
                    }
                    if (dbx.hasFourth()) {
                        dbxResultSB.append(", ").append(dbx.getFourth());
                    }
                    dbxStringList.add(dbxResultSB.toString());
                }
                return dbxStringList;
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    public static void searchForProteinExistence(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search by protein existence");

        ProteinExistence transcriptLevelExistence = ProteinExistence.TRANSCRIPT_LEVEL;

        System.out.println("Searching for entries with protein existence level: " + transcriptLevelExistence
                .getDisplayName());
        Query query = UniProtQueryBuilder.proteinExistence(transcriptLevelExistence);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                return Arrays.asList(entry.getUniProtId().getValue());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    public static void searchForEntriesWithProteinName(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for entries with partial protein name");

        String searchTerm = "14-3-3 protein";
        System.out
                .printf("Search for first %d protein entries with protein name: %s%n", DISPLAY_ENTRY_SIZE, searchTerm);

        Query query = UniProtQueryBuilder.proteinName(searchTerm);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                ProteinDescription description = entry.getProteinDescription();

                Name recName = description.getRecommendedName();
                String recFullText = formatFullName(recName);

                return Collections.singletonList(recFullText);
            }

            private String formatFullName(Name name) {
                List<Field> fullNameFields = name.getFieldsByType(FieldType.FULL);

                String nameText = "";

                if (!fullNameFields.isEmpty()) {
                    Field fullNameField = fullNameFields.get(0);

                    nameText = name.getNameType().getValue() + " ";
                    nameText += fullNameField.getType().getValue() + ": ";
                    nameText += fullNameField.getValue();
                }

                return nameText;
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForEntriesWithEcNumber(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for entries with EC number");

        String searchTerm = "2.7.7.48";
        System.out.printf("Search for first %d protein entries with EC number: %s%n", DISPLAY_ENTRY_SIZE, searchTerm);

        Query query = UniProtQueryBuilder.ec(searchTerm);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                ProteinDescription description = entry.getProteinDescription();
                Section mainSection = description.getSection();

                return ecNumberText(mainSection);
            }

            private List<String> ecNumberText(Section section) {
                List<Name> names = section.getNames();
                return names.stream().map(Name::getFields)
                        .flatMap(Collection::stream).filter(field -> field.getType() == FieldType.EC)
                        .map(Field::getValue).collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForFragmentedProteinEntries(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for fragmented entries");
        System.out.printf("Search for first %d protein entries that are fragments%n", DISPLAY_ENTRY_SIZE);

        Query query = UniProtQueryBuilder.fragment(true);
        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                return Collections.singletonList("Fragment: " + entry.isFragment());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForEntriesWithGeneName(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for fragmented entries");

        String searchTerm = "HLA-A";
        System.out.printf("Search for first %d protein entries that come from gene: %s%n", DISPLAY_ENTRY_SIZE,
                searchTerm);

        Query query = UniProtQueryBuilder.gene(searchTerm);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<Gene> genes = entry.getGenes();

                return genes.stream().map(gene -> gene.getGeneName().getValue()).collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForEntriesWithOrganismName(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for entries with organism name");

        String searchTerm = "Bacillus licheniformis";
        System.out.printf("Search for first %d protein entries that come from organism: %s%n", DISPLAY_ENTRY_SIZE,
                searchTerm);

        Query query = UniProtQueryBuilder.organismName(searchTerm);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                return Collections.singletonList(formatOrganismNames(entry.getOrganism()));
            }

            private String formatOrganismNames(Organism organism) {
                String organismText = organism.getScientificName().getValue();

                if (organism.hasCommonName()) {
                    organismText += " (" + organism.getCommonName().getValue() + ")";
                }

                if (organism.hasSynonym()) {
                    organismText += " (" + organism.getSynonym().getValue() + ")";
                }

                return organismText;
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForEntriesWithOrganismTaxonomy(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for entries with organism taxonomic identifier");

        int searchTerm = 9606;
        System.out.printf("Search for first %d protein entries that come from taxonomic id: %d%n", DISPLAY_ENTRY_SIZE,
                searchTerm);

        Query query = UniProtQueryBuilder.taxonID(searchTerm);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                String taxonId = entry.getNcbiTaxonomyIds().get(0).getValue();
                String organismName = entry.getOrganism().getScientificName().getValue();

                return Collections.singletonList(formatOuput(organismName, taxonId));
            }

            private String formatOuput(String organismName, String taxonId) {
                return "Organism: " + organismName + "; taxonId: " + taxonId;
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForEntriesWithLineage(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for entries with name in lineage");

        String searchTerm = "Homo";
        System.out.printf("Search for first %d protein entries that contain \"%s\" in their lineage%n",
                DISPLAY_ENTRY_SIZE, searchTerm);

        Query query = UniProtQueryBuilder.taxonName(searchTerm);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<NcbiTaxon> lineage = entry.getTaxonomy();
                return Collections.singletonList(formatLineage(lineage));
            }

            private String formatLineage(List<NcbiTaxon> lineage) {
                return lineage.stream().map(taxon -> taxon.getValue() + "; ").collect(Collectors.joining());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForEntriesWithTaxonIdInLineage(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search for entries with taxonomic identifier within its lineage");

        int searchTerm = 9605;
        System.out.printf("Search for first %d protein entries that contain the taxonomic ID %d in their lineage.%n",
                DISPLAY_ENTRY_SIZE, searchTerm);

        Query query = UniProtQueryBuilder.taxonID(searchTerm);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<NcbiTaxon> lineage = entry.getTaxonomy();
                return Collections.singletonList(formatLineage(lineage));
            }

            private String formatLineage(List<NcbiTaxon> lineage) {
                return lineage.stream().map(taxon -> taxon.getValue() + "; ").collect(Collectors.joining());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void searchForEntriesWithKeywords(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search by keyword");

        String keyword = "kinase";

        System.out.println("Searching for entries containing keyword: " + keyword);
        Query query = UniProtQueryBuilder.keyword(keyword);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<Keyword> keywords = entry.getKeywords();
                return keywords.stream().map(Keyword::getValue).collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    public static void searchForEntriesWithOrganelle(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Search by organelle");

        String organelle = "mitochondrion";

        System.out.println("Searching for entries containing organelle: " + organelle);
        Query query = UniProtQueryBuilder.keyword(organelle);

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                List<Organelle> organelles = entry.getOrganelles();
                return organelles.stream().map(Organelle::getValue).collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);

        printSearchResults(entries);
    }

    public static void orExample(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Query with OR");

        String ec1 = "3.1.6.-";
        String ec2 = "3.2.4.-";
        System.out.printf("Search for first %d protein entries that contain the EC numbers: %s OR %s%n",
                DISPLAY_ENTRY_SIZE, ec1, ec2);

        Query query = UniProtQueryBuilder.ec(ec1)
                .or(UniProtQueryBuilder.ec(ec2));

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                ProteinDescription description = entry.getProteinDescription();
                return extractEcNumbers(description);
            }

            private List<String> extractEcNumbers(ProteinDescription description) {
                Collection<Section> sections = aggregateSections(description);

                return sections.stream()
                        .map(Section::getNames).flatMap(List::stream)
                        .map(Name::getFields).flatMap(List::stream)
                        .filter(field -> field.getType() == FieldType.EC)
                        .map(Field::getValue)
                        .distinct()
                        .collect(Collectors.toList());
            }

            private Collection<Section> aggregateSections(ProteinDescription description) {
                Set<Section> sections = new HashSet<>();

                sections.add(description.getSection());
                sections.addAll(description.getIncludes());
                sections.addAll(description.getContains());

                return sections;
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    public static void andExample(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("Query with AND");

        String keyword = "Reference proteome";

        System.out.printf("Search for first %d protein entries that contain the keyword: %s AND is a " +
                "Swiss-Prot entry%n", DISPLAY_ENTRY_SIZE, keyword);

        Query query = UniProtQueryBuilder.keyword(keyword)
                .and(UniProtQueryBuilder.swissprot());

        SearchExecutor searchExecutor = new SearchExecutor(uniProtService) {
            @Override
            List<String> extractValues(UniProtEntry entry) {
                return entry.getKeywords().stream().map(Keyword::getValue).collect(Collectors.toList());
            }
        };

        Map<String, List<String>> entries = searchExecutor.executeSearch(query, DISPLAY_ENTRY_SIZE);
        printSearchResults(entries);
    }

    @SafeVarargs
    private static <T> Set<T> asSet(T... values) {
        return new LinkedHashSet<>(Arrays.asList(values));
    }

    private static Date convertToDate(LocalDate localDate) {
        Instant instant = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        return Date.from(instant);
    }

    private static String convertToString(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
        return DATE_FORMAT.format(localDate);
    }

    /**
     * Class that given a query executes the search, and returns the results as a Map
     */
    private abstract static class SearchExecutor {
        private static final int NO_LIMIT = Integer.MAX_VALUE;

        private final UniProtService uniProtService;
        private final EnumSet<QuerySpec> spec;

        public SearchExecutor(UniProtService uniProtService) {
            this(uniProtService, null);
        }


        public SearchExecutor(UniProtService uniProtService, EnumSet<QuerySpec> spec) {
            this.uniProtService = uniProtService;
            this.spec = spec;
        }

        Map<String, List<String>> executeSearch(Query query) throws ServiceException {
            return executeSearch(query, NO_LIMIT);
        }

        Map<String, List<String>> executeSearch(Query query, int limit) throws ServiceException {
            QueryResult<UniProtEntry> searchResult = uniProtService.getEntries(query, spec);

            Map<String, List<String>> entries = new HashMap<>();

            while (searchResult.hasNext() && limit > 0) {
                UniProtEntry entry = searchResult.next();
                String accession = entry.getPrimaryUniProtAccession().getValue();

                entries.put(accession, extractValues(entry));
                limit--;
            }

            return entries;
        }

        abstract List<String> extractValues(UniProtEntry entry);
    }
}
