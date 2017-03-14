package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;

import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.PlasticButtonUI;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;

import de.mpa.util.ColorUtils;

/**
 * TODO: API
 * 
 * @author A. Behne
 */
public class RolloverButtonUI extends PlasticButtonUI {

    private static final RolloverButtonUI instance = new RolloverButtonUI();

    public static ComponentUI createUI(JComponent b) {
        return RolloverButtonUI.instance;
    }

    public void installDefaults(AbstractButton b) {
    	super.installDefaults(b);
    	
    	b.setMargin(new Insets(1, 1, 1, 1));
    	b.setBorder(RolloverButtonUI.RolloverButtonBorder.getInstance());
    	b.setBackground(new Color(255, 255, 255, 159));
    	b.setOpaque(false);
    }

    @Override
	public void update(Graphics g, JComponent c) {
        AbstractButton btn = (AbstractButton) c;
		ButtonModel model = btn.getModel();
		if (model.isSelected()) {
			this.paintButtonPressed(g, btn);
			if (btn.isContentAreaFilled()) {
				g.setColor(new Color(255, 255, 255, model.isRollover() ? 127 : 63));
				g.fillRoundRect(1, 1, c.getWidth() - 2, c.getHeight() - 2, 3, 3);
			}
		} else if (model.isRollover() || model.isArmed()) {
            if (btn.isContentAreaFilled()) {
            	g.setColor(c.getBackground());
            	g.fillRect(1, 1, c.getWidth() - 2, c.getHeight() - 2);

				Rectangle r = new Rectangle(1, 1, c.getWidth() - 2, c.getHeight() - 1);
				Color brightenStop = UIManager.getColor("Plastic.brightenStop");
				if (brightenStop == null) {
		            brightenStop = PlasticTheme.BRIGHTEN_STOP;
		        }
//
            	// Add round sides
        		Graphics2D g2 = (Graphics2D) g;
        		int border = 10;
        		g2.setPaint(new GradientPaint(r.x, r.y, brightenStop, r.x + border, r.y, PlasticTheme.BRIGHTEN_START));
        		g2.fillRect(r.x, r.y, border, r.height);
        		int x = r.x + r.width -border;
        		int y = r.y;
        		g2.setPaint(new GradientPaint(x, y, PlasticTheme.DARKEN_START, x + border, y, PlasticTheme.LT_DARKEN_STOP));
        		g2.fillRect(x, y, border, r.height);

        		int height = (int) (r.height * 0.5);
        		g2.setPaint(new GradientPaint(
        				r.x, r.y, brightenStop,
        				r.x, r.y + height, PlasticTheme.BRIGHTEN_START));
        		g2.fillRect(r.x, r.y, r.width, height);
        		g2.setPaint(new GradientPaint(
        				r.x, r.y + height, PlasticTheme.DARKEN_START,
        				r.x, r.y + r.height, PlasticTheme.LT_DARKEN_STOP));
        		g2.fillRect(r.x, r.y + height, r.width, height);
            }
		}
        this.paint(g, c);
	}

	@Override
	protected void paintFocus(Graphics g, AbstractButton b,
			Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        g.setColor(getFocusColor());
        g.drawRect(2, 2, b.getWidth() - 5, b.getHeight() - 5);
	}

	@Override
	protected void paintButtonPressed(Graphics g, AbstractButton b) {
		if (b.isContentAreaFilled()) {
			Dimension size = b.getSize();
			g.setColor(this.getSelectColor());
			g.fillRoundRect(0, 0, size.width, size.height, 5, 5);
		}
	}

	/**
	 * TODO: API
	 *
	 * @author A. Behne
	 */
	@SuppressWarnings("serial")
	public static class RolloverButtonBorder extends AbstractBorder {

		private static RolloverButtonUI.RolloverButtonBorder instance;

		public static RolloverButtonUI.RolloverButtonBorder getInstance() {
			if (instance == null) {
				instance = new RolloverButtonUI.RolloverButtonBorder();
			}
			return RolloverButtonUI.RolloverButtonBorder.instance;
		}
		
		private RolloverButtonBorder() {
        }
		
		/**
		 * The border insets.
		 */
		private final Insets insets = new Insets(3, 2, 3, 2);
		
		@Override
		public void paintBorder(Component c, Graphics g, int x, int y,
				int w, int h) {
			
			AbstractButton button = (AbstractButton) c;
			ButtonModel model = button.getModel();
			
			if (model.isRollover() || model.isArmed() || model.isSelected()) {
                drawButtonBorder(g, x, y, w, h,
						PlasticLookAndFeel.getControlDarkShadow(),
						LookUtils.getSlightlyBrighter(
								PlasticLookAndFeel.getControlDarkShadow(),
								1.25f));
				if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
					g.setColor(ColorUtils.getTranslucentColor(
							PlasticLookAndFeel.getControlDarkShadow(), 128));
					g.fillRect(2, 1, w - 4, 1);

					g.setColor(ColorUtils.getTranslucentColor(
							PlasticLookAndFeel.getControlHighlight(), 80));
					g.fillRect(2, h - 2, w - 4, 1);
				} else {
					// inner edges
					g.setColor(new Color(255, 255, 255, 191));
					g.fillRect(x + 2, y + 1, w - 4, 1); // top
					g.fillRect(x + 1, y + 2, 1, h - 4); // left
					g.setColor(new Color(255, 255, 255, 63));
					g.fillRect(x + 2, y + h - 2, w - 4, 1); // bottom
					g.fillRect(x + w - 2, y + 2, 1, h - 4); // right
				}
			}
			
		}
		
		private void drawButtonBorder(Graphics g, int x, int y, int w, int h,
				Color edgeColor, Color cornerColor) {
			// prepare
			g.setClip(new RoundRectangle2D.Double(0, 0, w, h, 3, 3));
			
			// edges
			g.setColor(edgeColor);
			g.drawRect(x, y, w - 1, h - 1);

			// corners
			g.setColor(cornerColor);
			g.fillRect(0, 0, 2, 2);
			g.fillRect(0, h - 2, 2, 2);
			g.fillRect(w - 2, 0, 2, 2);
			g.fillRect(w - 2, h - 2, 2, 2);

			// clean up
			g.setClip(null);
		}
	    
		@Override
		public Insets getBorderInsets(Component c) {
			return this.insets;
		}
	}

}
