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
 * MySwing: Advanced Swing Utilites Copyright (C) 2005 Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

public class ComponentTitledBorder implements Border, MouseListener, MouseMotionListener {
	
	private int offset = 8;

	private JComponent comp;
	private JComponent container;
	private Rectangle rect;
	private Border border;
	private JPanel intermediate;

	private boolean hovering = false;

	public ComponentTitledBorder(JComponent comp, JComponent container) {
		this(comp, container, null);
	}

	public ComponentTitledBorder(JComponent comp, JComponent container, Border border) {
		this.comp = comp;
		this.comp.setOpaque(false);
		this.comp.setFont(UIManager.getFont("TitledBorder.font"));
		this.comp.setForeground(UIManager.getColor("TitledBorder.titleColor"));
		this.comp.setBorder(new EmptyBorder(0, 1, 0, 1));
		Dimension size = comp.getPreferredSize();
		this.rect = new Rectangle(offset, 1, size.width, size.height);
		this.container = container;
		if (border == null) {
			border = BorderFactory.createTitledBorder(" ");
		}
		this.border = border;
		this.intermediate = new JPanel();
		container.addMouseListener(this);
		container.addMouseMotionListener(this);
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Insets borderInsets = border.getBorderInsets(c);
		Insets insets = getBorderInsets(c);
		int temp = (insets.top - borderInsets.top) / 2;
		border.paintBorder(c, g, x, y + temp, width, height - temp);
		g.setColor(comp.getBackground());
		g.fillRect(rect.x-1, rect.y, rect.width+2, rect.height);
		SwingUtilities.paintComponent(g, comp, intermediate, rect);
	}

	public Insets getBorderInsets(Component c) {
		Dimension size = comp.getPreferredSize();
		Insets insets = border.getBorderInsets(c);
		insets.top = Math.max(insets.top, size.height);
		return insets;
	}

	public void setEnabled(boolean enabled) {
		comp.setEnabled(enabled);
	}
	
	private void dispatchEvent(MouseEvent me) {
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
			Point pt = me.getPoint();
			pt.translate(-offset, 0);
			comp.setBounds(rect);
			comp.dispatchEvent(new MouseEvent(comp, id, me.getWhen(),
					me.getModifiers(), pt.x, pt.y, me.getClickCount(),
					me.isPopupTrigger(), me.getButton()));
			if (!comp.isValid())
				container.repaint();
		}
	}


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