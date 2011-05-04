package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tweenable;
import java.util.List;

/**
 * A TweenStudioObject represents a Tweenable object that is directly
 * supported in the Tween Studio.
 */
public interface TweenStudioObject extends Tweenable {
	/**
	 * Gets a list of the available tweenable types.
	 */
	public List<Integer> getAvailableTweenTypes();

	/**
	 * Gets the full static name of the tween type.
	 */
	public String getTweenTypeStaticName(int tweenType);

	/**
	 * Gets a human readable description of the tween type.
	 */
	public String getTweenTypeDesc(int tweenType);
}
