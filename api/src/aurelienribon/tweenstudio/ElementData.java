package aurelienribon.tweenstudio;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class ElementData {
	private final Object target;
	private final int tweenType;

	public ElementData(Object target, int tweenType) {
		this.target = target;
		this.tweenType = tweenType;
	}

	public Object getTarget() {
		return target;
	}

	public int getTweenType() {
		return tweenType;
	}
}
