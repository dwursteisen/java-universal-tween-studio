package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.Tweenable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Editor {
	private final Map<Class<? extends Tweenable>, List<Property>> propertiesMap = new HashMap<Class<? extends Tweenable>, List<Property>>();
	private TweenStudio studio;
	private TweenManager tweenManager;

	// -------------------------------------------------------------------------
	// Abstract
	// -------------------------------------------------------------------------

	public abstract void initialize();
	public abstract void dispose();
	public abstract void setFileContent(String filepath, String content);
	public abstract String getFileContent(String filepath);

	// -------------------------------------------------------------------------
	// Protected
	// -------------------------------------------------------------------------

	protected void registerProperty(Class<? extends Tweenable> clazz, int tweenType, String propertyName) {
		if (!propertiesMap.containsKey(clazz))
			propertiesMap.put(clazz, new ArrayList<Property>(5));
		propertiesMap.get(clazz).add(new Property(tweenType, propertyName));
	}

	protected void fireStateChanged(Tweenable tweenable, int tweenType) {
		studio.tweenableStateChanged(tweenable, tweenType);
	}

	protected List<Tweenable> getRegisteredTweenables() {
		return studio.getTweenables();
	}

	protected TweenManager getTweenManager() {
		return tweenManager;
	}

	// -------------------------------------------------------------------------
	// Package-protected
	// -------------------------------------------------------------------------

	List<Property> getProperties(Class<? extends Tweenable> clazz) {
		assert propertiesMap.containsKey(clazz);
		return propertiesMap.get(clazz);
	}

	Property getProperty(Class<? extends Tweenable> clazz, int tweenType) {
		List<Property> properties = getProperties(clazz);
		for (Property property : properties)
			if (property.getTweenType() == tweenType)
				return property;
		assert false;
		return null;
	}

	void setStudio(TweenStudio studio) {
		this.studio = studio;
	}

	void setTweenManager(TweenManager tweenManager) {
		this.tweenManager = tweenManager;
	}
}
