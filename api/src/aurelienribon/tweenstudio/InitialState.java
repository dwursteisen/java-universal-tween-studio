package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class InitialState {
	private final Map<Integer, float[]> map = new HashMap<Integer, float[]>();

	public InitialState(Editor editor, Object target) {
		for (Property property : editor.getProperties(target.getClass())) {
			int tweenType = property.getId();
			int count = property.getAttributesCount();
			float[] values = new float[count];

			TweenAccessor accessor = Tween.getRegisteredAccessor(target.getClass());
			accessor.getValues(target, tweenType, values);
			
			map.put(tweenType, values);
		}
	}

	public float[] getValues(int tweenType) {
		return map.get(tweenType);
	}
}
