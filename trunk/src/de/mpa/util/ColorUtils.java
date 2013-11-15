package de.mpa.util;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.ChartColor;

public class ColorUtils {
	
	/* Color constants */
	public static final Color LIGHT_RED = new Color(255, 127, 127);
	public static final Color DARK_RED = new Color(127, 0, 0);
	public static final Color LIGHT_MAGENTA = new Color(255, 127, 255);
	public static final Color DARK_MAGENTA = new Color(127, 0, 127);
	public static final Color LIGHT_BLUE = new Color(127, 127, 255);
	public static final Color DARK_BLUE = new Color(0, 0, 127);
	public static final Color LIGHT_CYAN = new Color(127, 255, 255);
	public static final Color DARK_CYAN = new Color(0, 127, 127); 
	public static final Color LIGHT_GREEN = new Color(127, 255, 127);
	public static final Color DARK_GREEN = new Color(0, 127, 0);
	public static final Color LIGHT_YELLOW = new Color(255, 255, 127);
	public static final Color DARK_YELLOW = new Color(127, 127, 0);
	public static final Color LIGHT_ORANGE = new Color(255, 191, 127);
	public static final Color DARK_ORANGE = new Color(127, 63, 0);
	
	/**
	 * Returns a rainbow color gradient with a parameterized size.
	 * @param size Size of gradient color array
	 * @return Gradient color array
	 */
	public static Color[] getRainbowGradient(int size) {
		Color[] colors = new Color[size];
		// pre-calculate colors
		for (int i = 0; i < size; i++) {
			float r = -Math.abs(i+1 - size*0.75f) * 4.0f/size + 1.5f;
			float g = -Math.abs(i+1 - size*0.50f) * 4.0f/size + 1.5f;
			float b = -Math.abs(i+1 - size*0.25f) * 4.0f/size + 1.5f;
			r = (r > 1.0f) ? 1.0f : (r < 0.0f) ? 0.0f : r;
			g = (g > 1.0f) ? 1.0f : (g < 0.0f) ? 0.0f : g;
			b = (b > 1.0f) ? 1.0f : (b < 0.0f) ? 0.0f : b;
			colors[i] = new Color(r, g, b);
		}
		return colors;
	}

	/**
	 * Returns a color whose brightness has been scaled by the provided factor.
	 * @param color The input color.
	 * @param factor The scale factor.
	 * @return The rescaled color.
	 */
	public static Color getRescaledColor(Color color, float factor) {
		float hsbVals[] = Color.RGBtoHSB(
				color.getRed(), color.getGreen(),
				color.getBlue(), null);
		return Color.getHSBColor(
				hsbVals[0], hsbVals[1], factor * hsbVals[2]);
	}

	/**
	 * Returns the specified color with its alpha intensity changed to the specified value.
	 * @param color the color to change
	 * @param alpha the alpha intensity to use
	 * @return the color with a changed alpha value
	 */
	public static Color getTranslucentColor(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(),
                          color.getBlue(), alpha);
    }
	
	/**
     * Convenience method to return an array of <code>Paint</code> objects that
     * represent the pre-defined colors in the <code>Color</code> and
     * <code>ChartColor</code> objects.<p>
     * Copied from {@link ChartColor}, with a different gray tone.
     *
     * @return An array of objects with the <code>Paint</code> interface.
     */
    public static Paint[] createDefaultPaintArray() {

        return new Paint[] {
            new Color(0xFF, 0x55, 0x55),
            new Color(0x55, 0x55, 0xFF),
            new Color(0x55, 0xFF, 0x55),
            new Color(0xFF, 0xFF, 0x55),
            new Color(0xFF, 0x55, 0xFF),
            new Color(0x55, 0xFF, 0xFF),
            Color.PINK,
            new Color(160, 160, 160),
            ChartColor.DARK_RED,
            ChartColor.DARK_BLUE,
            ChartColor.DARK_GREEN,
            ChartColor.DARK_YELLOW,
            ChartColor.DARK_MAGENTA,
            ChartColor.DARK_CYAN,
            Color.DARK_GRAY,
            ChartColor.LIGHT_RED,
            ChartColor.LIGHT_BLUE,
            ChartColor.LIGHT_GREEN,
            ChartColor.LIGHT_YELLOW,
            ChartColor.LIGHT_MAGENTA,
            ChartColor.LIGHT_CYAN,
            Color.LIGHT_GRAY,
            ChartColor.VERY_DARK_RED,
            ChartColor.VERY_DARK_BLUE,
            ChartColor.VERY_DARK_GREEN,
            ChartColor.VERY_DARK_YELLOW,
            ChartColor.VERY_DARK_MAGENTA,
            ChartColor.VERY_DARK_CYAN,
            ChartColor.VERY_LIGHT_RED,
            ChartColor.VERY_LIGHT_BLUE,
            ChartColor.VERY_LIGHT_GREEN,
            ChartColor.VERY_LIGHT_YELLOW,
            ChartColor.VERY_LIGHT_MAGENTA,
            ChartColor.VERY_LIGHT_CYAN
        };
    }
    
}
