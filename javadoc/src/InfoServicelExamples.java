package uk.ac.ebi.uniprot.dataservice.client.examples;

import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.info.InfoService;
import uk.ac.ebi.uniprot.dataservice.domain.info.ServiceInfoObject;
import uk.ac.ebi.uniprot.dataservice.domain.info.UniParcServiceInfoObject;
import uk.ac.ebi.uniprot.dataservice.domain.info.UniProtServiceInfoObject;
import uk.ac.ebi.uniprot.dataservice.domain.info.UniRefServiceInfoObject;

import java.util.Optional;

public class InfoServicelExamples {
    /**
     * Indicates the number of entry results that should be retrieved from the
     * search
     */
    private static final int DISPLAY_ENTRY_SIZE = 10;

    public static void main(String[] args) throws ServiceException {
        ServiceFactory serviceFactoryInstance = Client
                .getServiceFactoryInstance();
        InfoService infoService = serviceFactoryInstance.getInfoService();

        Optional<ServiceInfoObject> serviceInfo = infoService.getServiceInfo();
        if (serviceInfo.isPresent()) {
            ServiceInfoObject serviceInfoObject = serviceInfo.get();
            UniProtServiceInfoObject uniProtServiceInfoObject =
                    serviceInfoObject.getUniProtServiceInfoObject();

            System.out.printf("UniProt Service Info:\nRelease: %s\nIsoform: %s\nSiwssprot: %s\nTrembl: %s\n",
                    uniProtServiceInfoObject.getReleaseNumber(), uniProtServiceInfoObject.getIsoformEntries(),
                    uniProtServiceInfoObject.getSwissProtEntries(), uniProtServiceInfoObject.getTremblEntries());

            UniParcServiceInfoObject uniParcServiceInfoObject = serviceInfoObject.getUniParcServiceInfoObject();

            System.out.printf("UniParc Service Info:\nUPIs: %s\nXrefs: %s\n",
                    uniParcServiceInfoObject.getUPIs(), uniParcServiceInfoObject.getXrefs()
            );

            UniRefServiceInfoObject uniRefServiceInfoObject = serviceInfoObject.getUniRefServiceInfoObject();

            System.out.printf("UniRef Service Info:\nUniRef50: %d\nUniRef90: %d\nUniRef100: %d\n",
                    uniRefServiceInfoObject.getUniref50(), uniRefServiceInfoObject.getUniref90(), uniRefServiceInfoObject.getUniref100());
        } else {
            System.out.println("ERROR: cannot locate ServiceInfoObject");
        }
    }

}
