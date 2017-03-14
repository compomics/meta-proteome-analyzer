package de.mpa.client.ui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTableHeader;

/**
 * Class to enable tables to display and interact with components in their headers.<br>
 * To be used in tandem with {@link ComponentHeaderRenderer}.
 * 
 * @author A. Behne
 * 
 * @see ComponentHeaderRenderer
 */
@SuppressWarnings("serial")
public class ComponentTableHeader extends JXTableHeader {

	private ComponentHeaderRenderer chr;
	private final String[] columnToolTips;
	private final boolean[] reorderingAllowedColumns;
	
    /**
     * Constructs a <code>JTableHeader</code> which is initialized with
     * <code>columnModel</code> as the column model.
     * 
     * @param columnModel the column model for the table
     */
	public ComponentTableHeader(TableColumnModel columnModel) {
		this(columnModel, null);
	}
	
	/**
	 * Constructs a <code>JTableHeader</code> which is initialized with
	 * <code>columnModel</code> as the column model and a string array
	 * containing column-specific tooltips.
	 * 
	 * @param columnModel the column model for the table
	 */
	public ComponentTableHeader(TableColumnModel columnModel, String[] columnToolTips) {
		super(columnModel);
		this.columnToolTips = columnToolTips;
        this.reorderingAllowedColumns = new boolean[columnModel.getColumnCount()];
	}
	
	@Override
	public String getToolTipText(MouseEvent me) {
		if (this.columnToolTips != null) {
			return this.columnToolTips[this.table.convertColumnIndexToModel(
                    this.columnModel.getColumnIndexAtX(me.getX()))];
		}
		return super.getToolTipText(me);
	}

	// hacky mouse event interception to avoid row sorting when hitting table header component
	@Override
	protected void processMouseEvent(MouseEvent me) {
		if (me.getID() == MouseEvent.MOUSE_PRESSED ||
				me.getID() == MouseEvent.MOUSE_CLICKED) {
			int col = this.columnModel.getColumnIndexAtX(me.getX());
			if (col != -1) {
				if (this.columnModel.getColumn(col).getHeaderRenderer() instanceof ComponentHeaderRenderer) {
                    this.chr = (ComponentHeaderRenderer) this.columnModel.getColumn(col).getHeaderRenderer();
					Rectangle headerRect = getHeaderRect(col);
					Rectangle compRect = this.chr.getComponent().getBounds();
					compRect.x += headerRect.x;
					if ((compRect.contains(me.getPoint()))) {

                        this.chr.dispatchEvent(new MouseEvent(
								me.getComponent(), me.getID(), me.getWhen(), me.getModifiers(),
								me.getX()-headerRect.x, me.getY(), me.getXOnScreen(), me.getYOnScreen(),
								me.getClickCount(), me.isPopupTrigger(), me.getButton()));
                        repaint(headerRect);
						// fool other listeners by changing click-type events into release-type ones
						if (me.getID() == MouseEvent.MOUSE_CLICKED) {
							me = new MouseEvent(
									me.getComponent(), MouseEvent.MOUSE_RELEASED, me.getWhen(), me.getModifiers(),
									me.getX()-headerRect.x, me.getY(), me.getXOnScreen(), me.getYOnScreen(),
									me.getClickCount(), me.isPopupTrigger(), me.getButton());
						}
					}
				}
			}
		} else if (me.getID() == MouseEvent.MOUSE_RELEASED) {
			if (this.chr != null) {
                this.chr.dispatchEvent(me);
                this.chr = null;
                repaint();
			}
		}
		super.processMouseEvent(me);
	}

	/**
	 * Sets whether the user can drag the specified column's header to reorder columns. 
	 * 
	 * @param reorderingAllowed <code>true</code> if the table view should allow reordering; otherwise <code>false</code>.
	 * @param column the column to be affected.
	 */
	public void setReorderingAllowed(boolean reorderingAllowed, int column) {
        this.reorderingAllowedColumns[column] = reorderingAllowed;
	}
	
}
