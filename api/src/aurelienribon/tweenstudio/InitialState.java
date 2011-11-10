package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.Tweenable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class InitialState {
	private static final float[] buffer = new float[Tween.MAX_COMBINED_TWEENS];
	private final Map<Integer, float[]> map = new HashMap<Integer, float[]>();

	public InitialState(Editor editor, Tweenable tweenable) {
		for (Property property : editor.getProperties(tweenable.getClass())) {
			int tweenType = property.getId();
			int count = tweenable.getTweenValues(tweenType, buffer);
			float[] values = new float[count];
			System.arraycopy(buffer, 0, values, 0, count);
			map.put(tweenType, values);
		}
	}

	public float[] getValues(int tweenType) {
		return map.get(tweenType);
	}
}
