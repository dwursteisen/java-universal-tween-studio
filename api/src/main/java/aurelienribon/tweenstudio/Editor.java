package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenstudio.Property.Field;
import aurelienribon.tweenstudio.TweenStudio.AnimationDef;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Editor {
	private final Map<Class, List<Property>> propertiesMap = new LinkedHashMap<Class, List<Property>>();
	private final List<State> changedStates = new ArrayList<State>();
	private AnimationDef animationDef;
	private MainWindow editionWindow;
	private boolean isEnabled = false;

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
	// Package API
	// -------------------------------------------------------------------------

	final void start(AnimationDef animationDef, MainWindow editionWindow) {
		this.animationDef = animationDef;
		this.editionWindow = editionWindow;
		isEnabled = true;
		enable();
	}

	final void stop() {
		isEnabled = false;
		disable();
	}

	final List<Property> getProperties(Object target) {
		List<Property> properties = new ArrayList<Property>();
		for (Class c : propertiesMap.keySet())
			if (c.isInstance(target))
				properties.addAll(propertiesMap.get(c));
		return properties;
	}

	final Property getProperty(Object target, TweenAccessor accessor, int tweenType) {
		List<Property> properties = getProperties(target);
		for (Property p : properties)
			if (p.accessor == accessor && p.tweenType == tweenType)
				return p;
		assert false;
		return null;
	}

	final boolean isEnabled() {
		return isEnabled;
	}

	// -------------------------------------------------------------------------
	// Protected API
	// -------------------------------------------------------------------------

	protected final AnimationDef getAnimationDef() {
		return animationDef;
	}

	protected final void registerProperty(Class clazz, int tweenType, String propertyName, Field... fields) {
		if (Tween.getRegisteredAccessor(clazz) == null) throw new RuntimeException("No accessor was found for the given class");
		if (!propertiesMap.containsKey(clazz)) propertiesMap.put(clazz, new ArrayList<Property>());
		propertiesMap.get(clazz).add(new Property(Tween.getRegisteredAccessor(clazz), tweenType, propertyName, fields));
	}

	protected final void beginReport() {
		changedStates.clear();
	}

	protected final void endReport() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {@Override public void run() {
				editionWindow.statesChanged(changedStates);
			}});
		} catch (InterruptedException ex) {
		} catch (InvocationTargetException ex) {
		}
	}

	protected final void reportStateChanged(Object target, Class targetClass, int tweenType) {
		float[] buffer = new float[Tween.MAX_COMBINED_TWEENS];
		TweenAccessor accessor = Tween.getRegisteredAccessor(targetClass);
		accessor.getValues(target, tweenType, buffer);
		
		changedStates.add(new State(target, targetClass, tweenType, buffer));
	}

	protected final void fireSelectedObjectsChanged(final List objs) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {@Override public void run() {
				editionWindow.selectedObjectsChanged(objs);
			}});
		} catch (InterruptedException ex) {
		} catch (InvocationTargetException ex) {
		}
	}

	protected final void fireMouseOverObjectChanged(final Object obj) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {@Override public void run() {
				editionWindow.mouseOverObjectChanged(obj);
			}});
		} catch (InterruptedException ex) {
		} catch (InvocationTargetException ex) {
		}
	}
}
