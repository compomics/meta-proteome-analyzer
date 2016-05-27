package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileChooserDecorationFactory {
	
	public enum DecorationType {
		TEXT_PREVIEW,
		IMAGE_PREVIEW,
		NONE;
	}
	
	public static void decorate(JFileChooser fc, DecorationType type) {
		decorate(fc, type, null);
	}

	public static void decorate(JFileChooser fc, DecorationType type, JComponent addComp) {
		switch (type) {
		case TEXT_PREVIEW:
			decorateWithTextPreview(fc, addComp);
			break;
		case IMAGE_PREVIEW:
			decorateWithImagePreview(fc, addComp);
			break;
		case NONE:
		default:
			decorateWithComponent(fc, addComp);
			break;
		}
	}
	
	public static void decorateWithTextPreview(JFileChooser fc) {
		decorateWithTextPreview(fc, null);
	}
	
	public static void decorateWithTextPreview(JFileChooser fc, JComponent addComp) {
		FormLayout fl = new FormLayout("p:g", "t:p, 2dlu, f:p:g");
		if (addComp != null) {
			fl.appendRow(RowSpec.decode("2dlu"));
			fl.appendRow(RowSpec.decode("b:p"));
		}
		JPanel panel = new JPanel(fl);
		
		@SuppressWarnings("serial")
		final JTextArea textArea = new JTextArea() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (getText().isEmpty()) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(
							RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					String str = "No preview available.";
					int strWidth = g2d.getFontMetrics().stringWidth(str);
					g2d.drawString(str, (getWidth() - strWidth) / 2.0f, getHeight() / 2.0f);
				}
			}
		};
		textArea.setEditable(false);
		textArea.setBackground(Color.WHITE);
		JScrollPane textPane = new JScrollPane(textArea);
		textPane.setPreferredSize(new Dimension(200, 0));
		
//		panel.setPreferredSize(new Dimension(200, 0));
		
		panel.add(new JLabel("Preview"), CC.xy(1, 1));
		panel.add(textPane, CC.xy(1, 3));
		if (addComp != null) {
			panel.add(addComp, CC.xy(1, 5));
		}
		
		BorderLayout bl = (BorderLayout) fc.getLayout();
		Component fsv = bl.getLayoutComponent(BorderLayout.CENTER);
		Dimension fsvSize = fsv.getPreferredSize();
		fsv.setPreferredSize(new Dimension(fsvSize.width - 100, fsvSize.height));
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, fsv, panel);
		splitPane.setBorder(null);
		((BasicSplitPaneUI) splitPane.getUI()).getDivider().setBorder(null);
		
		fc.add(splitPane, BorderLayout.CENTER);
		
		fc.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String propName = evt.getPropertyName();
				if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propName)) {
					textArea.setText(null);
				} else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propName)) {
					JFileChooser fc = (JFileChooser) evt.getSource();
					File selFile = fc.getSelectedFile();
					if (selFile != null) {
						try {
							int bufSize = 8192;
							BufferedReader reader = new BufferedReader(new FileReader(selFile), bufSize);
							long fileSize = selFile.length();
							boolean oversized = false;
							if (fileSize > bufSize) {
								fileSize = bufSize;
								oversized = true;
							}
							char[] cbuf = new char[(int) fileSize];
							reader.read(cbuf);
							reader.close();
							String text = new String(cbuf);
							if (oversized) {
								text += " [...]\n(rest of file omitted)";
							}
							textArea.setText(text);
							textArea.setCaretPosition(0);
						} catch (IOException e) {
							JXErrorPane.showDialog(ClientFrame.getInstance(),
									new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
							textArea.setText(null);
						}
					} else {
						textArea.setText(null);
					}
				}
			}
		});
	}
	
	public static void decorateWithImagePreview(JFileChooser fc) {
		decorateWithImagePreview(fc, null);
	}
	
	public static void decorateWithImagePreview(JFileChooser fc, JComponent addComp) {
		// TODO: create image preview (or remove idea altogether)
	}
	
	public static void decorateWithComponent(JFileChooser fc, JComponent addComp) {
		fc.setAccessory(addComp);
	}

}
