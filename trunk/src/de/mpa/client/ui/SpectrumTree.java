package de.mpa.client.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import de.mpa.algorithms.RankedLibrarySpectrum;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class SpectrumTree extends JTree implements TreeSelectionListener {
	
	private ClientFrame clientFrame;

	private TreeType treeType;
	private File lastSelectedFile;
	private MascotGenericFileReader reader;

	public enum TreeType { FILE_SELECT, RESULT_LIST }

	public SpectrumTree(TreeModel treeModel, TreeType treeType, ClientFrame clientFrame) {
		super(treeModel);
		this.treeType = treeType;
		this.clientFrame = clientFrame;
		this.addTreeSelectionListener(this);
	}

	public String convertValueToText(Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object obj = node.getUserObject();
		if (obj instanceof File) {
			return ((File)obj).getName();
		} else if (leaf && !((DefaultMutableTreeNode)value).isRoot()) {
			if (treeType == TreeType.RESULT_LIST) {
				try {
					ArrayList<RankedLibrarySpectrum> hitList = clientFrame.getResultMap().get(getSpectrumAt(node).getTitle());
					int numHits = 0;
					if (hitList != null) {
						numHits = hitList.size();
					}
					if (numHits == 1) {
						return ("Spectrum " + obj + "     " + numHits + " hit");
					} else {
						return ("Spectrum " + obj + "     " + numHits + " hits");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			} else {
				return ("Spectrum " + obj);
			}
		} else {
			return value.toString();
		}
	}

	public MascotGenericFile getSpectrumAt(DefaultMutableTreeNode node) throws IOException {
//		if (!node.isRoot() && node.isLeaf() && !clientFrame.specPosMap.isEmpty()) {
//			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
//			File mgfFile = (File) parent.getUserObject();
//			if (!mgfFile.equals(this.lastSelectedFile)) {	// check whether new file has been selected
//				this.lastSelectedFile = mgfFile;			// and open new reader if true
//				if (this.reader != null) {
//					this.reader.close();
//				}
//				this.reader = new MascotGenericFileReader(mgfFile, MascotGenericFileReader.NONE);
//			}
//			int index = (Integer) node.getUserObject();
//			long pos = clientFrame.specPosMap.get(mgfFile.getAbsolutePath()).get(index-1);
//				return reader.loadNthSpectrum(index, pos);
//		}
		return null;
	}

	@Override
	public void valueChanged(TreeSelectionEvent tse) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
		MascotGenericFile mgf = null;

		if (node == null) {
			return;	// nothing is selected.
		} else {
			try {
				mgf = getSpectrumAt(node);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		switch (treeType) {
		case FILE_SELECT:
//			clientFrame.getFilePanel().refreshFileTable(mgf, node);
			break;
		case RESULT_LIST:
			clientFrame.refreshResultsTables(mgf);
			break;
		}

	}
}