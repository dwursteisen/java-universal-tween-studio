package aurelienribon.tweenstudio;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class ElementData {
	private final Object target;
	private final int id;

	public ElementData(Object target, int tweenType) {
		this.target = target;
		this.id = tweenType;
	}

	public Object getTarget() {
		return target;
	}

	public int getTweenType() {
		return id;
	}
}
