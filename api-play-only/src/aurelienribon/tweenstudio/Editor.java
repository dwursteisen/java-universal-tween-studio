package aurelienribon.tweenstudio;

import aurelienribon.tweenstudio.Property.Field;
import aurelienribon.tweenstudio.TweenStudio.AnimationDef;
import java.util.List;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Editor {

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public abstract void initialize();
	public void enable() {}
	public void disable() {}
	public void render() {}
	public void selectedObjectsChanged(List<Object> objs) {}
	public void mouseOverObjectChanged(Object obj) {}

	// -------------------------------------------------------------------------
	// Protected API
	// -------------------------------------------------------------------------

	protected final AnimationDef getAnimationDef() {
		return null;
	}

	protected final void registerProperty(Class clazz, int tweenType, String propertyName, Field... fields) {
	}

	protected final void beginReport() {
	}

	protected final void endReport() {
	}

	protected final void reportStateChanged(Object target, Class targetClass, int tweenType) {
	}

	protected final void fireSelectedObjectsChanged(final List objs) {
	}

	protected final void fireMouseOverObjectChanged(final Object obj) {
	}
}
