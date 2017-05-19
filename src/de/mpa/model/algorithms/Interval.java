package de.mpa.model.algorithms;

/**
 * Class to hold a one-dimensional interval. 
 * 
 * @author A. Behne
 */
public class Interval {
	
	/**
	 * The lower boundary of the interval.
	 */
	private double leftBorder;
	
	/**
	 * The upper boundary of the interval.
	 */
	private double rightBorder;
	
	/**
	 * A custom user object that is associated with the interval.
	 */
	private Object userObject;
	
	/**
	 * Constructs an interval from a specified <code>leftBorder</code> and <code>rightBorder</code>. 
	 * Will automatically swap borders if <code>leftBorder > rightBorder</code>.
	 * @param leftBorder The interval's lower boundary.
	 * @param rightBorder The interval's upper boundary.
	 */
	public Interval(double leftBorder, double rightBorder) {
		this(leftBorder, rightBorder, null);
	}
	
	/**
	 * Constructs an interval from a specified <code>leftBorder</code> and <code>rightBorder</code> 
	 * and associates it with the provided <code>userObject</code>.
	 * Will automatically swap borders if <code>leftBorder > rightBorder</code>.
	 * @param leftBorder The interval's lower boundary.
	 * @param rightBorder The interval's upper boundary.
	 * @param userObject The object to be associated with the interval.
	 */
	public Interval(double leftBorder, double rightBorder, Object userObject) {
		this.leftBorder = Math.min(leftBorder, rightBorder);
		this.rightBorder = Math.max(leftBorder, rightBorder);
		this.userObject = userObject;
	}

	/**
	 * Returns whether the specified value is inside the interval bounds.
	 * @param value The value to be checked
	 * @param leftInclusive <code>false</code> for strict inequality of upper boundary.
	 * @param rightInclusive <code>false</code> for strict inequality of upper boundary.
	 * @return <code>true</code> if the value is inside the interval, 
	 * <code>false</code> otherwise.
	 */
	public boolean contains(double value, boolean leftInclusive, boolean rightInclusive) {
		boolean res = (leftInclusive) ? (value >= this.leftBorder) : (value > this.leftBorder);
		return res & ((rightInclusive) ? (value <= this.rightBorder) : (value < this.rightBorder));
	}
	
	/**
	 * Returns whether the specified value is inside the interval (borders exclusive).
	 * @param value The value to be checked.
	 * @return <code>true</code> if the value is inside the interval, 
	 * <code>false</code> otherwise.
	 */
	public boolean containsExclusive(double value) {
		return this.contains(value, false, false);
	}

	/**
	 * Returns whether the specified value is inside the interval (borders inclusive).
	 * @param value The value to be checked.
	 * @return <code>true</code> if the value is inside the interval, 
	 * <code>false</code> otherwise.
	 */
	public boolean containsInclusive(double value) {
		return this.contains(value, true, true);
	}

	/**
	 * Returns the interval's width.
	 * @return The interval's width.
	 */
	public double getWidth() {
		return this.rightBorder - this.leftBorder;
	}

	/**
	 * Returns the interval's lower boundary.
	 * @return The interval's lower boundary.
	 */
	public double getLeftBorder() {
		return this.leftBorder;
	}

	/**
	 * Sets the interval's lower boundary.
	 * @param leftBorder The upper boundary value.
	 */
	public void setLeftBorder(double leftBorder) {
		this.leftBorder = leftBorder;
	}

	/**
	 * Returns the interval's upper boundary.
	 * @return The interval's upper boundary.
	 */
	public double getRightBorder() {
		return this.rightBorder;
	}

	/**
	 * Sets the interval's upper boundary.
	 * @param rightBorder The upper boundary value.
	 */
	public void setRightBorder(double rightBorder) {
		this.rightBorder = rightBorder;
	}

	/**
	 * Returns the interval's user object.
	 * @return The interval's user Object.
	 */
	public Object getUserObject() {
		return this.userObject;
	}

	/**
	 * Sets the interval's user object.
	 * @param userObject The object to be associated with the interval.
	 */
	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}
	
	@Override
	public String toString() {
		return "[" + this.leftBorder + ", " + this.rightBorder + "]";
	}

}
