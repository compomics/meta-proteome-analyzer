package de.mpa.client.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * A combo box whose items can be disabled.
 * 
 * @author behne
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class DisableComboBox extends JComboBox {
	
	List<Boolean> enableStates = new ArrayList<Boolean>();
	
	@SuppressWarnings("unchecked")
	public DisableComboBox(Object[] objects) {
		super(objects);
		for (int i = 0; i < objects.length; i++) {
			enableStates.add(true);
		}
		setRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel label = new JLabel();
				label.setOpaque(true);
				label.setText(value.toString());
				if (index >= 0) {
					if (!enableStates.get(index)) {
						label.setEnabled(false);
						label.setFocusable(false);
					} else {
						if (isSelected)
							label.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
					}
				}
				return label;
			}
		});
	}
	
	@Override
	public void setSelectedIndex(int index) {
		if (enableStates.get(index))
			super.setSelectedIndex(index);
	}

	public void setItemEnabledAt(int index, boolean enabled) {
		enableStates.set(index, enabled);
	}
	
	// TODO: properly handle item addition, insertion, removal, etc.
}
