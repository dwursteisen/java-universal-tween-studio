package aurelienribon.tweenstudio;

import aurelienribon.tweenstudio.Property.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Editor {
	private final Map<Class, List<Property>> propertiesMap = new HashMap<Class, List<Property>>();
	private boolean isEnabled = false;

	// -------------------------------------------------------------------------
	// Abstract and overridable stuff
	// -------------------------------------------------------------------------

	public abstract void initialize();
	public void stateChanged(boolean isEnabled) {}
	public void render() {}
	public void selectedObjectChanged(Object obj) {}
	public void mouseOverObjectChanged(Object obj) {}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public final void start() {
		isEnabled = true;
		stateChanged(isEnabled);
	}

	public final void stop() {
		isEnabled = false;
		stateChanged(isEnabled);
	}

	public final List<Property> getProperties(Class clazz) {
		assert propertiesMap.containsKey(clazz);
		return propertiesMap.get(clazz);
	}

	public final Property getProperty(Class clazz, int tweenType) {
		List<Property> properties = getProperties(clazz);
		for (Property property : properties)
			if (property.getId() == tweenType)
				return property;
		assert false;
		return null;
	}

	public final boolean isEnabled() {
		return isEnabled;
	}

	// -------------------------------------------------------------------------
	// Protected API
	// -------------------------------------------------------------------------

	protected final void registerProperty(Class clazz, int tweenType, String propertyName, Field... fields) {
		if (!propertiesMap.containsKey(clazz)) propertiesMap.put(clazz, new ArrayList<Property>(5));
		propertiesMap.get(clazz).add(new Property(tweenType, propertyName, fields));
	}

	protected final void fireStateChanged(Object target, int tweenType) {
		Set<Integer> tweenTypes = new HashSet<Integer>();
		tweenTypes.add(tweenType);
		TweenStudio.targetStateChanged(target, tweenTypes);
	}

	protected final void fireStateChanged(Object target, Set<Integer> tweenTypes) {
		TweenStudio.targetStateChanged(target, tweenTypes);
	}

	protected final void fireSelectedObjectChanged(Object obj) {
		TweenStudio.selectedObjectChanged(obj);
	}

	protected final void fireMouseOverObjectChanged(Object obj) {
		TweenStudio.mouseOverObjectChanged(obj);
	}
}
