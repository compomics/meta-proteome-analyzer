package de.mpa.graphdb.properties;

/**
 * Marker interface for different Frame properties.
 * @author Alex Behne, Thilo Muth
 * @date 2013-01-09
 * @version 0.6.1
 */
public interface ElementProperty {
	
	/**
	 * The generic identifier property.
	 */
	public static final ElementProperty IDENTIFIER = new ElementProperty() {
		@Override
		public String toString() {
			return "Identifier";
		}
	};
	
	@Override
	public String toString();
}
