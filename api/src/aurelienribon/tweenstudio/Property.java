package aurelienribon.tweenstudio;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Property {
	private final int id;
	private final String name;
	private final Field[] fields;

	public Property(int tweenType, String name, Field... fields) {
		this.id = tweenType;
		this.name = name;
		this.fields = fields;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Field[] getFields() {
		return fields;
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
