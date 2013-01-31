package de.mpa.client.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Custom titled border capable of displaying an embedded component.<br>
 * Based on <a href="http://www.jroller.com/santhosh/entry/component_titled_border">
 * Santhosh Kumar's implementation</a>.
 */
public class ComponentTitledBorder implements Border, MouseListener, MouseMotionListener {
	
	/**
	 * The border component's pixel offset from the left edge.
	 */
	private int offset = 8;

	/**
	 * The component that is drawn on top of the border.
	 */
	private JComponent comp;
	
	/**
	 * The parent component around which the border is drawn.
	 */
	private JComponent container;
	
	/**
	 * The area containing the border's component.
	 */
	private Rectangle rect;
	
	/**
	 * The underlying border on top of which the border's component will be painted.
	 */
	private Border border;
	
	/**
	 * Dummy panel for painting purposes.
	 */
	private JPanel intermediate;

	/**
	 * Boolean flag denoting whether the mouse pointer is currently resting on the border.
	 */
	private boolean hovering = false;

	/**
	 * Creates a default titled border with the specified component painted on top.
	 * @param comp the component to be displayed
	 * @param container the enclosed container
	 */
	public ComponentTitledBorder(JComponent comp, JComponent container) {
		this(comp, container, BorderFactory.createTitledBorder(" "));
	}

	/**
	 * Creates a component titled border with the specified component painted on
	 * top of the specified border underneath.
	 * @param comp the component to be displayed
	 * @param container the enclosed container
	 * @param border the underlying border
	 */
	public ComponentTitledBorder(JComponent comp, JComponent container, Border border) {
		this.comp = comp;
		this.comp.setOpaque(false);
		this.comp.setFont(UIManager.getFont("TitledBorder.font"));
		this.comp.setForeground(UIManager.getColor("TitledBorder.titleColor"));
		this.comp.setBorder(new EmptyBorder(0, 1, 0, 1));
		Dimension size = comp.getPreferredSize();
		this.rect = new Rectangle(offset, 1, size.width, size.height);
		this.container = container;
		this.border = border;
		this.intermediate = new JPanel();
		container.addMouseListener(this);
		container.addMouseMotionListener(this);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Insets borderInsets = border.getBorderInsets(c);
		Insets insets = getBorderInsets(c);
		int temp = (insets.top - borderInsets.top) / 2;
		border.paintBorder(c, g, x, y + temp, width, height - temp);
		g.setColor(comp.getBackground());
		g.fillRect(rect.x-1, rect.y, rect.width+2, rect.height);
		SwingUtilities.paintComponent(g, comp, intermediate, rect);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		Dimension size = comp.getPreferredSize();
		Insets insets = border.getBorderInsets(c);
		insets.top = Math.max(insets.top, size.height);
		return insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	/**
	 * Sets whether or not this border's component is enabled.
	 * @param enabled <code>true</code> if this border's component should be enabled,
	 * 				  <code>false</code> otherwise
	 */
	public void setEnabled(boolean enabled) {
		comp.setEnabled(enabled);
	}
	
	/**
	 * Convenience method to handle mouse events.
	 * @param me the mouse event
	 */
	private void dispatchEvent(MouseEvent me) {
		// determine whether border needs to be repainted, e.g. to reflect mouse-over changes
		boolean doRepaint = false;
		int id = me.getID();
		if ((id == MouseEvent.MOUSE_ENTERED) || (id == MouseEvent.MOUSE_MOVED) && !hovering) {
			if (rect.contains(me.getX(), me.getY())) {
				hovering = true;
				id = MouseEvent.MOUSE_ENTERED;
				doRepaint = true;
			}
		} else if ((id == MouseEvent.MOUSE_EXITED) || (id == MouseEvent.MOUSE_MOVED) && hovering) {
			if (!rect.contains(me.getX(), me.getY())) {
				hovering = false;
				id = MouseEvent.MOUSE_EXITED;
				doRepaint = true;
			}
		} else {
			if (rect.contains(me.getX(), me.getY())) {
				doRepaint = true;
			}
		}

		if (doRepaint) {
			// repaint the border's component
			Point pt = me.getPoint();
			pt.translate(-offset, 0);
			comp.setBounds(rect);
			comp.dispatchEvent(new MouseEvent(comp, id, me.getWhen(),
					me.getModifiers(), pt.x, pt.y, me.getClickCount(),
					me.isPopupTrigger(), me.getButton()));
			// repaint the enclosed container if invalidated
			if (!comp.isValid()) {
				container.repaint();
			}
		}
	}

	/* Mouse event handling, all redirecting to dispatchEvent() method */
	public void mouseClicked(MouseEvent me) {
		dispatchEvent(me);
	}
	public void mouseEntered(MouseEvent me) {
		dispatchEvent(me);
	}
	public void mouseExited(MouseEvent me) {
		dispatchEvent(me);
	}
	public void mousePressed(MouseEvent me) {
		dispatchEvent(me);
	}
	public void mouseReleased(MouseEvent me) {
		dispatchEvent(me);
	}
	public void mouseDragged(MouseEvent me) {
//		dispatchEvent(me);
	}
	public void mouseMoved(MouseEvent me) {
		dispatchEvent(me);
	}
}