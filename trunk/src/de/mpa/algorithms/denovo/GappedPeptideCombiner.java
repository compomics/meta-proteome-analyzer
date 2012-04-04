package de.mpa.algorithms.denovo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * This class combines various gapped peptides with each other.
 * @author N. Colaert, T. Muth
 *
 */
public class GappedPeptideCombiner {
	
	/**
	 * List combined peptides.
	 */
    private GappedPeptide combinedGappedPeptide;
    
    /**
     * The element count.
     */
    private int elementCount;
    
    /**
     * The fragement mass tolerance.
     */
    private double fragTol;
    
    /**
     * The list of gapped peptides.
     */
    private List<GappedPeptide> peptideList;
    
    /**
     * 
     * @param peptideList The list of peptides.
     * @param fragTol The fragment mass tolerance.
     */
    public GappedPeptideCombiner(List<GappedPeptide> peptideList, double fragTol){
    	this.fragTol = fragTol;
    	this.peptideList = peptideList;
		if (peptideList.size() > 0) {
			combinePeptides();
		}
    }
    
    /**
     * This method combines the peptides.
     */
    private void combinePeptides(){
    	// List of gapped peptide elements.
        List<GappedPeptideElement> gappedPepElements = new ArrayList<GappedPeptideElement>();
        int offset;
     
        // Get the (n/2+1) offset.
        if(peptideList.size() % 2 == 0){
            offset = peptideList.size() / 2 + 1;
        } else {
            offset = (peptideList.size() + 1) / 2;
        }

        // Cycle through the half + 1 of the peptides since we only need to find tags that are
        // present in half + 1 of the peptides and not less
		for (int i = 0; i < offset; i++) {
            GappedPeptide gappedPeptide = peptideList.get(i);

            // Check every tag, and check if we can find it in half + 1 of the gapped peptides
            Vector<Boolean> lTagNotInCombinationVector = gappedPeptide.getTagNotInCombinationVector();
            Vector<Double> lMassVector = gappedPeptide.getMassVector();
            Vector<Double> lMassSumVector = gappedPeptide.getMassSumVector();
            for(int t = 0; t<lTagNotInCombinationVector.size(); t ++){
                // Only if it's a tag
                if(lTagNotInCombinationVector.get(t)){
                    lTagNotInCombinationVector.set(t,false);
                    
                    // Check if we can find this tag in the other gapped peptides
                    double tagCount = 1.0;
                    double startMass = 0.0;
                    double endMass = 0.0;
                    if(t != 0){
                        startMass = startMass + lMassSumVector.get(t-1);
                    }
                    if(t != lTagNotInCombinationVector.size() - 1){
                        endMass = endMass + lMassSumVector.get(t + 1);
                    }

					for (int g = 0; g < peptideList.size(); g++) {
                        if(i != g){
                            GappedPeptide lGappedMatch = peptideList.get(g);
                            Vector<Boolean> lTagNotInCombinationMatchVector = lGappedMatch.getTagNotInCombinationVector();
                            Vector<Double> lMassMatchVector = lGappedMatch.getMassVector();
                            Vector<Double> lMassMatchSumVector = lGappedMatch.getMassSumVector();
                            for(int s = 0; s<lTagNotInCombinationMatchVector.size(); s ++){
                                if(lTagNotInCombinationMatchVector.get(s)){
                                    //check if the element mass is exactly the same
                                    if(lMassVector.get(t).equals(lMassMatchVector.get(s))){
                                        //check the start
                                        if(Math.abs(lMassSumVector.get(t) - lMassMatchSumVector.get(s)) <= 2.0 * fragTol){
                                            //we found one: the mass difference is the same and they start at the correct summed mass
                                            tagCount = tagCount + 1.0;
                                            //set that we already used this tag
                                            lTagNotInCombinationMatchVector.set(s,false);
                                            if(s != 0){
                                                startMass = startMass + lMassMatchSumVector.get(s-1);
                                            }
                                            if(s != lTagNotInCombinationMatchVector.size() - 1){
                                                endMass = endMass + lMassMatchSumVector.get(s + 1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(tagCount >= offset){
                        // We found this tag in the half + 1 cases
                        GappedPeptideElement lEle = new GappedPeptideElement(startMass/tagCount, endMass/tagCount, lMassVector.get(t),gappedPeptide.getTagSequenceVector().get(t));
                        gappedPepElements.add(lEle);
                    }
                }
            }
        }
		
        double totalMass = 0.0;
		for (int i = 0; i < peptideList.size(); i++) {
            totalMass = totalMass + peptideList.get(i).getTotalMass();
        }
        totalMass = totalMass/Double.valueOf(String.valueOf(peptideList.size()) + ".0");
        Collections.sort(gappedPepElements, new GappedPeptideElementSorter());
        String gappedResult = "";
        for(int i = 0; i<gappedPepElements.size(); i++){
            GappedPeptideElement element = gappedPepElements.get(i);
            if(i == 0 && element.getStartMass() != 0.0){
                gappedResult = gappedResult + "<" + element.getStartMass() + ">," + element.getElement() + ",";
            } else {
                if(i == 0){
                    gappedResult = gappedResult + element.getElement() + ",";
                } else {
                    //check if there is a difference with the previous
                    GappedPeptideElement previousElement = gappedPepElements.get(i - 1);
                    double massDiff = Math.abs(element.getStartMass() - previousElement.getStartMass()  - previousElement.getMassDifference() );
                    if(massDiff > 1.0){
                        //System.out.println(lMassDifference);
                        gappedResult = gappedResult + "<" + massDiff + ">," + element.getElement() + ",";
                    } else {
                        gappedResult = gappedResult + element.getElement() + ",";
                    }
                }
            }
            if(i == gappedPepElements.size() - 1){
                if(element.iEndMass != 0.0){
                    double mass = totalMass - element.getStartMass() - element.getMassDifference();
                    gappedResult = gappedResult + "<" + mass + ">";
                }
            }
        }
        if(gappedResult.length() == 0){
            gappedResult = "<" + totalMass + ">";
        }
        combinedGappedPeptide = new GappedPeptide(gappedResult);
        elementCount = gappedPepElements.size();
    }
    
    /**
     * Returns the element count.
     * @return
     */
    public int getElementCount() {
        return elementCount;
    }
    
    /**
     * Returns the combined gapped peptide element.
     * @return
     */
    public GappedPeptide getCombinedGappedPeptide() {
        return combinedGappedPeptide;
    }


    class GappedPeptideElementSorter implements Comparator<GappedPeptideElement> {
        public int compare(GappedPeptideElement o1, GappedPeptideElement o2) {
            if(o1.getStartMass() > o2.getStartMass()){
                return 1;
            }
            if(o1.getStartMass() < o2.getStartMass()){
                return -1;
            }
            return 0;
        }
    }
}
