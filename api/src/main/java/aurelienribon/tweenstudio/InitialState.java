package aurelienribon.tweenstudio;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
class InitialState {
	private final Map<Integer, float[]> map = new HashMap<Integer, float[]>();

	public InitialState(Editor editor, Object target) {
		for (Property property : editor.getProperties(target)) {
			int tweenType = property.tweenType;
			float[] values = new float[property.fields.length];
			property.accessor.getValues(target, tweenType, values);
			map.put(tweenType, values);
		}
	}

	public float[] getValues(int tweenType) {
		return map.get(tweenType);
	}
}
