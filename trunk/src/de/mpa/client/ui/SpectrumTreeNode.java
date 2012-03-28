package de.mpa.client.ui;

import java.io.File;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;

public class SpectrumTreeNode extends AbstractMutableTreeTableNode {
	
	private String title;
	private int significantPeaks;
	private double TIC;
	private double SNR;
	
	private boolean selected = false;
	
	public SpectrumTreeNode(File file) {
		this.userObject = file;
		this.title = file.getPath();
	}
	
	public SpectrumTreeNode(Long pos, String title, int significantPeaks, double TIC, double SNR) {
		this.userObject = pos;
		this.title = title;
		this.significantPeaks = significantPeaks;
		this.TIC = TIC;
		this.SNR = SNR;
	}
	
	public SpectrumTreeNode(String str) {
		this.userObject = str;
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int col) {
		if (userObject instanceof String) {
			if (col == 0) {
				return userObject.toString();
			}
		} else if (userObject instanceof File) {
			File file = (File) userObject;
			switch (col) {
			case 0:
				return file.getName();
			case 1:
				return file.getAbsolutePath();
			}
		} else if (userObject instanceof Long) {
			switch (col) {
			case 0:
				return ("Spectrum " + (parent.getIndex(this) + 1));
			case 1:
				return title;
			case 2:
				return significantPeaks;
			case 3:
				return TIC;
			case 4:
				return SNR;
			}
		}
		return null;
	}
	
	@Override
	public void setValueAt(Object aValue, int column) {
		if (aValue instanceof Boolean) {
			this.selected = (Boolean) aValue;
		} else {
			super.setValueAt(aValue, column);
		}
	}

	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public Long getPos() {
		return (Long) userObject; 
	}
	
}
