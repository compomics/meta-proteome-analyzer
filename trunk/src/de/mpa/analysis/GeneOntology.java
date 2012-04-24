package de.mpa.analysis;

//public class GeneOntology extends TreeMap<String, List<Entry>> {
//
//    public GeneOntology(GeneAnnotationFile geneAnnotationFile) {
//        super();
//        while(geneAnnotationFile.hasNext()) {
//            Entry geneAnnotation = geneAnnotationFile.next();
//            
//            // check if this uni prot (dbObjectId) is already in the map
//            String uniProt = geneAnnotation.dbObjectId;
//            List<Entry> existingAnnotations = get(uniProt);
//            
//            // if not, add it
//            if(existingAnnotations == null) {
//                existingAnnotations = new ArrayList<Entry>();
//                put(uniProt, existingAnnotations);
//            }
//            
//            // add this annotation to the list
//            existingAnnotations.add(geneAnnotation);
//        }
//    }
//
//}

