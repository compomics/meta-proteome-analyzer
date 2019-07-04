package de.mpa.analysis;

import de.mpa.client.model.dbsearch.PeptideHit;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.net.URI;

public class UnipeptAnalyzer {

    private List<String> peptideSequences = new ArrayList<String>();

    private URI unipeptURI;

    public UnipeptAnalyzer(List<PeptideHit> peptideHits) {
        for (PeptideHit peptideHit : peptideHits) {
            peptideSequences.add(peptideHit.getSequence());
        }
        analyzeInUnipept(peptideSequences);
    }

    /**
     * Analyze the given list of peptides, using the default configuration settings.
     */
    public void analyzeInUnipept(List<String> peptides) {
        this.analyzeInUnipept(peptides, true, false, false);
    }

    /**
     * Analyze the given list of peptides, with the specified configuration, using the Unipept webinterface.
     *
     * @param peptides A list of peptides that should be analyzed using Unipept.
     * @param equateIl Should the amino acids I and L be equated or not? (True if they need to be considered as equals).
     * @param filterDuplicates Should duplicate peptides be filtered?
     * @param handleMissingCleavage Enable missing cleavage handling?
     */
    public void analyzeInUnipept(List<String> peptides, boolean equateIl, boolean filterDuplicates, boolean handleMissingCleavage) {
        String htmlBeforePeptides =
                "<html>" +
                        "   <body>" +
                        "       <form id=\"unipept-form\" action=\"https://unipept.ugent.be/mpa\" accept-charset=\"UTF-8\" method=\"post\">" +
                        "           <input name=\"utf8\" type=\"hidden\" value=\"✓\">" +
                        "            <textarea name=\"qs\" id=\"qs\" rows=\"7\" style=\"visibility: hidden;\">";

        String htmlAfterPeptides =
                "            </textarea>" +
                        "            <input type=\"text\" name=\"search_name\" id=\"search_name\" style=\"visibility: hidden;\">" +
                        "            <input type=\"checkbox\" name=\"il\" id=\"il\" value=\"1\" " + (equateIl ? "checked=\"checked\"" : "") + " style=\"visibility: hidden;\">" +
                        "            <input type=\"checkbox\" name=\"dupes\" id=\"dupes\"  value=\"1\" " + (filterDuplicates ? "checked=\"checked\"" : "") + " style=\"visibility: hidden;\">" +
                        "            <input type=\"checkbox\" name=\"missed\" id=\"missed\" value=\"1\" " + (handleMissingCleavage ? "checked=\"checked\"" : "") + " style=\"visibility: hidden;\">" +
                        "       </form>" +
                        "       <script>" +
                        "            window.onload = () => {" +
                        "                document.getElementById(\"unipept-form\").submit();" +
                        "            };" +
                        "        </script>" +
                        "   </body>" +
                        "</html>";

        // Write the list of peptides to the form.
        StringBuilder builder = new StringBuilder(htmlBeforePeptides);
        for (String peptide: peptides) {
            builder.append(peptide);
            builder.append("\n");
        }
        String tempHtml = builder.toString() + htmlAfterPeptides;

        // Write this HTML-form to a temporary file on the disk
        File temporaryFile = new File("mpa.html");
        try (FileWriter writer = new FileWriter(temporaryFile)) {
            writer.write(tempHtml);
            // Open the temporary file in default browser. This temporary file automatically forwards the browser to the
            // Unipept analysis page, with all required values automatically set and filled in.
            // Desktop.getDesktop().browse(temporaryFile.toURI());
            unipeptURI = temporaryFile.toURI();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors here, and display a message to the user if applicable
        }
    }

    public URI getUnipeptURI() {
        return unipeptURI;
    }
}
