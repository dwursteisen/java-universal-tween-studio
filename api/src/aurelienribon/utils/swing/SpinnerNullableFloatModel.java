package aurelienribon.utils.swing;

import javax.swing.AbstractSpinnerModel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class SpinnerNullableFloatModel extends AbstractSpinnerModel {
	private float min, max, step;
	private Float value;

	public SpinnerNullableFloatModel(float min, float max, float step) {
		this.min = min;
		this.max = max;
		this.step = step;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		if (value != null && !(value instanceof Float)) throw new RuntimeException("value is not a Float");
		this.value = (Float) value;
		fireStateChanged();
	}

	@Override
	public Object getNextValue() {
		return value != null ? Math.min(max, value + step) : getValue();
	}

	@Override
	public Object getPreviousValue() {
		return value != null ? Math.max(min, value - step) : getValue();
	}	
}
