package de.mpa.client.ui.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
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
 * @author A. Behne
 */
public class IconConstants {
	
	public static final ImageIcon EMPTY_ICON = new ImageIcon();

	/** <img src="../../../resources/icons/mpa01.png"> */
	public static final ImageIcon MPA_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/mpa01.png"));
	public static final ImageIcon MPA_SMALL_ICON = createRescaledIcon(MPA_ICON, 16, 16);
	
	/** <img src="../../../resources/icons/project48.png"> */
	public static final ImageIcon PROJECT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/project48.png"));
	/** <img src="../../../resources/icons/addspectra.png"> */
	public static final ImageIcon INPUT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/addspectra.png"));
	/** <img src="../../../resources/icons/settings.png"> */
	public static final ImageIcon SETTINGS_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/settings.png"));
	/** <img src="../../../resources/icons/results.png"> */
	public static final ImageIcon RESULTS_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/results.png"));
	/** <img src="../../../resources/icons/compare48.png"> */
	public static final ImageIcon COMPARE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/compare32.png"));
	/** <img src="../../../resources/icons/logging.png"> */
	public static final ImageIcon LOGGING_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/logging.png"));

	/** <img src="../../../resources/icons/exit16.png"> */
	public static final ImageIcon EXIT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/exit16.png"));
	/** <img src="../../../resources/icons/color_wheel16.png"> */
	public static final ImageIcon COLOR_SETTINGS_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/color_wheel16.png"));
	/** <img src="../../../resources/icons/connect16.png"> */
	public static final ImageIcon CONNECT_SMALL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/connect16.png"));
	
	/** <img src="../../../resources/icons/database_connect16.png"> */
	public static final ImageIcon DATABASE_CONNECT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/database_connect16.png"));
	/** <img src="../../../resources/icons/server_connect16.png"> */
	public static final ImageIcon SERVER_CONNECT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/server_connect16.png"));
	
	/** <img src="../../../resources/icons/graph16.png"> */
	public static final ImageIcon GRAPH_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/graph16.png"));
	
	/** <img src="../../../resources/icons/project_add32.png"> */
	public static final ImageIcon PROJECT_ADD_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/project_add32.png"));
	public static final ImageIcon PROJECT_ADD_ROLLOVER_ICON = createColorRescaledIcon(PROJECT_ADD_ICON, 1.1f);
	public static final ImageIcon PROJECT_ADD_PRESSED_ICON = createColorRescaledIcon(PROJECT_ADD_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/project_view32.png"> */
	public static final ImageIcon PROJECT_VIEW_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/project_view32.png"));
	public static final ImageIcon PROJECT_VIEW_ROLLOVER_ICON = createColorRescaledIcon(PROJECT_VIEW_ICON, 1.1f);
	public static final ImageIcon PROJECT_VIEW_PRESSED_ICON = createColorRescaledIcon(PROJECT_VIEW_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/project_delete32.png"> */
	public static final ImageIcon PROJECT_DELETE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/project_delete32.png"));
	public static final ImageIcon PROJECT_DELETE_ROLLOVER_ICON = createColorRescaledIcon(PROJECT_DELETE_ICON, 1.1f);
	public static final ImageIcon PROJECT_DELETE_PRESSED_ICON = createColorRescaledIcon(PROJECT_DELETE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/experiment_add32.png"> */
	public static final ImageIcon EXPERIMENT_ADD_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/experiment_add32.png"));
	public static final ImageIcon EXPERIMENT_ADD_ROLLOVER_ICON = createColorRescaledIcon(EXPERIMENT_ADD_ICON, 1.1f);
	public static final ImageIcon EXPERIMENT_ADD_PRESSED_ICON = createColorRescaledIcon(EXPERIMENT_ADD_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/experiment_view32.png"> */
	public static final ImageIcon EXPERIMENT_VIEW_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/experiment_view32.png"));
	public static final ImageIcon EXPERIMENT_VIEW_ROLLOVER_ICON = createColorRescaledIcon(EXPERIMENT_VIEW_ICON, 1.1f);
	public static final ImageIcon EXPERIMENT_VIEW_PRESSED_ICON = createColorRescaledIcon(EXPERIMENT_VIEW_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/experiment_delete32.png"> */
	public static final ImageIcon EXPERIMENT_DELETE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/experiment_delete32.png"));
	public static final ImageIcon EXPERIMENT_DELETE_ROLLOVER_ICON = createColorRescaledIcon(EXPERIMENT_DELETE_ICON, 1.1f);
	public static final ImageIcon EXPERIMENT_DELETE_PRESSED_ICON = createColorRescaledIcon(EXPERIMENT_DELETE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/zoom16.png"> */
	public static final ImageIcon ZOOM_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/zoom16.png"));
	public static final ImageIcon ZOOM_ROLLOVER_ICON = createColorRescaledIcon(ZOOM_ICON, 1.1f);
	public static final ImageIcon ZOOM_PRESSED_ICON = createColorRescaledIcon(ZOOM_ICON, 0.8f);

	/** <img src="../../../resources/icons/results_fetch32.png"> */
	public static final ImageIcon RESULTS_FETCH_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/results_fetch32.png"));
	public static final ImageIcon RESULTS_FETCH_ROLLOVER_ICON = createColorRescaledIcon(RESULTS_FETCH_ICON, 1.1f);
	public static final ImageIcon RESULTS_FETCH_PRESSED_ICON = createColorRescaledIcon(RESULTS_FETCH_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/results_process32.png"> */
	public static final ImageIcon RESULTS_PROCESS_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/results_process32.png"));
	public static final ImageIcon RESULTS_PROCESS_ROLLOVER_ICON = createColorRescaledIcon(RESULTS_PROCESS_ICON, 1.1f);
	public static final ImageIcon RESULTS_PROCESS_PRESSED_ICON = createColorRescaledIcon(RESULTS_PROCESS_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/settings16.png"> */
	public static final ImageIcon SETTINGS_SMALL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/settings16.png"));
	public static final ImageIcon SETTINGS_SMALL_ROLLOVER_ICON = createColorRescaledIcon(SETTINGS_SMALL_ICON, 1.1f);
	public static final ImageIcon SETTINGS_SMALL_PRESSED_ICON = createColorRescaledIcon(SETTINGS_SMALL_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/disconnect32.png"> */
	public static final ImageIcon DISCONNECT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/disconnect32.png"));
	public static final ImageIcon DISCONNECT_ROLLOVER_ICON = createColorRescaledIcon(DISCONNECT_ICON, 1.2f);
	public static final ImageIcon DISCONNECT_PRESSED_ICON = createColorRescaledIcon(DISCONNECT_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/connect32.png"> */
	public static final ImageIcon CONNECT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/connect32.png"));
	public static final ImageIcon CONNECT_ROLLOVER_ICON = createColorRescaledIcon(CONNECT_ICON, 1.2f);
	public static final ImageIcon CONNECT_PRESSED_ICON = createColorRescaledIcon(CONNECT_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/lightning32.png"> */
	public static final ImageIcon LIGHTNING_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/lightning32.png"));
	public static final ImageIcon LIGHTNING_ROLLOVER_ICON = createColorRescaledIcon(LIGHTNING_ICON, 1.2f);
	public static final ImageIcon LIGHTNING_PRESSED_ICON = createColorRescaledIcon(LIGHTNING_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/search32.png"> */
	public static final ImageIcon SEARCH_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/search32.png"));
	public static final ImageIcon SEARCH_ROLLOVER_ICON = createColorRescaledIcon(SEARCH_ICON, 1.2f);
	public static final ImageIcon SEARCH_PRESSED_ICON = createColorRescaledIcon(SEARCH_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/bug32.png"> */
	public static final ImageIcon BUG_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/bug32.png"));
	public static final ImageIcon BUG_ROLLOVER_ICON = createColorRescaledIcon(BUG_ICON, 1.1f);
	public static final ImageIcon BUG_PRESSED_ICON = createColorRescaledIcon(BUG_ICON, 0.8f);

	/** <img src="../../../resources/icons/bug16.png"> */
	public static final ImageIcon BUG_SMALL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/bug16.png"));
	public static final ImageIcon BUG_SMALL_ROLLOVER_ICON = createColorRescaledIcon(BUG_SMALL_ICON, 1.1f);
	public static final ImageIcon BUG_SMALL_PRESSED_ICON = createColorRescaledIcon(BUG_SMALL_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/www13.png"> */
	public static final ImageIcon WWW_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/www13.png")) {
		public String toString() { return "www"; };
	};
	
	/** <img src="../../../resources/icons/sort16.png"> */
	public static final ImageIcon SORT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/sort16.png"));
//	public static final ImageIcon SORT_ROLLOVER_ICON = createRescaledIcon(SORT_ICON, 1.1f);
//	public static final ImageIcon SORT_PRESSED_ICON = createRescaledIcon(SORT_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/filter16.png"> */
	public static final ImageIcon FILTER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/filter16.png"));
	public static final ImageIcon FILTER_ROLLOVER_ICON = createColorRescaledIcon(FILTER_ICON, 1.1f);
	public static final ImageIcon FILTER_PRESSED_ICON = createColorRescaledIcon(FILTER_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/calculator16.png"> */
	public static final ImageIcon CALCULATOR_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/calculator16.png"));
//	public static final ImageIcon CALCULATOR_ROLLOVER_ICON = createRescaledIcon(CALCULATOR_ICON, 1.1f);
//	public static final ImageIcon CALCULATOR_PRESSED_ICON = createRescaledIcon(CALCULATOR_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/expand_all16.png"> */
	public static final ImageIcon EXPAND_ALL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/expand_all16.png"));
	public static final ImageIcon EXPAND_ALL_ROLLOVER_ICON = createColorRescaledIcon(EXPAND_ALL_ICON, 1.1f);
	public static final ImageIcon EXPAND_ALL_PRESSED_ICON = createColorRescaledIcon(EXPAND_ALL_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/collapse_all16.png"> */
	public static final ImageIcon COLLAPSE_ALL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/collapse_all16.png"));
	public static final ImageIcon COLLAPSE_ALL_ROLLOVER_ICON = createColorRescaledIcon(COLLAPSE_ALL_ICON, 1.1f);
	public static final ImageIcon COLLAPSE_ALL_PRESSED_ICON = createColorRescaledIcon(COLLAPSE_ALL_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/checkbox_table16.png"> */
	public static final ImageIcon CHECKBOX_TABLE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/checkbox_table16.png"));
	public static final ImageIcon CHECKBOX_TABLE_ROLLOVER_ICON = createColorRescaledIcon(CHECKBOX_TABLE_ICON, 1.1f);
	public static final ImageIcon CHECKBOX_TABLE_PRESSED_ICON = createColorRescaledIcon(CHECKBOX_TABLE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/coverage16.png"> */
	public static final ImageIcon COVERAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/coverage16.png"));
	public static final ImageIcon COVERAGE_ROLLOVER_ICON = createColorRescaledIcon(COVERAGE_ICON, 1.1f);
	public static final ImageIcon COVERAGE_PRESSED_ICON = createColorRescaledIcon(COVERAGE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/spectrum16.png"> */
	public static final ImageIcon SPECTRUM_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/spectrum16.png"));
	public static final ImageIcon SPECTRUM_ROLLOVER_ICON = createColorRescaledIcon(SPECTRUM_ICON, 1.1f);
	public static final ImageIcon SPECTRUM_PRESSED_ICON = createColorRescaledIcon(SPECTRUM_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/check16.png"> */
	public static final ImageIcon CHECK_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/check16.png"));
	public static final ImageIcon CHECK_ROLLOVER_ICON = createColorRescaledIcon(CHECK_ICON, 1.1f);
	public static final ImageIcon CHECK_PRESSED_ICON = createColorRescaledIcon(CHECK_ICON, 0.8f);

	/** <img src="../../../resources/icons/cross16.png"> */
	public static final ImageIcon CROSS_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/cross16.png"));
	public static final ImageIcon CROSS_ROLLOVER_ICON = createColorRescaledIcon(CROSS_ICON, 1.1f);
	public static final ImageIcon CROSS_PRESSED_ICON = createColorRescaledIcon(CROSS_ICON, 0.8f);

	/** <img src="../../../resources/icons/cross_small.png"> */
	public static final ImageIcon CROSS_SMALL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/cross_small.png"));
	public static final ImageIcon CROSS_SMALL_ROLLOVER_ICON = createColorRescaledIcon(CROSS_SMALL_ICON, 1.1f);
	public static final ImageIcon CROSS_SMALL_PRESSED_ICON = createColorRescaledIcon(CROSS_SMALL_ICON, 0.8f);

	/** <img src="../../../resources/icons/bulb_off16.png"> */
	public static final ImageIcon HELP_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/bulb_off16.png"));
	public static final ImageIcon HELP_ROLLOVER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/bulb16.png"));

	/** <img src="../../../resources/icons/add16.png"> */
	public static final ImageIcon ADD_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/add16.png"));
	public static final ImageIcon ADD_ROLLOVER_ICON = createColorRescaledIcon(ADD_ICON, 1.1f);
	public static final ImageIcon ADD_PRESSED_ICON = createColorRescaledIcon(ADD_ICON, 0.8f);

	/** <img src="../../../resources/icons/delete16.png"> */
	public static final ImageIcon DELETE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/delete16.png"));
	public static final ImageIcon DELETE_ROLLOVER_ICON = createColorRescaledIcon(DELETE_ICON, 1.1f);
	public static final ImageIcon DELETE_PRESSED_ICON = createColorRescaledIcon(DELETE_ICON, 0.8f);

	/** <img src="../../../resources/icons/plugin16.png"> */
	public static final ImageIcon PLUGIN_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/plugin16.png"));
	public static final ImageIcon PLUGIN_ROLLOVER_ICON = createColorRescaledIcon(PLUGIN_ICON, 1.1f);
	public static final ImageIcon PLUGIN_PRESSED_ICON = createColorRescaledIcon(PLUGIN_ICON, 0.8f);

	/** <img src="../../../resources/icons/plugin_red16.png"> */
	public static final ImageIcon PLUGIN_RED_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/plugin_red16.png"));
	/** <img src="../../../resources/icons/plugin_purple16.png"> */
	public static final ImageIcon PLUGIN_PURPLE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/plugin_purple16.png"));
	/** <img src="../../../resources/icons/plugin_blue16.png"> */
	public static final ImageIcon PLUGIN_BLUE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/plugin_blue16.png"));

	/** <img src="../../../resources/icons/textfield16.png"> */
	public static final ImageIcon TEXTFIELD_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/textfield16.png"));
	public static final ImageIcon TEXTFIELD_ROLLOVER_ICON = createColorRescaledIcon(TEXTFIELD_ICON, 1.1f);
	public static final ImageIcon TEXTFIELD_PRESSED_ICON = createColorRescaledIcon(TEXTFIELD_ICON, 0.8f);

	/** <img src="../../../resources/icons/update16.png"> */
	public static final ImageIcon UPDATE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/update16.png"));
	public static final ImageIcon UPDATE_ROLLOVER_ICON = createColorRescaledIcon(UPDATE_ICON, 1.1f);
	public static final ImageIcon UPDATE_PRESSED_ICON = createColorRescaledIcon(UPDATE_ICON, 0.8f);

	/** <img src="../../../resources/icons/cancel16.png"> */
	public static final ImageIcon CANCEL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/cancel16.png"));
	public static final ImageIcon CANCEL_ROLLOVER_ICON = createColorRescaledIcon(CANCEL_ICON, 1.2f);
	public static final ImageIcon CANCEL_PRESSED_ICON = createColorRescaledIcon(CANCEL_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/save16.png"> */
	public static final ImageIcon SAVE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/save16.png"));
	public static final ImageIcon SAVE_ROLLOVER_ICON = createColorRescaledIcon(SAVE_ICON, 1.2f);
	public static final ImageIcon SAVE_PRESSED_ICON = createColorRescaledIcon(SAVE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/database_save.png"> */
	public static final ImageIcon SAVE_DB_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/database_save.png"));
	public static final ImageIcon SAVE_DB_ROLLOVER_ICON = createColorRescaledIcon(SAVE_DB_ICON, 1.1f);
	public static final ImageIcon SAVE_DB_PRESSED_ICON = createColorRescaledIcon(SAVE_DB_ICON, 0.8f);

	/** <img src="../../../resources/icons/database_go16.png"> */
	public static final ImageIcon GO_DB_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/database_go16.png"));
	public static final ImageIcon GO_DB_ROLLOVER_ICON = createColorRescaledIcon(GO_DB_ICON, 1.1f);
	public static final ImageIcon GO_DB_PRESSED_ICON = createColorRescaledIcon(GO_DB_ICON, 0.8f);

	/** <img src="../../../resources/icons/database_delete.png"> */
	public static final ImageIcon DELETE_DB_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/database_delete.png"));
	public static final ImageIcon DELETE_DB_ROLLOVER_ICON = createColorRescaledIcon(DELETE_DB_ICON, 1.1f);
	public static final ImageIcon DELETE_DB_PRESSED_ICON = createColorRescaledIcon(DELETE_DB_ICON, 0.8f);

	/** <img src="../../../resources/icons/add_folder16.png"> */
	public static final ImageIcon ADD_FOLDER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/add_folder16.png"));
	public static final ImageIcon ADD_FOLDER_ROLLOVER_ICON = createColorRescaledIcon(ADD_FOLDER_ICON, 1.1f);
	public static final ImageIcon ADD_FOLDER_PRESSED_ICON = createColorRescaledIcon(ADD_FOLDER_ICON, 0.8f);

	/** <img src="../../../resources/icons/view_folder16.png"> */
	public static final ImageIcon VIEW_FOLDER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/view_folder16.png"));
	public static final ImageIcon VIEW_FOLDER_ROLLOVER_ICON = createColorRescaledIcon(VIEW_FOLDER_ICON, 1.1f);
	public static final ImageIcon VIEW_FOLDER_PRESSED_ICON = createColorRescaledIcon(VIEW_FOLDER_ICON, 0.8f);

	/** <img src="../../../resources/icons/delete_folder16.png"> */
	public static final ImageIcon DELETE_FOLDER_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/delete_folder16.png"));
	public static final ImageIcon DELETE_FOLDER_ROLLOVER_ICON = createColorRescaledIcon(DELETE_FOLDER_ICON, 1.1f);
	public static final ImageIcon DELETE_FOLDER_PRESSED_ICON = createColorRescaledIcon(DELETE_FOLDER_ICON, 0.8f);

	/** <img src="../../../resources/icons/add_page16.png"> */
	public static final ImageIcon ADD_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/add_page16.png"));
	public static final ImageIcon ADD_PAGE_ROLLOVER_ICON = createColorRescaledIcon(ADD_PAGE_ICON, 1.1f);
	public static final ImageIcon ADD_PAGE_PRESSED_ICON = createColorRescaledIcon(ADD_PAGE_ICON, 0.8f);

	/** <img src="../../../resources/icons/view_page16.png"> */
	public static final ImageIcon VIEW_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/view_page16.png"));
	public static final ImageIcon VIEW_PAGE_ROLLOVER_ICON = createColorRescaledIcon(VIEW_PAGE_ICON, 1.1f);
	public static final ImageIcon VIEW_PAGE_PRESSED_ICON = createColorRescaledIcon(VIEW_PAGE_ICON, 0.8f);

	/** <img src="../../../resources/icons/database_refresh.png"> */
	public static final ImageIcon GO_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/page_go16.png"));
	public static final ImageIcon GO_PAGE_ROLLOVER_ICON = createColorRescaledIcon(GO_PAGE_ICON, 1.1f);
	public static final ImageIcon GO_PAGE_PRESSED_ICON = createColorRescaledIcon(GO_PAGE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/delete_page16.png"> */
	public static final ImageIcon DELETE_PAGE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/delete_page16.png"));
	public static final ImageIcon DELETE_PAGE_ROLLOVER_ICON = createColorRescaledIcon(DELETE_PAGE_ICON, 1.1f);
	public static final ImageIcon DELETE_PAGE_PRESSED_ICON = createColorRescaledIcon(DELETE_PAGE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/next.png"> */
	public static final ImageIcon NEXT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/next.png"));
	public static final ImageIcon NEXT_ROLLOVER_ICON = createColorRescaledIcon(NEXT_ICON, 1.2f);
	public static final ImageIcon NEXT_PRESSED_ICON = createColorRescaledIcon(NEXT_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/next.png"> (flipped horizontally)*/
	public static final ImageIcon PREV_ICON = createFlippedIcon(NEXT_ICON, SwingConstants.HORIZONTAL);
	public static final ImageIcon PREV_ROLLOVER_ICON = createColorRescaledIcon(PREV_ICON, 1.2f);
	public static final ImageIcon PREV_PRESSED_ICON = createColorRescaledIcon(PREV_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/skip32.png"> */
	public static final ImageIcon SKIP_ICON = createRotatedIcon(NEXT_ICON, Math.PI / 2.0);
	public static final ImageIcon SKIP_ROLLOVER_ICON = createColorRescaledIcon(SKIP_ICON, 1.2f);
	public static final ImageIcon SKIP_PRESSED_ICON = createColorRescaledIcon(SKIP_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/size_vertical_32.png"> */
	public static final ImageIcon SIZE_VERT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/size_vertical_32.png"));
	public static final ImageIcon SIZE_VERT_ROLLOVER_ICON = createColorRescaledIcon(SIZE_VERT_ICON, 1.2f);
	public static final ImageIcon SIZE_VERT_PRESSED_ICON = createColorRescaledIcon(SIZE_VERT_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/hierarchy16.png"> */
	public static final ImageIcon HIERARCHY_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/hierarchy16.png"));
	public static final ImageIcon HIERARCHY_ROLLOVER_ICON = createColorRescaledIcon(HIERARCHY_ICON, 1.2f);
	public static final ImageIcon HIERARCHY_PRESSED_ICON = createColorRescaledIcon(HIERARCHY_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/pie_chart16.png"> */
	public static final ImageIcon PIE_CHART_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/pie_chart16.png"));
	public static final ImageIcon PIE_CHART_ROLLOVER_ICON = createColorRescaledIcon(PIE_CHART_ICON, 1.2f);
	public static final ImageIcon PIE_CHART_PRESSED_ICON = createColorRescaledIcon(PIE_CHART_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/bar_chart16.png"> */
	public static final ImageIcon BAR_CHART_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/bar_chart16.png"));
	public static final ImageIcon BAR_CHART_ROLLOVER_ICON = createColorRescaledIcon(BAR_CHART_ICON, 1.2f);
	public static final ImageIcon BAR_CHART_PRESSED_ICON = createColorRescaledIcon(BAR_CHART_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/frame_full16.png"> */
	public static final ImageIcon FRAME_FULL_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/frame_full16.png"));
	public static final ImageIcon FRAME_FULL_ROLLOVER_ICON = createColorRescaledIcon(FRAME_FULL_ICON, 1.2f);
	public static final ImageIcon FRAME_FULL_PRESSED_ICON = createColorRescaledIcon(FRAME_FULL_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/frame_tiled16.png"> */
	public static final ImageIcon FRAME_TILED_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/frame_tiled16.png"));
	public static final ImageIcon FRAME_TILED_ROLLOVER_ICON = createColorRescaledIcon(FRAME_TILED_ICON, 1.2f);
	public static final ImageIcon FRAME_TILED_PRESSED_ICON = createColorRescaledIcon(FRAME_TILED_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/page_save32.png"> */
	public static final ImageIcon SAVE_FILE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/page_save32.png"));
	public static final ImageIcon SAVE_FILE_ROLLOVER_ICON = createColorRescaledIcon(SAVE_FILE_ICON, 1.2f);
	public static final ImageIcon SAVE_FILE_PRESSED_ICON = createColorRescaledIcon(SAVE_FILE_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/file_csv16.png"> */
	public static final ImageIcon FILE_CSV = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/file_csv16.png"));

	/** <img src="../../../resources/icons/protein.png"> */
	public static final ImageIcon PROTEIN_TREE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/protein.png"));
	/** <img src="../../../resources/icons/metaprotein.png"> */
	public static final ImageIcon METAPROTEIN_TREE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/metaprotein.png"));

	/** <img src="../../../resources/icons/excel_export16.png"> */
	public static final ImageIcon EXCEL_EXPORT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/excel_export16.png"));
	public static final ImageIcon EXCEL_EXPORT_ROLLOVER_ICON = createColorRescaledIcon(EXCEL_EXPORT_ICON, 1.1f);
	public static final ImageIcon EXCEL_EXPORT_PRESSED_ICON = createColorRescaledIcon(EXCEL_EXPORT_ICON, 0.8f);
	
	/** <img src="../../../resources/icons/webresource16.png"> */
	public static final ImageIcon WEB_RESOURCE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/webresource16.png"));
	/** <img src="../../../resources/icons/ncbi16.png"> */
	public static final ImageIcon WEB_NCBI_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/ncbi16.png"));
	/** <img src="../../../resources/icons/uniprot16.png"> */
	public static final ImageIcon WEB_UNIPROT_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/uniprot16.png"));
	/** <img src="../../../resources/icons/pfam16.png"> */
	public static final ImageIcon WEB_PFAM_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/pfam16.png"));
	/** <img src="../../../resources/icons/interpro16.png"> */
	public static final ImageIcon WEB_INTERPRO_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/interpro16.png"));
	/** <img src="../../../resources/icons/pdb16.png"> */
	public static final ImageIcon WEB_PDB_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/pdb16.png"));
	/** <img src="../../../resources/icons/pride16.png"> */
	public static final ImageIcon WEB_PRIDE_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/pride16.png"));
	/** <img src="../../../resources/icons/reactome16.png"> */
	public static final ImageIcon WEB_REACTOME_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/reactome16.png"));
	/** <img src="../../../resources/icons/kegg16.png"> */
	public static final ImageIcon WEB_KEGG_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/kegg16.png"));
	/** <img src="../../../resources/icons/blast16.png"> */
	public static final Icon WEB_BLAST_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/blast16.png"));;
	/** <img src="../../../resources/icons/quickgo16.png"> */
	public static final Icon WEB_QUICKGO_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/quickgo16.png"));;
	/** <img src="../../../resources/icons/eggnog16.png"> */
	public static final Icon WEB_EGGNOG_ICON = new ImageIcon(IconConstants.class.getResource("/de/mpa/resources/icons/eggnog16.png"));;
	
	/**
	 * Creates an empty item of the specified width and height.
	 * @param width the pixel width of the icon
	 * @param height the pixel height of the icon
	 * @return an empty icon
	 */
	public static Icon createEmptyIcon(final int width, final int height) {
		return new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
			}
			@Override
			public int getIconWidth() {
				return width;
			}
			@Override
			public int getIconHeight() {
				return height;
			}
		};
	}
	
	/**
	 * Rescales the color space of the provided icon. Use to brighten or darken icons.
	 * @param icon he icon to be rescaled
	 * @param scale the scale factor to be used. Below 1.0 will darken, above 1.0 will brighten the image.
	 * @return the icon with rescaled color space
	 */
	public static ImageIcon createColorRescaledIcon(Icon icon, float scale) {
		
		// transfer image data to buffered image
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		
		// re-scale pixel intensities
		float[] factors = new float[] { scale, scale, scale, 1.0f };
		float[] offsets = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
		RescaleOp op = new RescaleOp(factors, offsets, null);
		
		return new ImageIcon(op.filter(bi, null));
	}
	
	/**
	 * Creates an icon by rescaling the image of the provided icon using the specified uniform scale factor.
	 * @param icon the icon to rescale
	 * @param scale the scale factor
	 * @return a rescaled icon
	 */
	public static ImageIcon createRescaledIcon(ImageIcon icon, float scale) {
		return createRescaledIcon(icon, scale, scale);
	}
	
	/**
	 * Creates an icon by rescaling the image of the provided icon using the
	 * specified horizontal and vertical axis scale factors.
	 * @param icon the icon to rescale
	 * @param xScale the horizontal scale factor
	 * @param yScale the vertical scale factor
	 * @return a rescaled icon
	 */
	public static ImageIcon createRescaledIcon(ImageIcon icon, float xScale, float yScale) {
		return createRescaledIcon(icon, icon.getIconWidth() * xScale, icon.getIconHeight() * yScale);
	}
	
	/**
	 * Creates an icon by rescaling the image of the provided icon using he
	 * specified image dimensions.
	 * @param icon the icon to rescale
	 * @param width the desired icon width
	 * @param height the desired icon height
	 * @return a rescaled icon
	 */
	public static ImageIcon createRescaledIcon(ImageIcon icon, int width, int height) {
		Image image = icon.getImage();
		
		// transfer image data to buffered image
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g.drawImage(image, 0, 0, width, height, null, null);
		g.dispose();
		
		return new ImageIcon(bi);
	}
	
	/**
	 * Flips the provided icon along the specified axis.
	 * @param icon the icon to be flipped
	 * @param orientation either {@link SwingConstants.HORIZONTAL} or {@link SwingConstants.VERTICAL}
	 * @return a flipped icon
	 */
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
	
	/**
	 * Creates an icon by rotating the provided icon's image by the specified angle.
	 * @param icon the icon to rotate
	 * @param theta the angle, positive values rotate in a clockwise fashion
	 * @return a rotated icon
	 */
	public static ImageIcon createRotatedIcon(ImageIcon icon, double theta) {

		// transfer image data to buffered image
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		
		// rotate icon around its center
		double centerX = bi.getWidth() / 2.0;
		double centerY = bi.getHeight() / 2.0;
		AffineTransform tx = AffineTransform.getRotateInstance(theta, centerX, centerY);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		
		return new ImageIcon(op.filter(bi, null));
	}

	/**
	 * Creates an icon with an additional downward pointing triangle to the
	 * right of it.
	 * @param icon the icon
	 * @return an icon with additional arrow
	 */
	public static Icon createArrowedIcon(Icon icon) {
		return createArrowedIcon(icon, SwingConstants.EAST);
	}

	/**
	 * Creates an icon with an additional downward pointing triangle at the
	 * specified relative location.
	 * 
	 * @param icon the icon
	 * @param location the location of the arrow
	 * @return an icon with additional arrow
	 */
	public static Icon createArrowedIcon(Icon icon, int location) {
		return createArrowedIcon(icon, location, SwingConstants.SOUTH);
	}

	/**
	 * Creates an icon with an additional triangle at the specified relative
	 * location pointing in the specified direction.
	 * 
	 * @param icon the icon
	 * @param location the location of the arrow
	 * @param direction the direction the arrow is pointing in
	 * @return an icon with additional arrow
	 */
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
