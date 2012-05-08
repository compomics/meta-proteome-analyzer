package de.mpa.algorithms;

/**
 * Class to hold an one-dimensional interval with inclusive borders. 
 * 
 * @author behne
 */
public class Interval {
	
	private double leftBorder;
	private double rightBorder;
	
	/**
	 * Constructs an interval from a specified <code>leftBorder</code> and <code>rightBorder</code>. 
	 * Will automatically swap borders if <code>leftBorder > rightBorder</code>.
	 * @param leftBorder
	 * @param rightBorder
	 */
	public Interval(double leftBorder, double rightBorder) {
		this.leftBorder = Math.min(leftBorder, rightBorder);
		this.rightBorder = Math.max(leftBorder, rightBorder);
	}
	
	/**
	 * Returns whether the specified value is inside the interval (borders inclusive).
	 * @param value
	 * @return
	 */
	public boolean contains(double value) {
		return ((value >= leftBorder) && (value <= rightBorder));
	}

	/**
	 * @return the leftBorder
	 */
	public double getLeftBorder() {
		return leftBorder;
	}

	/**
	 * @param leftBorder the leftBorder to set
	 */
	public void setLeftBorder(double leftBorder) {
		this.leftBorder = leftBorder;
	}

	/**
	 * @return the rightBorder
	 */
	public double getRightBorder() {
		return rightBorder;
	}

	/**
	 * @param rightBorder the rightBorder to set
	 */
	public void setRightBorder(double rightBorder) {
		this.rightBorder = rightBorder;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return this.rightBorder - this.leftBorder;
	}

}
