package de.mpa.client.ui.chart;

import java.util.Arrays;
import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.PieDataset;

/**
 * TODO: API
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class PieToCategoryDataset extends AbstractDataset implements
		CategoryDataset, DatasetChangeListener {
	
	/**
	 * The source pie dataset.
	 */
	private final PieDataset source;
	
	public PieToCategoryDataset(PieDataset source) {
		this.source = source;
		this.source.addChangeListener(this);
	}
	
	public PieDataset getPieDataset() {
		return source;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Comparable getRowKey(int row) {
		return "items";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int getRowIndex(Comparable key) {
		return ("items".equals(key) ? 0 : -1);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getRowKeys() {
		return Arrays.asList("items");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable getColumnKey(int column) {
		return source.getKey(column);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int getColumnIndex(Comparable key) {
		return source.getIndex(key);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getColumnKeys() {
		return source.getKeys();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Number getValue(Comparable rowKey, Comparable columnKey) {
		return source.getValue(columnKey);
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public int getColumnCount() {
		return source.getItemCount();
	}

	@Override
	public Number getValue(int row, int column) {
		return source.getValue(column);
	}

	/**
     * Sends a {@link DatasetChangeEvent} to all registered listeners, with
     * this (not the underlying) dataset as the source.
     *
     * @param event  the event (ignored, a new event with this dataset as the
     *     source is sent to the listeners).
     */
	@Override
    public void datasetChanged(DatasetChangeEvent event) {
        this.fireDatasetChanged();
    }
}
