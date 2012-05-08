package de.mpa.client.ui.icons;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.ImageIcon;

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
	/** @see IconConstants#CROSS_ICON */
	public static final ImageIcon CROSS_ROLLOVER_ICON = createRescaledIcon(CROSS_ICON, 1.1f);
	/** @see IconConstants#CROSS_ICON */
	public static final ImageIcon CROSS_PRESSED_ICON = createRescaledIcon(CROSS_ICON, 0.8f);

	/** <img src="../../../resources/icons/bulb_off16.png"> */
	public static final ImageIcon HELP_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/bulb_off16.png"));
	/** <img src="../../../resources/icons/bulb16.png"> */
	public static final ImageIcon HELP_ROLLOVER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/bulb16.png"));

	/** <img src="../../../resources/icons/add16.png"> */
	public static final ImageIcon ADD_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/add16.png"));
	/** @see IconConstants#ADD_ICON */
	public static final ImageIcon ADD_ROLLOVER_ICON = createRescaledIcon(ADD_ICON, 1.1f);
	/** @see IconConstants#ADD_ICON */
	public static final ImageIcon ADD_PRESSED_ICON = createRescaledIcon(ADD_ICON, 0.8f);

	/** <img src="../../../resources/icons/update16.png"> */
	public static final ImageIcon UPDATE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/update16.png"));
	/** @see IconConstants#UPDATE_ICON */
	public static final ImageIcon UPDATE_ROLLOVER_ICON = createRescaledIcon(UPDATE_ICON, 1.1f);
	/** @see IconConstants#UPDATE_ICON */
	public static final ImageIcon UPDATE_PRESSED_ICON = createRescaledIcon(UPDATE_ICON, 0.8f);

	/** <img src="../../../resources/icons/cancel16.png"> */
	public static final ImageIcon CANCEL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/cancel16.png"));
	/** @see IconConstants#CANCEL_ICON */
	public static final ImageIcon CANCEL_ROLLOVER_ICON = createRescaledIcon(CANCEL_ICON, 1.2f);
	/** @see IconConstants#CANCEL_ICON */
	public static final ImageIcon CANCEL_PRESSED_ICON = createRescaledIcon(CANCEL_ICON, 0.8f);

	/** <img src="../../../resources/icons/database_save.png"> */
	public static final ImageIcon SAVE_DB_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/database_save.png"));
	/** @see IconConstants#SAVE_DB_ICON */
	public static final ImageIcon SAVE_DB_ROLLOVER_ICON = createRescaledIcon(SAVE_DB_ICON, 1.1f);
	/** @see IconConstants#SAVE_DB_ICON */
	public static final ImageIcon SAVE_DB_PRESSED_ICON = createRescaledIcon(SAVE_DB_ICON, 0.8f);

	/** <img src="../../../resources/icons/database_refresh.png"> */
	public static final ImageIcon REFRESH_DB_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/database_refresh.png"));
	/** @see IconConstants#REFRESH_DB_ICON */
	public static final ImageIcon REFRESH_DB_ROLLOVER_ICON = createRescaledIcon(REFRESH_DB_ICON, 1.1f);
	/** @see IconConstants#REFRESH_DB_ICON */
	public static final ImageIcon REFRESH_DB_PRESSED_ICON = createRescaledIcon(REFRESH_DB_ICON, 0.8f);

	/** <img src="../../../resources/icons/database_delete.png"> */
	public static final ImageIcon DELETE_DB_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/database_delete.png"));
	/** @see IconConstants#DELETE_DB_ICON */
	public static final ImageIcon DELETE_DB_ROLLOVER_ICON = createRescaledIcon(DELETE_DB_ICON, 1.1f);
	/** @see IconConstants#DELETE_DB_ICON */
	public static final ImageIcon DELETE_DB_PRESSED_ICON = createRescaledIcon(DELETE_DB_ICON, 0.8f);

	/** <img src="../../../resources/icons/add_folder16.png"> */
	public static final ImageIcon ADD_FOLDER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/add_folder16.png"));
	/** @see IconConstants#ADD_FOLDER_ICON */
	public static final ImageIcon ADD_FOLDER_ROLLOVER_ICON = createRescaledIcon(ADD_FOLDER_ICON, 1.1f);
	/** @see IconConstants#ADD_FOLDER_ICON */
	public static final ImageIcon ADD_FOLDER_PRESSED_ICON = createRescaledIcon(ADD_FOLDER_ICON, 0.8f);

	/** <img src="../../../resources/icons/view_folder16.png"> */
	public static final ImageIcon VIEW_FOLDER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/view_folder16.png"));
	/** @see IconConstants#VIEW_FOLDER_ICON */
	public static final ImageIcon VIEW_FOLDER_ROLLOVER_ICON = createRescaledIcon(VIEW_FOLDER_ICON, 1.1f);
	/** @see IconConstants#VIEW_FOLDER_ICON */
	public static final ImageIcon VIEW_FOLDER_PRESSED_ICON = createRescaledIcon(VIEW_FOLDER_ICON, 0.8f);

	/** <img src="../../../resources/icons/delete_folder16.png"> */
	public static final ImageIcon DELETE_FOLDER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/delete_folder16.png"));
	/** @see IconConstants#DELETE_FOLDER_ICON */
	public static final ImageIcon DELETE_FOLDER_ROLLOVER_ICON = createRescaledIcon(DELETE_FOLDER_ICON, 1.1f);
	/** @see IconConstants#DELETE_FOLDER_ICON */
	public static final ImageIcon DELETE_FOLDER_PRESSED_ICON = createRescaledIcon(DELETE_FOLDER_ICON, 0.8f);

	/** <img src="../../../resources/icons/add_page16.png"> */
	public static final ImageIcon ADD_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/add_page16.png"));
	/** @see IconConstants#ADD_PAGE_ICON */
	public static final ImageIcon ADD_PAGE_ROLLOVER_ICON = createRescaledIcon(ADD_PAGE_ICON, 1.1f);
	/** @see IconConstants#ADD_PAGE_ICON */
	public static final ImageIcon ADD_PAGE_PRESSED_ICON = createRescaledIcon(ADD_PAGE_ICON, 0.8f);

	/** <img src="../../../resources/icons/view_page16.png"> */
	public static final ImageIcon VIEW_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/view_page16.png"));
	/** @see IconConstants#VIEW_PAGE_ICON */
	public static final ImageIcon VIEW_PAGE_ROLLOVER_ICON = createRescaledIcon(VIEW_PAGE_ICON, 1.1f);
	/** @see IconConstants#VIEW_PAGE_ICON */
	public static final ImageIcon VIEW_PAGE_PRESSED_ICON = createRescaledIcon(VIEW_PAGE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/delete_page16.png"> */
	public static final ImageIcon DELETE_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/delete_page16.png"));
	/** @see IconConstants#DELETE_PAGE_ICON */
	public static final ImageIcon DELETE_PAGE_ROLLOVER_ICON = createRescaledIcon(DELETE_PAGE_ICON, 1.1f);
	/** @see IconConstants#DELETE_PAGE_ICON */
	public static final ImageIcon DELETE_PAGE_PRESSED_ICON = createRescaledIcon(DELETE_PAGE_ICON, 0.8f);
	
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

}
