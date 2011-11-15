package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tweenable;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class ElementData {
	private final Tweenable tweenable;
	private final int tweenType;

	public ElementData(Tweenable tweenable, int tweenType) {
		this.tweenable = tweenable;
		this.tweenType = tweenType;
	}

	public Tweenable getTweenable() {
		return tweenable;
	}

	public int getTweenType() {
		return tweenType;
	}
}
