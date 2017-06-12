package aurelienribon.tweenstudio;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
class ElementData {
	private final Object target;
	private final Property property;

	public ElementData(Object target, Property property) {
		this.target = target;
		this.property = property;
	}

	public Object getTarget() {
		return target;
	}

	public Property getProperty() {
		return property;
	}
}
