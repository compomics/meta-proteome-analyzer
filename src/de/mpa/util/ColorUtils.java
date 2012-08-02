package de.mpa.util;

import java.awt.Color;

public class ColorUtils {
	
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
}
