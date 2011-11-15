package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tweenable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class InitialState {
	private final Map<Integer, float[]> map = new HashMap<Integer, float[]>();

	public InitialState(Editor editor, Tweenable tweenable) {
		for (Property property : editor.getProperties(tweenable.getClass())) {
			int tweenType = property.getId();
			int count = property.getCombinedTweensCount();
			float[] values = new float[count];
			tweenable.getTweenValues(tweenType, values);
			map.put(tweenType, values);
		}
	}

	public float[] getValues(int tweenType) {
		return map.get(tweenType);
	}
}
