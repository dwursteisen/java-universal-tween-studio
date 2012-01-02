package aurelienribon.tweenstudio;

import aurelienribon.tweenstudio.Property.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Editor {
	private final Map<Class, List<Property>> propertiesMap = new HashMap<Class, List<Property>>();
	private TweenStudio studio;
	private boolean isUsed = false;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	protected abstract void initializeOverride();
	protected abstract void disposeOverride();
	public abstract void render();
	public abstract void selectedObjectChanged(Object obj);
	public abstract void mouseOverObjectChanged(Object obj);

	public final void initialize() {
		isUsed = true;
		initializeOverride();
	}

	public final void dispose() {
		isUsed = false;
		disposeOverride();
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

	public final boolean isUsed() {
		return isUsed;
	}

	// -------------------------------------------------------------------------
	// Protected API
	// -------------------------------------------------------------------------

	protected final void registerProperty(Class clazz, int tweenType, String propertyName, Field... fields) {
		if (!propertiesMap.containsKey(clazz)) propertiesMap.put(clazz, new ArrayList<Property>(5));
		propertiesMap.get(clazz).add(new Property(tweenType, propertyName, fields));
	}

	protected final void fireStateChanged(Object target, int tweenType) {
		studio.targetStateChanged(target, tweenType);
	}

	protected final void fireStateChanged(Object target, Set<Integer> tweenTypes) {
		studio.targetStateChanged(target, tweenTypes);
	}

	protected final void fireSelectedObjectChanged(Object obj) {
		studio.selectedObjectChanged(obj);
	}

	protected final void fireMouseOverObjectChanged(Object obj) {
		studio.mouseOverObjectChanged(obj);
	}

	protected final List<Object> getRegisteredTargets() {
		return studio.getTargets();
	}

	protected final String getRegisteredName(Object target) {
		return studio.getName(target);
	}

	// -------------------------------------------------------------------------
	// Package-protected
	// -------------------------------------------------------------------------

	void setStudio(TweenStudio studio) {
		this.studio = studio;
	}
}
