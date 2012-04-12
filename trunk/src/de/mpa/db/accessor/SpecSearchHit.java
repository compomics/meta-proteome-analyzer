package de.mpa.db.accessor;

import de.mpa.client.model.specsim.SpectrumSpectrumMatch;

public class SpecSearchHit extends SpecsearchhitTableAccessor {
	
	public SpecSearchHit(SpectrumSpectrumMatch ssm) {
		iFk_searchspectrumid = ssm.getSearchspectrumID();
		iFk_libspectrumid = ssm.getLibspectrumID();
		iSimilarity = ssm.getSimilarity();
	}

}
