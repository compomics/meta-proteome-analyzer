package de.mpa.client.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;


/**
 *  The ButtonColumn class provides a renderer and an editor that looks like a
 *  JButton. The renderer and editor will then be used for a specified column
 *  in the table. The TableModel will contain the String to be displayed on
 *  the button.
 *
 *  The button can be invoked by a mouse click or by pressing the space bar
 *  when the cell has focus. Optionally a mnemonic can be set to invoke the
 *  button. When the button is invoked the provided Action is invoked. The
 *  source of the Action will be the table. The action command will contain
 *  the model row number of the button that was clicked.
 */
public class ButtonColumn extends AbstractCellEditor
	implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener
{
	private JTable table;
	private Action action;
	private int mnemonic;
	private Border originalBorder;
	private Border focusBorder;

	private JButton renderButton;
	private JButton editButton;
	private Object editorValue;
	private boolean isButtonColumnEditor;
	
	/**
	 *  Create the ButtonColumn to be used as a renderer and editor. 
	 *
	 *  @param table the table containing the button renderer/editor
	 *  @param action the Action to be invoked when the button is invoked
	 */
	public ButtonColumn(JTable table, Action action) {
		this.table = table;
		this.action = action;

		JButton renderButton = new JButton();
		renderButton.setUI(new MetalButtonUI());
		renderButton.setOpaque(true);
		renderButton.setBorder(null);
		this.renderButton = renderButton;
		
		JButton editButton = new JButton();
		editButton.setOpaque(true);
		editButton.setFocusPainted(false);
		editButton.addActionListener(this);
		editButton.setBorder(null);
		this.editButton = editButton;
		
		this.originalBorder = editButton.getBorder();
		
		this.setFocusBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
	}

	/**
	 *  Create the ButtonColumn to be used as a renderer and editor. The
	 *  renderer and editor will automatically be installed on the TableColumn
	 *  of the specified column.
	 *
	 *  @param table the table containing the button renderer/editor
	 *  @param action the Action to be invoked when the button is invoked
	 *  @param column the column to which the button renderer/editor is added
	 */
	public ButtonColumn(JTable table, Action action, int column) {
		this(table, action);

		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(column).setCellRenderer(this);
		columnModel.getColumn(column).setCellEditor(this);
		table.addMouseListener(this);
	}


	/**
	 *  Get foreground color of the button when the cell has focus
	 *
	 *  @return the foreground color
	 */
	public Border getFocusBorder() {
		return focusBorder;
	}

	/**
	 *  The foreground color of the button when the cell has focus
	 *
	 *  @param focusBorder the foreground color
	 */
	public void setFocusBorder(Border focusBorder) {
		this.focusBorder = focusBorder;
		this.editButton.setBorder(focusBorder);
	}

	public int getMnemonic() {
		return this.mnemonic;
	}

	/**
	 *  The mnemonic to activate the button when the cell has focus
	 *
	 *  @param mnemonic the mnemonic
	 */
	public void setMnemonic(int mnemonic) {
		this.mnemonic = mnemonic;
		this.renderButton.setMnemonic(mnemonic);
		this.editButton.setMnemonic(mnemonic);
	}

	@Override
	public Component getTableCellEditorComponent(
		JTable table, Object value, boolean isSelected, int row, int column) {
		String text = "";
		Icon icon = null;
		if (value instanceof Icon) {
			icon = (Icon) value;
		} else if (value != null) {
			text = value.toString();
		}
		this.editButton.setText(text);
		this.editButton.setIcon(icon);

		this.editorValue = value;
		return this.editButton;
	}

	@Override
	public Object getCellEditorValue() {
		return this.editorValue;
	}

//
//  Implement TableCellRenderer interface
//
	public Component getTableCellRendererComponent(
		JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (hasFocus) {
			this.renderButton.setBorder(this.focusBorder);
		} else {
			this.renderButton.setBorder(this.originalBorder);
		}

//		renderButton.setText((value == null) ? "" : value.toString());
		String text = "";
		Icon icon = null;
		if (value instanceof Icon) {
			icon = (Icon) value;
		} else if (value != null) {
			text = value.toString();
		}
		this.renderButton.setText(text);
		this.renderButton.setIcon(icon);
		
		return this.renderButton;
	}

//
//  Implement ActionListener interface
//
	/*
	 *	The button has been pressed. Stop editing and invoke the custom Action
	 */
	public void actionPerformed(ActionEvent e) {
//		int row = this.table.convertRowIndexToModel(this.table.getEditingRow());
		int row = this.table.getSelectedRow();
		this.fireEditingStopped();

		//  Invoke the Action
		this.action.actionPerformed(new ActionEvent(this.table,
			ActionEvent.ACTION_PERFORMED, "" + row));
	}

//
//  Implement MouseListener interface
//
	/*
	 *  When the mouse is pressed the editor is invoked. If you then then drag
	 *  the mouse to another cell before releasing it, the editor is still
	 *  active. Make sure editing is stopped when the mouse is released.
	 */
	public void mousePressed(MouseEvent e) {
    	if (this.table.isEditing() && (this.table.getCellEditor() == this)) {
    		this.isButtonColumnEditor = true;
    	}
    }

	public void mouseReleased(MouseEvent e) {
    	if (this.isButtonColumnEditor && this.table.isEditing()) {
    		this.table.getCellEditor().stopCellEditing();
    	}

    	this.isButtonColumnEditor = false;
    }

    public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
