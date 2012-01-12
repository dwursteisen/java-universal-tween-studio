package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Property {
	public final TweenAccessor accessor;
	public final int tweenType;
	public final String name;
	public final Field[] fields;

	public Property(TweenAccessor accessor, int tweenType, String name, Field... fields) {
		this.accessor = accessor;
		this.tweenType = tweenType;
		this.name = name;
		this.fields = fields;
	}

	// -------------------------------------------------------------------------

	public static class Field {
		public final String name;
		public final float min;
		public final float max;
		public final float step;

		public Field(String name, float step) {
			this.name = name;
			this.min = -9999999;
			this.max = +9999999;
			this.step = step;
		}

		public Field(String name, float min, float max, float step) {
			this.name = name;
			this.min = min;
			this.max = max;
			this.step = step;
		}
	}
}
