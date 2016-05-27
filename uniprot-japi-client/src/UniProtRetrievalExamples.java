package uk.ac.ebi.uniprot.dataservice.client.examples;

import uk.ac.ebi.kraken.interfaces.uniprot.*;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Comment;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureLocation;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtComponent;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtData;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtData.ComponentType;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.List;

/**
 * This example shows how to create and use a basic UniProt {@link uk.ac.ebi.uniprot.dataservice.query.Query} with a
 * {@link uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService} and subsequently retrieve various parts of the
 * entries that satisfy the query.
 */
public final class UniProtRetrievalExamples {

    public static void main(String[] args) {
        driveExamples();
    }

    public static void driveExamples() {
        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniProtService uniProtService = serviceFactoryInstance.getUniProtQueryService();
        try {
            // start the service
            uniProtService.start();

            // the accession we're interested in
            String accession = "P10415";

            // use the service directly to fetch the UniProtEntry
            accessSingleFullUniProtEntry(uniProtService, accession);

            // create a query that will satisfy the 1 document result,
            // i.e., the one for entry P10144
            Query query = UniProtQueryBuilder.accession(accession);

            // use the service with the query to entries
            accessMultiFullUniProtEntry(uniProtService, query);

            // use the service with the query to access only its comments
            accessCommentsOnly(uniProtService, query);

            // use the service with the query to access only its features
            accessFeaturesOnly(uniProtService, query);

            // use the service with the query to access only its protein names
            accessProteinNamesOnly(uniProtService, query);

            // use the service with the query to access only its EC numbers
            accessECsOnly(uniProtService, query);

            // use the service with the query to access only its genes
            accessGenesOnly(uniProtService, query);

            // use the service with the query to access only its database cross-references
            accessXrefsOnly(uniProtService, query);

            // use the service with the query to access results with all components
            accessResults(uniProtService, query);

            // use the service simply find out numbers of hits of a query
            showResultHits(uniProtService);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // always remember to stop the service
            uniProtService.stop();
            System.out.println("service now stopped.");
        }
    }

    public static void accessXrefsOnly(UniProtService uniProtService, Query query) throws ServiceException {
        printExampleHeader("xrefs");
        QueryResult<UniProtComponent<DatabaseCrossReference>> xrefComponents = uniProtService.getXrefs(query);
        while (xrefComponents.hasNext()) {
            UniProtComponent<DatabaseCrossReference> xrefComponent = xrefComponents.next();
            if (!xrefComponent.getComponent().isEmpty()) {
                System.out.println("accession: " + xrefComponent.getAccession().getValue());
                for (DatabaseCrossReference dbx : xrefComponent.getComponent()) {
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

                    System.out.println("  " + dbxResultSB);
                }
            }
        }
    }

    public static void accessGenesOnly(UniProtService uniProtService, Query query) throws ServiceException {
        printExampleHeader("genes");
        QueryResult<UniProtComponent<Gene>> geneComponents = uniProtService.getGenes(query);
        while (geneComponents.hasNext()) {
            UniProtComponent<Gene> geneComponent = geneComponents.next();
            if (!geneComponent.getComponent().isEmpty()) {
                System.out.println("accession: " + geneComponent.getAccession().getValue());
                for (Gene gene : geneComponent.getComponent()) {
                    System.out.println("  " + gene.getGeneName());
                }
            }
        }
    }

    public static void accessECsOnly(UniProtService uniProtService, Query query) throws ServiceException {
        printExampleHeader("ec numbers");
        QueryResult<UniProtComponent<String>> ecComponents = uniProtService.getECNumbers(query);
        while (ecComponents.hasNext()) {
            UniProtComponent<String> ecComponent = ecComponents.next();
            if (!ecComponent.getComponent().isEmpty()) {
                System.out.println("accession: " + ecComponent.getAccession().getValue());
                for (String ec : ecComponent.getComponent()) {
                    System.out.println("  " + ec);
                }
            }
        }
    }

    public static void accessProteinNamesOnly(UniProtService uniProtService, Query query) throws ServiceException {
        printExampleHeader("protein names");
        QueryResult<UniProtComponent<String>> proteinComponents = uniProtService.getProteinNames(query);
        int count = 0;
        while (proteinComponents.hasNext()) {
            count++;
            UniProtComponent<String> proteinComponent = proteinComponents.next();
            if (!proteinComponent.getComponent().isEmpty()) {
                System.out.println("accession: " + proteinComponent.getAccession().getValue());
                for (String protein : proteinComponent.getComponent()) {
                    System.out.println("  " + protein);
                }
            }
        }
        System.out.println("number of hits = " + count);
    }

    public static void accessFeaturesOnly(UniProtService uniProtService, Query query) throws ServiceException {
        printExampleHeader("features");
        QueryResult<UniProtComponent<Feature>> featureComponents = uniProtService.getFeatures(query);
        while (featureComponents.hasNext()) {
            UniProtComponent<Feature> featureComponent = featureComponents.next();
            if (!featureComponent.getComponent().isEmpty()) {
                System.out.println("accession: " + featureComponent.getAccession().getValue());

                for (Feature feature : featureComponent.getComponent()) {
                    StringBuilder featureSB = new StringBuilder();
                    FeatureLocation featureLocation = feature.getFeatureLocation();
                    featureSB.append(feature.getType().getName())
                            .append(", ")
                            .append(featureLocation.getStartModifier().toString())
                            .append(featureLocation.getStart())
                            .append(", ")
                            .append(featureLocation.getEnd())
                            .append(featureLocation.getEndModifier());

                    System.out.println("  " + featureSB.toString());
                }
            }
        }
    }

    public static void accessCommentsOnly(UniProtService uniProtService, Query query) throws ServiceException {
        printExampleHeader("comments");
        QueryResult<UniProtComponent<Comment>> commentComponents = uniProtService.getComments(query);
        int count = 0;
        while (commentComponents.hasNext()) {
            count++;
            UniProtComponent<Comment> commentComponent = commentComponents.next();
            if (!commentComponent.getComponent().isEmpty()) {
                System.out.println("accession: " + commentComponent.getAccession().getValue());
                for (Comment comment : commentComponent.getComponent()) {
                    System.out.println("   " + "CommentType = " + comment.getCommentType()
                            .toDisplayName() + ", " + "CommentStatus = " + comment.getCommentStatus().getValue());
                }
            }
        }
        System.out.println("number of hits = " + count);
    }

    public static void accessSingleFullUniProtEntry(UniProtService uniProtService, String accession) throws ServiceException {
        UniProtEntry entry = uniProtService.getEntry(accession);
        if (entry == null) {
            System.out.println("Entry " + accession + " could not be retrieved");
        } else {
            System.out.println("Retrieved UniProtEntry object");
            System.out.println(entry.getUniProtId().getValue());
        }
    }

    public static void accessMultiFullUniProtEntry(UniProtService uniProtService, Query query) throws ServiceException {
        QueryResult<UniProtEntry> entries = uniProtService.getEntries(query);
        printExampleHeader("full entry");
        int count = 0;
        while (entries.hasNext()) {
            count++;
            UniProtEntry entry = entries.next();
            System.out.println(entry.getUniProtId().getValue());
        }
        System.out.println("retrieved entries = " + count);

    }

    public static void accessResults(UniProtService uniProtService, Query query) throws ServiceException {
        printExampleHeader("accessResults (choose which fields you want to see)");
        QueryResult<UniProtData> results = uniProtService
                .getResults(query, ComponentType.COMMENTS, ComponentType.FEATURES, ComponentType.GENES);
        System.out.println("   number of hits = " + results.getNumberOfHits());
        while (results.hasNext()) {
            UniProtData data = results.next();
            System.out.println("   Accession: " + data.getAccession().getValue());
            System.out.println("   UniProtId: " + data.getUniProtId().getValue());
            for (ComponentType type : ComponentType.values()) {
                if (data.hasComponent(type)) {
                    switch (type) {
                        case COMMENTS:
                            printExampleHeader("   ", " comments");
                            List<Comment> comments = data.getComponent(ComponentType.COMMENTS);
                            for (Comment comment : comments) {
                                System.out.println("   " + "CommentType = " + comment.getCommentType()
                                        .toDisplayName() + ", " + "CommentStatus = " + comment.getCommentStatus().getValue());
                            }
                            break;
                        case FEATURES:
                            printExampleHeader("   ", "features");
                            List<Feature> features = data.getComponent(type);
                            for (Feature feature : features) {
                                StringBuilder featureSB = new StringBuilder();
                                FeatureLocation featureLocation = feature.getFeatureLocation();
                                featureSB.append(feature.getType().getName())
                                        .append(", ")
                                        .append(featureLocation.getStartModifier().toString())
                                        .append(featureLocation.getStart())
                                        .append(", ")
                                        .append(featureLocation.getEnd())
                                        .append(featureLocation.getEndModifier());

                                System.out.println("     " + featureSB.toString());
                            }
                            break;
                        case GENES:
                            printExampleHeader("   ", "genes");
                            List<Gene> genes = data.getComponent(type);
                            for (Gene gene : genes) {
                                System.out.println("     " + gene.getGeneName());
                            }
                            break;
                        case XREFS:
                            printExampleHeader("   ", "xrefs");
                            List<DatabaseCrossReference> xrefs = data.getComponent(type);
                            for (DatabaseCrossReference xref : xrefs) {
                                System.out.println("     " + xref.toString());
                            }
                            break;
                        case ECNUMBER:
                            printExampleHeader("   ", "ec numbers");
                            List<String> ecs = data.getComponent(type);
                            for (String ec : ecs) {
                                System.out.println("     " + ec);
                            }
                            break;
                        case PROTEIN_NAMES:
                            printExampleHeader("   ", "protein name");
                            List<String> proteins = data.getComponent(type);
                            for (String protein : proteins) {
                                System.out.println("     " + protein);
                            }

                            break;
                        case KEYWORDS:
                            printExampleHeader("   ", "keywords");
                            List<Keyword> kws = data.getComponent(type);
                            for (Keyword kw : kws) {
                                System.out.println("     " + kw.getValue());
                            }
                            break;
                        case TAXONOMY:
                            printExampleHeader("   ", "taxonomy");
                            List<NcbiTaxon> taxs = data.getComponent(type);
                            for (NcbiTaxon tax : taxs) {
                                System.out.println("     " + tax.getValue());
                            }
                            break;
                    }
                }
            }
        }

    }

    public static void showResultHits(UniProtService uniProtService) throws ServiceException {
        printExampleHeader("showResultHits");

        // query swissprot
        Query query1 = UniProtQueryBuilder.ec("3.1.3")  // we want EC number 3.1.6.-
                .and(UniProtQueryBuilder.swissprot());  // we want SwissProt entries

        QueryResult<PrimaryUniProtAccession> accessionQueryResult1 = uniProtService.getAccessions(query1);
        System.out
                .println("   inside SwissProt, the query, " + query1.getQueryString() + " has " + accessionQueryResult1
                        .getNumberOfHits() + " matches" + ".");

        // query trembl
        Query query2 = UniProtQueryBuilder.ec("3.1.3")  // we want EC number 3.1.6.-
                .and(UniProtQueryBuilder.trembl());  // we want TrEMBL entries
        QueryResult<PrimaryUniProtAccession> accessionQueryResult2 = uniProtService.getAccessions(query2);
        System.out.println("   inside TrEMBL, the query, " + query2.getQueryString() + " has " + accessionQueryResult2
                .getNumberOfHits() + " matches" + ".");
    }

    public static void printExampleHeader(String headerTitle) {
        printExampleHeader("", headerTitle);
    }

    public static void printExampleHeader(String prefix, String headerTitle) {
        System.out.printf("%s=========== %s ==========%n", prefix, headerTitle);
    }

}
