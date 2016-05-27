package uk.ac.ebi.uniprot.dataservice.client.examples;

import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.alignment.blast.*;
import uk.ac.ebi.uniprot.dataservice.client.alignment.blast.input.DatabaseOption;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a simple example of submitting blast jobs through the UniProtJAPI.
 */
public class BlastDemo {
    private static final Logger logger = LoggerFactory.getLogger(BlastDemo.class);

    public static void main(String[] args) {
        runUniProtBlastExample();
        runUniParcBlastExample();
        runUniRefBlastExample();
    }

    public static void runUniProtBlastExample() {
        logger.info("Start UniProt blast");

        // Query String
        String querySequence = "MES00005665499\n" +
                "MSNHGFAYFFTSYQSLSLDSSSPPPSPHPRAHASSRFPPRARAVASFHTSCKMARTKQTA\n" +
                "RKSTGGKAPRKQLATKAARKSAPATGGVKKPHRYRPGTVALREIRKYQKSTELLIRKLPF\n" +
                "QRLVREIAQDFKTDLRFQSSAVLALQEASEAYLVGLFEDTNLCAIHAKRVTIMPKDVQLA\n" +
                "RRIRGERA";

        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniProtBlastService uniProtBlastService = serviceFactoryInstance.getUniProtBlastService();
        uniProtBlastService.start();

        BlastInput input = new BlastInput.Builder(DatabaseOption.SWISSPROT, querySequence).build();

        CompletableFuture<BlastResult<UniProtHit>> resultFuture = uniProtBlastService.runBlast(input);

        try {
            BlastResult<UniProtHit> blastResult = resultFuture.get();
            logger.info("Number of blast hits: " + blastResult.getNumberOfHits());

            for (UniProtHit hit : blastResult.hits()) {
                System.out.println(hit.getSummary().getEntryAc() + "\t" +
                        hit.getEntry().getPrimaryUniProtAccession().getValue());
            }
        } catch (ExecutionException e) {
            logger.error(e.getCause().getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            uniProtBlastService.stop();
        }

        logger.info("Finished UniProt blast");
    }

    public static void runUniParcBlastExample() {
        logger.info("Start UniParc blast");

        // Query String
        String querySequence = "MGAAASIQTTVNTLSERISSKLEQEANASAQTKCDIEIGNFYIRQNHGCNLTVKNMCSAD\n" +
                "ADAQLDAVLSAATETYSGLTPEQKAYVPAMFTAALNIQTSVNTVVRDFENYVKQTCNSSA\n" +
                "VVDNKLKIQNVIIDECYGAPGSPTNLEFINTGSSKGNCAIKALMQLTTKATTQIAPKQVA\n" +
                "GTGVQFYMIVIGVIILAALFMYYAKRMLFTSTNDKIKLILANKENVHWTTYMDTFFRTSP\n" +
                "MVIATTDMQN";

        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniParcBlastService uniParcBlastService = serviceFactoryInstance.getUniParcBlastService();
        uniParcBlastService.start();

        BlastInput input = new BlastInput.Builder(DatabaseOption.UNIPARC, querySequence).build();

        CompletableFuture<BlastResult<UniParcHit>> resultFuture = uniParcBlastService.runBlast(input);

        try {
            BlastResult<UniParcHit> blastResult = resultFuture.get();
            logger.info("Number of blast hits: " + blastResult.getNumberOfHits());

            for (UniParcHit hit : blastResult.hits()) {
                System.out.println(hit.getSummary().getEntryId() + "\t" +
                        hit.getEntry().getUniParcId().getValue());
            }
        } catch (ExecutionException e) {
            logger.error(e.getCause().getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            uniParcBlastService.stop();
        }

        logger.info("Finished UniParc blast");
    }

    public static void runUniRefBlastExample() {
        logger.info("Start UniRef blast");

        // Query String
        String querySequence = "MLRGSARTYWTLTGLWVLLRAGTLVVGLLFQRLFDALGAGGGVWLIIALVAAIEAGRLFL\n" +
                "QFGVMINRLEPRVQYGTTARLRHALLGSALRGSEVTARTSPGESLRTVGEDVDETGFFVA\n" +
                "WAPTNLAHWLFVAASVTVMMRIDAVVTGALLALLVLLTLVTALAHSRFLRHRRATRAASG\n" +
                "EVAGALREMVGAVGAVQAAAAEPQVAAHVAGLNGARAEAAVREELYAVVQRTVIGNPAPI\n" +
                "GVGVVLLLVAGRMDEGTFSVGDLALFAFYLQILTEALGSIGMLSVRLQRVSVALGRITNN\n" +
                "LGCRLRRSLERASPPIASDAPGGTGEGAAAPDAGPEPAPPLRELAVRGLTARHPGAGHGI\n" +
                "EDVDLVVERHTVTVVTGRVGSGKSTLVRAVLGLLPHERGTVLWNGEPIADPASFLVAPRC\n" +
                "GYTPQVPCLFSGTVRENVLLGRDGAAFDEAVRLAVAEPDLAAMQDGPDTVVGPRGLRLSG\n" +
                "GQIQRVAIARMLVGDPELVVLDDVSSALDPETEHLLWERLLDGTRTVLAVSHRPALLRAA\n" +
                "DRVVVLEGGRVEASGTFEEVMAVSAEMGRIWTGAGPGGGDAGPAPQSPPAG";

        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        UniRefBlastService uniRefBlastService = serviceFactoryInstance.getUniRefBlastService();
        uniRefBlastService.start();

        BlastInput input = new BlastInput.Builder(DatabaseOption.UNIREF_90, querySequence).build();

        CompletableFuture<BlastResult<UniRefHit>> resultFuture = uniRefBlastService.runBlast(input);

        try {
            BlastResult<UniRefHit> blastResult = resultFuture.get();
            logger.info("Number of blast hits: " + blastResult.getNumberOfHits());

            for (UniRefHit hit : blastResult.hits()) {
                System.out.println(hit.getSummary().getEntryId() + "\t" +
                        hit.getEntry().getUniRefEntryId().getValue());
            }
        } catch (ExecutionException e) {
            logger.error(e.getCause().getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            uniRefBlastService.stop();
        }

        logger.info("Finished UniRef blast");
    }
}