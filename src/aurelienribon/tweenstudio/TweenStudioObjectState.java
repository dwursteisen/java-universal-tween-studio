package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tween;
import java.util.HashMap;
import java.util.Map;

/**
 * A TSO state is a list of values corresponding to every possible
 * tweenable attribute.
 */
public class TweenStudioObjectState {
    private final Map<Integer, float[]> map;

	/**
	 * Creates a state from an object.
	 */
	public TweenStudioObjectState(TweenStudioObject obj) {
		map = new HashMap<Integer, float[]>();

		for (Integer tweenType : obj.getAvailableTweenTypes()) {
			assert !map.containsKey(tweenType);

			float[] statePart = new float[Tween.MAX_COMBINED_TWEENS];
			obj.getTweenValues(tweenType, statePart);

			map.put(tweenType, statePart);
		}
	}

	/**
	 * Returns true if the state is applicable for such type of tweenable
	 * attribute.
	 */
	public boolean containsValues(int tweenType) {
		return map.containsKey(tweenType);
	}

	/**
	 * Gets the attribute values corresponding to a tween type.
	 * Returns null if the given type is not supported.
	 */
	public float[] getValues(int tweenType) {
		return map.get(tweenType);
	}

	/**
	 * Apply the current state to the given object.
	 */
	public final void applyTo(TweenStudioObject obj) {
		for (Integer tweenType : obj.getAvailableTweenTypes())
			if (containsValues(tweenType))
				obj.tweenUpdated(tweenType, getValues(tweenType));
	}
}
