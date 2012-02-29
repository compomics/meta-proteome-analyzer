package de.mpa.algorithms;

public class Interval {
	
	private double leftBorder;
	private double rightBorder;
	
	public Interval(double leftBorder, double rightBorder) {
		this.leftBorder = Math.min(leftBorder, rightBorder);
		this.rightBorder = Math.max(leftBorder, rightBorder);
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
