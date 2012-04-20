package de.mpa.algorithms.denovo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import de.mpa.analysis.Masses;
import de.mpa.util.Formatter;

/**
 * This class represents a gapped peptide object.
 * @author N. Colaert, T. Muth
 *
 */
public class GappedPeptide {

    private HashMap<String,Double> iMasses = new HashMap<String,Double>();
    private double iSum;
    private Vector<Double> iMassVector;
    private Vector<Double> iMassSumVector;
    private Vector<Boolean> iTagVector;
    private Vector<String> iTagSequenceVector;
    private Vector<Boolean> iTagNotInCombinationUsedVector;

    public GappedPeptide(String gappedPeptide){
        this.calculateMasses();
        gappedPeptide = gappedPeptide.replace("C<Pyr,", "C<Pyr|");
        String[] elements = gappedPeptide.split(",");
        iSum = 0.0;
        iMassVector = new Vector<Double>();
        iTagSequenceVector = new Vector<String>();
        iMassSumVector = new Vector<Double>();
        iTagVector = new Vector<Boolean>();
        iTagNotInCombinationUsedVector = new Vector<Boolean>();
        boolean previousTag = true;

        for(int i = 0; i<elements.length; i ++){
            String element = elements[i];
            element = element.replace("C<Pyr|", "C<Pyr,");
            element = element.replace("#Gln->pyro-Glu (N-term Q)#-Q", "NH2-Q<Pyr>");
            element = element.replace("#Glu->pyro-Glu (N-term E)#-E", "NH2-E<Pyr>");
            element = element.replace("#Pyro-carbamidomethyl (N-term C)#-C", "NH2-C<Pyr,Cmm>");

            try{
                double lMass = 0.0;
                if(element.startsWith("<")){
                    if(previousTag){
                        lMass = Double.valueOf(element.substring(1,element.indexOf(">")));
                        iMassVector.add(lMass);
                        iTagSequenceVector.add(null);
                        iTagVector.add(false);
                        iTagNotInCombinationUsedVector.add(false);
                        if(iMassSumVector.size() == 0){
                            iMassSumVector.add(lMass);
                        } else {
                            iMassSumVector.add(lMass + iMassSumVector.get(iMassSumVector.size() - 1));
                        }
                    } else {
                        iMassVector.set(iMassVector.size()-1, iMassVector.get(iMassVector.size()-1) + iMasses.get(element));
                        iMassSumVector.set(iMassSumVector.size()-1, iMassSumVector.get(iMassSumVector.size()-1) + iMasses.get(element));
                    }
                    previousTag = false;
                } else {
                    if(iMasses.get(element) == null){
                        System.out.println(element + "   " + gappedPeptide);
                    }
                    lMass = iMasses.get(element);
                    iMassVector.add(iMasses.get(element));
                    iTagSequenceVector.add(element);
                    if(iMassSumVector.size() == 0){
                        iMassSumVector.add(iMasses.get(element));
                    } else {
                        iMassSumVector.add(iMasses.get(element) + iMassSumVector.get(iMassSumVector.size() - 1));
                    }
                    previousTag = true;
                    iTagVector.add(true);
                    iTagNotInCombinationUsedVector.add(true);
                }
                iSum =  iSum + lMass;
            } catch(StringIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
    }

    public int getTagCount(){
        int count = 0;
        for(int i = 0; i < iTagVector.size(); i++){
            if(iTagVector.get(i)){
                count++;
            }
        }
        return count;
    }
    
    public double getSum() {
        return iSum;
    }

    public Vector<Double> getMassVector() {
        return iMassVector;
    }

    public Vector<Boolean> getTagNotInCombinationVector() {
        return iTagNotInCombinationUsedVector;
    }

    public Vector<Double> getMassSumVector() {
        return iMassSumVector;
    }

    public Vector<Boolean> getTagVector() {
        return iTagVector;
    }

    private void calculateMasses() {
        iMasses = MassesMap.getInstance().getMasses();
    }
    
    /**
	 * Splits the sequence into comma separated parts...
	 * @param sequence
	 * @return String The splitted sequence.
	 */
	public static String splitSequence(String sequence){
		String newSequence = "";
		for (int i = 0; i < sequence.length() - 1; i++){			
			if(i+4 < sequence.length()&& sequence.substring(i, i+4).equals("M+16")){
				newSequence += sequence.substring(i, i+4) + ",";
				i = i+3;
			} else {
				newSequence += sequence.substring(i, i+1) + ",";
			}			
		}
		newSequence += sequence.charAt(sequence.length()-1);
        return newSequence;
    }	
	
	/**
	 * Returns the gapped sequence of the peptide.
	 * @return The gapped sequence string of the peptide.
	 */
	public String getGappedSequence(){
		String sequence = "";
        for(int i = 0; i<iTagVector.size(); i ++){
            if(iTagVector.get(i)){
                sequence = sequence + iTagSequenceVector.get(i);
            } else {
                sequence = sequence + "<" + Formatter.roundDouble(iMassVector.get(i), 3) + ">";
            }
        }
        return sequence;
	}
	
	/**
	 * Returns the formatted sequence, i.e. the gaps are replaced by amino acid suggestions.
	 * @return
	 */
	public String getFormattedSequence() {
		String format = "";
        for(int i = 0; i<iTagVector.size(); i ++){
            if(iTagVector.get(i)){
                format += iTagSequenceVector.get(i);
            } else {
            	double mass = iMassVector.get(i);
            	if(getDiPeptide(mass).length() > 0){
            		format += getDiPeptide(mass);
            	} else {
            		format += "<" +  Formatter.roundDouble(mass, 3) + ">";
            	}
            }
        }
        return format;
	}
	
	public String getDiPeptide(double mass){
		String seq = "";
		Map<String, Double> masses = Masses.getInstance();
		Map<Double, String> dipeptides = DiPeptideMap.getInstance();
		Set<Double> dipeptideMasses = dipeptides.keySet();
		Set<Entry<String, Double>> entrySet = masses.entrySet();
		
		for (Entry<String, Double> entry : entrySet) {
			double delta = Math.abs(mass - entry.getValue());
			
			// Check for single amino acid mapping.
			if(delta <= 0.5) {
				seq = "[" + entry.getKey();
			}
		}	
		// Check for di-peptides.
		for (Double mass2 : dipeptideMasses) {
				double delta2 = Math.abs(mass - mass2);
				if(seq.length() > 0){					
					if(delta2 <= 0.5){
						seq += "|" + dipeptides.get(mass2); // + "|" + Formatter.reverseString(dipeptides.get(mass2));
					}
				} else {
					if(delta2 <= 0.5) {
						seq = "[" + dipeptides.get(mass2); // + "|" + Formatter.reverseString(dipeptides.get(mass2));
				}
			}
		}
		
		if(seq.length() > 0) seq += "]";
		
		return seq;
	}
	
    public String toString(){
    	return getGappedSequence();
    }
    
    public Vector<String> getTagSequenceVector() {
        return iTagSequenceVector;
    }

    public double getTotalMass() {
        return Math.round(iSum*1000.0)/1000.0;
    }

    public HashMap getMasses() {
        return iMasses;
    }

    /**
     * Internal modification class.
     * @author T. Muth
     *
     */
    class Modification{
        /**
         * The element
         */
        private String iName;
        private String iDescription;
        private String iConversion;
        /**
         * The mass
         */
        private double iMass;

        private boolean iNterm = false;
        private boolean iCterm = false;
        private boolean iModifiesEverything = false;
        private Vector<String> iAminoacids = new Vector<String>();


        /**
         * Constructor for modification.
         *
         * @param aElement The elements
         * @param aMw      The mass
         */
        public Modification(String aElement, double aMw) {
            this.iDescription = aElement;
            if(aElement.indexOf("N-term")>0){
                iNterm = true;
            }
            if(aElement.indexOf("C-term")>0){
                iCterm = true;
            }
            this.iName = aElement.substring(0, aElement.indexOf("(") - 1);
            this.iConversion = iName;
            this.iMass = aMw;
            String lTemp = aElement.substring(aElement.indexOf("(") + 1, aElement.indexOf(")"));
            if(iNterm){
                lTemp = lTemp.substring(6);
            }
            if(iCterm){
                lTemp = lTemp.substring(6);
            }
            if(lTemp.length()==0){
                iModifiesEverything = true;
            }else{
                for(int i = 0; i<lTemp.length(); i ++){
                    iAminoacids.add(String.valueOf(lTemp.charAt(i)));
                }
            }
        }

        /**
         * Getter for the element
         *
         * @return String with the element
         */
        public String getName() {
            return this.iName;
        }

        /**
         * Getter for the mass
         *
         * @return double with the mass
         */
        public double getMw() {
            return this.iMass;
        }

        /**
         * To string method
         *
         * @return String
         */
        public String toString() {
            String returnString = iName + " (" + iConversion + ")  " + iMass;
            return returnString;
        }

        public void setConversion(String lConversion) {
            this.iConversion = lConversion;
        }

        public boolean modifies(String element) {
            if(iModifiesEverything){
                return true;
            } else {
                boolean lModifies = false;
                for(int i = 0; i<iAminoacids.size(); i ++){
                    if(iAminoacids.get(i).equalsIgnoreCase(element)){
                        lModifies = true;
                    }
                }
                return lModifies;
            }
        }

        public String getConversion() {
            return iConversion;
        }

        public boolean isNterm() {
            return iNterm;
        }

        public boolean isCterm() {
            return iCterm;
        }

        public boolean isInternal() {
            if(!iNterm && !iCterm){
                return true;
            }
            return false;
        }

        public Vector<String> getAminoAcids() {
            return iAminoacids;
        }

        public String getDescription() {
            return iDescription;
        }
    }

}

