package de.mpa.client.ui;

/**
 * Common interface for UI components that need to track whether they are
 * currently busy processing data.
 * 
 * @author A. Behne
 */
public interface Busyable {
	
	/**
	 * Returns whether this component is currently busy.
	 * @return <code>true</code> if this component is busy, <code>false</code> otherwise
	 */
    boolean isBusy();
	
	/**
	 * Sets the busy state of this component to the specified value.
	 * @param busy <code>true</code> if this component is to be busy, <code>false</code> otherwise
	 */
    void setBusy(boolean busy);

}
