package de.mpa.client.ui.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

/**
 * Class holding icons and icon manipulation-related methods.
 * 
 * @author Behne
 */
public class IconConstants {

	/** <img src="../../../resources/icons/check16.png"> */
	public static final ImageIcon CHECK_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/check16.png"));
	public static final ImageIcon CHECK_ROLLOVER_ICON = createRescaledIcon(CHECK_ICON, 1.1f);
	public static final ImageIcon CHECK_PRESSED_ICON = createRescaledIcon(CHECK_ICON, 0.8f);

	/** <img src="../../../resources/icons/cross16.png"> */
	public static final ImageIcon CROSS_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/cross16.png"));
	public static final ImageIcon CROSS_ROLLOVER_ICON = createRescaledIcon(CROSS_ICON, 1.1f);
	public static final ImageIcon CROSS_PRESSED_ICON = createRescaledIcon(CROSS_ICON, 0.8f);

	/** <img src="../../../resources/icons/bulb_off16.png"> */
	public static final ImageIcon HELP_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/bulb_off16.png"));
	public static final ImageIcon HELP_ROLLOVER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/bulb16.png"));

	/** <img src="../../../resources/icons/add16.png"> */
	public static final ImageIcon ADD_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/add16.png"));
	public static final ImageIcon ADD_ROLLOVER_ICON = createRescaledIcon(ADD_ICON, 1.1f);
	public static final ImageIcon ADD_PRESSED_ICON = createRescaledIcon(ADD_ICON, 0.8f);

	/** <img src="../../../resources/icons/update16.png"> */
	public static final ImageIcon UPDATE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/update16.png"));
	public static final ImageIcon UPDATE_ROLLOVER_ICON = createRescaledIcon(UPDATE_ICON, 1.1f);
	public static final ImageIcon UPDATE_PRESSED_ICON = createRescaledIcon(UPDATE_ICON, 0.8f);

	/** <img src="../../../resources/icons/cancel16.png"> */
	public static final ImageIcon CANCEL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/cancel16.png"));
	public static final ImageIcon CANCEL_ROLLOVER_ICON = createRescaledIcon(CANCEL_ICON, 1.2f);
	public static final ImageIcon CANCEL_PRESSED_ICON = createRescaledIcon(CANCEL_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/save16.png"> */
	public static final ImageIcon SAVE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/save16.png"));
	public static final ImageIcon SAVE_ROLLOVER_ICON = createRescaledIcon(SAVE_ICON, 1.2f);
	public static final ImageIcon SAVE_PRESSED_ICON = createRescaledIcon(SAVE_ICON, 0.8f);

	/** <img src="../../../resources/icons/database_save.png"> */
	public static final ImageIcon SAVE_DB_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/database_save.png"));
	public static final ImageIcon SAVE_DB_ROLLOVER_ICON = createRescaledIcon(SAVE_DB_ICON, 1.1f);
	public static final ImageIcon SAVE_DB_PRESSED_ICON = createRescaledIcon(SAVE_DB_ICON, 0.8f);

	/** <img src="../../../resources/icons/database_refresh.png"> */
	public static final ImageIcon GO_DB_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/database_go16.png"));
	public static final ImageIcon GO_DB_ROLLOVER_ICON = createRescaledIcon(GO_DB_ICON, 1.1f);
	public static final ImageIcon GO_DB_PRESSED_ICON = createRescaledIcon(GO_DB_ICON, 0.8f);

	/** <img src="../../../resources/icons/database_delete.png"> */
	public static final ImageIcon DELETE_DB_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/database_delete.png"));
	public static final ImageIcon DELETE_DB_ROLLOVER_ICON = createRescaledIcon(DELETE_DB_ICON, 1.1f);
	public static final ImageIcon DELETE_DB_PRESSED_ICON = createRescaledIcon(DELETE_DB_ICON, 0.8f);

	/** <img src="../../../resources/icons/add_folder16.png"> */
	public static final ImageIcon ADD_FOLDER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/add_folder16.png"));
	public static final ImageIcon ADD_FOLDER_ROLLOVER_ICON = createRescaledIcon(ADD_FOLDER_ICON, 1.1f);
	public static final ImageIcon ADD_FOLDER_PRESSED_ICON = createRescaledIcon(ADD_FOLDER_ICON, 0.8f);

	/** <img src="../../../resources/icons/view_folder16.png"> */
	public static final ImageIcon VIEW_FOLDER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/view_folder16.png"));
	public static final ImageIcon VIEW_FOLDER_ROLLOVER_ICON = createRescaledIcon(VIEW_FOLDER_ICON, 1.1f);
	public static final ImageIcon VIEW_FOLDER_PRESSED_ICON = createRescaledIcon(VIEW_FOLDER_ICON, 0.8f);

	/** <img src="../../../resources/icons/delete_folder16.png"> */
	public static final ImageIcon DELETE_FOLDER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/delete_folder16.png"));
	public static final ImageIcon DELETE_FOLDER_ROLLOVER_ICON = createRescaledIcon(DELETE_FOLDER_ICON, 1.1f);
	public static final ImageIcon DELETE_FOLDER_PRESSED_ICON = createRescaledIcon(DELETE_FOLDER_ICON, 0.8f);

	/** <img src="../../../resources/icons/add_page16.png"> */
	public static final ImageIcon ADD_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/add_page16.png"));
	public static final ImageIcon ADD_PAGE_ROLLOVER_ICON = createRescaledIcon(ADD_PAGE_ICON, 1.1f);
	public static final ImageIcon ADD_PAGE_PRESSED_ICON = createRescaledIcon(ADD_PAGE_ICON, 0.8f);

	/** <img src="../../../resources/icons/view_page16.png"> */
	public static final ImageIcon VIEW_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/view_page16.png"));
	public static final ImageIcon VIEW_PAGE_ROLLOVER_ICON = createRescaledIcon(VIEW_PAGE_ICON, 1.1f);
	public static final ImageIcon VIEW_PAGE_PRESSED_ICON = createRescaledIcon(VIEW_PAGE_ICON, 0.8f);

	/** <img src="../../../resources/icons/database_refresh.png"> */
	public static final ImageIcon GO_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/page_go16.png"));
	public static final ImageIcon GO_PAGE_ROLLOVER_ICON = createRescaledIcon(GO_PAGE_ICON, 1.1f);
	public static final ImageIcon GO_PAGE_PRESSED_ICON = createRescaledIcon(GO_PAGE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/delete_page16.png"> */
	public static final ImageIcon DELETE_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/delete_page16.png"));
	public static final ImageIcon DELETE_PAGE_ROLLOVER_ICON = createRescaledIcon(DELETE_PAGE_ICON, 1.1f);
	public static final ImageIcon DELETE_PAGE_PRESSED_ICON = createRescaledIcon(DELETE_PAGE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/next.png"> */
	public static final ImageIcon NEXT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/next.png"));
	public static final ImageIcon NEXT_ROLLOVER_ICON = createRescaledIcon(NEXT_ICON, 1.2f);
	public static final ImageIcon NEXT_PRESSED_ICON = createRescaledIcon(NEXT_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/next.png"> (flipped horizontally)*/
	public static final ImageIcon PREV_ICON = createFlippedIcon(NEXT_ICON, SwingConstants.HORIZONTAL);
	public static final ImageIcon PREV_ROLLOVER_ICON = createRescaledIcon(PREV_ICON, 1.2f);
	public static final ImageIcon PREV_PRESSED_ICON = createRescaledIcon(PREV_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/size_vertical_32.png"> */
	public static final ImageIcon SIZE_VERT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/size_vertical_32.png"));
	public static final ImageIcon SIZE_VERT_ROLLOVER_ICON = createRescaledIcon(SIZE_VERT_ICON, 1.2f);
	public static final ImageIcon SIZE_VERT_PRESSED_ICON = createRescaledIcon(SIZE_VERT_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/hierarchy16.png"> */
	public static final ImageIcon HIERARCHY_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/hierarchy16.png"));
	public static final ImageIcon HIERARCHY_ROLLOVER_ICON = createRescaledIcon(HIERARCHY_ICON, 1.2f);
	public static final ImageIcon HIERARCHY_PRESSED_ICON = createRescaledIcon(HIERARCHY_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/pie_chart16.png"> */
	public static final ImageIcon PIE_CHART_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/pie_chart16.png"));
	public static final ImageIcon PIE_CHART_ROLLOVER_ICON = createRescaledIcon(PIE_CHART_ICON, 1.2f);
	public static final ImageIcon PIE_CHART_PRESSED_ICON = createRescaledIcon(PIE_CHART_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/frame_full16.png"> */
	public static final ImageIcon FRAME_FULL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/frame_full16.png"));
	public static final ImageIcon FRAME_FULL_ROLLOVER_ICON = createRescaledIcon(FRAME_FULL_ICON, 1.2f);
	public static final ImageIcon FRAME_FULL_PRESSED_ICON = createRescaledIcon(FRAME_FULL_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/frame_tiled16.png"> */
	public static final ImageIcon FRAME_TILED_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/frame_tiled16.png"));
	public static final ImageIcon FRAME_TILED_ROLLOVER_ICON = createRescaledIcon(FRAME_TILED_ICON, 1.2f);
	public static final ImageIcon FRAME_TILED_PRESSED_ICON = createRescaledIcon(FRAME_TILED_ICON, 0.8f);

	/** <img src="../../../resources/icons/protein.png"> */
	public static final ImageIcon PROTEIN_TREE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/protein.png"));
	/** <img src="../../../resources/icons/metaprotein.png"> */
	public static final ImageIcon METAPROTEIN_TREE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/metaprotein.png"));
	
	
	/**
	 * Rescales color space of a provided icon. Use to brighten or darken icons.
	 * 
	 * @param icon The icon to be rescaled.
	 * @param scale The scale to be used. Below 1.0 will darken, above 1.0 will brighten the image.
	 * @return An icon with rescaled color space.
	 */
	public static ImageIcon createRescaledIcon(ImageIcon icon, float scale) {
		Image image = icon.getImage();
		
		// transfer image data to buffered image
		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		
		// re-scale pixel intensities
		float[] factors = new float[] { scale, scale, scale, 1.0f };
		float[] offsets = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
		RescaleOp op = new RescaleOp(factors, offsets, null);
		
		return new ImageIcon(op.filter(bi, null));
	}
	
	public static ImageIcon createFlippedIcon(ImageIcon icon, int orientation) {
		Image image = icon.getImage();
		
		// transfer image data to buffered image
		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		// flip pixel coordinates
		AffineTransform tx = (orientation == SwingConstants.HORIZONTAL) ?
				AffineTransform.getScaleInstance(-1, 1) : AffineTransform.getScaleInstance(1, -1);
		tx.translate(-bi.getWidth(), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		
		return new ImageIcon(op.filter(bi, null));
	}
	
	public static Icon createArrowedIcon(Icon icon) {
		return createArrowedIcon(icon, SwingConstants.EAST);
	}
	
	public static Icon createArrowedIcon(Icon icon, int location) {
		return createArrowedIcon(icon, location, SwingConstants.SOUTH);
	}

	public static Icon createArrowedIcon(final Icon icon, final int location, final int direction) {
		final int xPadding, yPadding;
		final int xMargin = 2, yMargin = 2;
		if (location == SwingConstants.WEST || location == SwingConstants.EAST) {
			// to the left or right of the icon
			yPadding = 0;
			if (direction == SwingConstants.WEST || direction == SwingConstants.EAST) {
				// left- or right-pointing triangle
				xPadding = 6 + xMargin;
			} else {
				// up- or down-pointing triangle
				xPadding = 10 + xMargin;
			}
		} else {
			// above or below the icon
			xPadding = 0;
			if (direction == SwingConstants.WEST || direction == SwingConstants.EAST) {
				// left- or right-pointing triangle
				yPadding = 10 + yMargin;
			} else {
				// up- or down-pointing triangle
				yPadding = 6 + yMargin;
			}
		}
		
		return new Icon() {
			public void paintIcon(Component c, Graphics g, int x, int y) {
				// paint original icon
				icon.paintIcon(c, g, (location == SwingConstants.WEST) ? x + xPadding + xMargin: x,
						(location == SwingConstants.NORTH) ? y + yPadding + yMargin : y);
				// determine arrowhead offsets
				int xOffset = 0, yOffset = 0;
				switch (location) {
				case SwingConstants.WEST:
					xOffset = 1;
					if (direction == SwingConstants.WEST || direction == SwingConstants.EAST) {
						yOffset = icon.getIconHeight() / 2 - 4;
					} else {
						yOffset = icon.getIconHeight() / 2 - 2;
					}
					break;
				case SwingConstants.EAST:
					xOffset = icon.getIconWidth() + xMargin + 1;
					if (direction == SwingConstants.WEST || direction == SwingConstants.EAST) {
						yOffset = icon.getIconHeight() / 2 - 4;
					} else {
						yOffset = icon.getIconHeight() / 2 - 2;
					}
					break;
				case SwingConstants.NORTH:
					yOffset = 1;
					if (direction == SwingConstants.WEST || direction == SwingConstants.EAST) {
						xOffset = icon.getIconWidth() / 2 - 2;
					} else {
						xOffset = icon.getIconWidth() / 2 - 4;
					}
					break;
				case SwingConstants.SOUTH:
					yOffset = icon.getIconHeight() + yMargin + 1;
					if (direction == SwingConstants.WEST || direction == SwingConstants.EAST) {
						xOffset = icon.getIconWidth() / 2 - 2;
					} else {
						xOffset = icon.getIconWidth() / 2 - 4;
					}
					break;
				}
				// draw arrowhead
				int[] xPoints = null, yPoints = null;
				switch (direction) {
				case SwingConstants.WEST:
					xPoints = new int[] { x + xOffset + 4, x + xOffset + 4, x + xOffset };
					yPoints = new int[] { y + yOffset, y + yOffset + 8, y + yOffset + 4 };
					break;
				case SwingConstants.EAST:
					xPoints = new int[] { x + xOffset, x + xOffset, x + xOffset + 4 };
					yPoints = new int[] { y + yOffset + 8, y + yOffset, y + yOffset + 4 };
					break;
				case SwingConstants.NORTH:
					xPoints = new int[] { x + xOffset + 8, x + xOffset, x + xOffset + 4 };
					yPoints = new int[] { y + yOffset + 4, y + yOffset + 4, y + yOffset };
					break;
				case SwingConstants.SOUTH:
					xPoints = new int[] { x + xOffset, x + xOffset + 8, x + xOffset + 4 };
					yPoints = new int[] { y + yOffset, y + yOffset, y + yOffset + 4 };
					break;
				}
				g.setColor(Color.BLACK);
				g.fillPolygon(xPoints, yPoints, 3);
			}
			public int getIconWidth() {
				return icon.getIconWidth() + xPadding;
			}
			public int getIconHeight() {
				return icon.getIconHeight() + yPadding;
			}
		};
	}

}
