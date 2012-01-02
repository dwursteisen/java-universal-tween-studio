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

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public abstract void initialize();
	public abstract void dispose();
	public abstract void render();
	public abstract void setFileContent(String filepath, String content);
	public abstract String getFileContent(String filepath);

	public List<Property> getProperties(Class clazz) {
		assert propertiesMap.containsKey(clazz);
		return propertiesMap.get(clazz);
	}

	public Property getProperty(Class clazz, int tweenType) {
		List<Property> properties = getProperties(clazz);
		for (Property property : properties)
			if (property.getId() == tweenType)
				return property;
		assert false;
		return null;
	}

	// -------------------------------------------------------------------------
	// Protected API
	// -------------------------------------------------------------------------

	protected void registerProperty(Class clazz, int tweenType, String propertyName, Field... fields) {
		if (!propertiesMap.containsKey(clazz)) propertiesMap.put(clazz, new ArrayList<Property>(5));
		propertiesMap.get(clazz).add(new Property(tweenType, propertyName, fields));
	}

	protected void fireStateChanged(Object target, int tweenType) {
		studio.targetStateChanged(target, tweenType);
	}

	protected void fireStateChanged(Object target, Set<Integer> tweenTypes) {
		studio.targetStateChanged(target, tweenTypes);
	}

	protected List<Object> getRegisteredTargets() {
		return studio.getTargets();
	}

	protected String getRegisteredName(Object target) {
		return studio.getName(target);
	}

	// -------------------------------------------------------------------------
	// Package-protected
	// -------------------------------------------------------------------------

	void setStudio(TweenStudio studio) {
		this.studio = studio;
	}
}
