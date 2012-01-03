package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.utils.io.FileUtils;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TweenStudio {

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	// Always needed
	private static final Map<String, Timeline> timelinesMap = new HashMap<String, Timeline>(5);
	private static final List<Object> targets = new ArrayList<Object>(5);
	private static final Map<Object, String> targetsNamesMap = new HashMap<Object, String>(5);

	// Only needed in edition mode
	private static Map<String, File> filesMap;
	private static Map<Class<? extends Editor>, Editor> editorsMap;
	private static MainWindow editionWindow;
	private static Editor currentEditor;
	private static String currentAnimationName;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public static boolean isEditionEnabled() {
		return editionWindow != null;
	}

	public static void enableEdition() {
		enableEdition(1000, 500);
	}

	public static void enableEdition(int width, int height) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
		} catch (InstantiationException ex) {
		} catch (IllegalAccessException ex) {
		} catch (UnsupportedLookAndFeelException ex) {
		}

		editionWindow = new MainWindow(new MainWindow.Callback() {
			@Override public void editionComplete() {
				currentAnimationName = null;
				currentEditor.stop();
			}
		});

		editionWindow.setSize(width, height);
		editionWindow.setVisible(true);
		editionWindow.addWindowListener(new WindowAdapter() {
			@Override public void windowClosed(WindowEvent e) {
				editionWindow = null;
			}
		});
	}

	public static <T extends Editor> T getEditor(Class<T> editorClass) {
		if (isEditionEnabled()) {
			if (editorsMap == null) editorsMap = new HashMap<Class<? extends Editor>, Editor>();
			T edt = (T) editorsMap.get(editorClass);
			if (edt == null) {
				try {
					edt = editorClass.newInstance();
					edt.initialize();
					editorsMap.put(editorClass, edt);
				} catch (InstantiationException ex) {throw new RuntimeException(ex.getMessage());
				} catch (IllegalAccessException ex) {throw new RuntimeException(ex.getMessage());
				}
			}
			return edt;
		}
		return null;
	}

	public static void registerTarget(Object target, String name) {
		if (!targets.contains(target)) targets.add(target);
		targetsNamesMap.put(target, name);
	}

	public static void unregisterTarget(Object target) {
		targets.remove(target);
		targetsNamesMap.remove(target);
	}

	public static void unregisterAllTargets() {
		targets.clear();
		targetsNamesMap.clear();
	}

	public static void loadAnimation(File file, String animationName) {
		try {
			String str = FileUtils.readFileToString(file);
			Timeline tl = ImportExportHelper.stringToTimeline(str);
			timelinesMap.put(animationName, tl);
			if (isEditionEnabled()) {
				if (filesMap == null) filesMap = new HashMap<String, File>();
				filesMap.put(animationName, file);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void unloadAnimation(String animationName) {
		timelinesMap.remove(animationName);
		if (filesMap != null) filesMap.remove(animationName);
	}

	public static void unloadAllAnimations() {
		timelinesMap.clear();
		if (filesMap != null) filesMap.clear();
	}

	public static Timeline createTimeline(String animationName) {
		if (!timelinesMap.containsKey(animationName)) throw new RuntimeException(animationName + " is not loaded.");
		if (isEditionEnabled() && currentEditor == null) throw new RuntimeException("No editor was set.");
		
		Timeline timeline = buildTimeline(timelinesMap.get(animationName));
		
		if (isEditionEnabled()) {
			TweenManager.setAutoStart(timeline, false);
			currentAnimationName = animationName;
			currentEditor.start();
			editionWindow.initialize(timeline);
		}

		return timeline;
	}

	public static void setCurrentEditor(Class<? extends Editor> editorClass) {
		currentEditor = getEditor(editorClass);
	}

	public static void update(int deltaMillis) {
		if (currentEditor == null || !currentEditor.isEnabled()) return;
		if (isEditionEnabled()) editionWindow.update(deltaMillis);
	}

	public static void render() {
		if (currentEditor == null || !currentEditor.isEnabled()) return;
		currentEditor.render();
	}

	public static List<Object> getRegisteredTargets() {
		return Collections.unmodifiableList(targets);
	}

	public static String getRegisteredName(Object target) {
		return targetsNamesMap.get(target);
	}

	// -------------------------------------------------------------------------
	// Package API
	// -------------------------------------------------------------------------

	static void targetStateChanged(Object target, Set<Integer> tweenTypes) {
		if (isEditionEnabled()) {
			String name = targetsNamesMap.get(target);
			editionWindow.targetStateChanged(target, name, tweenTypes);
		}
	}

	static void selectedObjectChanged(Object obj) {
		if (isEditionEnabled()) editionWindow.selectedObjectChanged(obj);
	}

	static void mouseOverObjectChanged(Object obj) {
		if (isEditionEnabled()) editionWindow.mouseOverObjectChanged(obj);
	}

	static Map<Object, String> getTargetsNamesMap() {
		return Collections.unmodifiableMap(targetsNamesMap);
	}

	static Editor getCurrentEditor() {
		return currentEditor;
	}

	static File getCurrentAnimationFile() {
		return filesMap.get(currentAnimationName);
	}

	static String getCurrentAnimationName() {
		return currentAnimationName;
	}

	static class DummyTweenAccessor implements TweenAccessor {
		public final String data;
		public DummyTweenAccessor(String data) {this.data = data;}
		@Override public int getValues(Object target, int tweenType, float[] returnValues) {return 0;}
		@Override public void setValues(Object target, int tweenType, float[] newValues) {}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private static Timeline buildTimeline(Timeline loadedTimeline) {
		Timeline tl = Timeline.createParallel();

		for (BaseTween child : loadedTimeline.getChildren()) {
			Tween t = (Tween) child;
			Object target = getTargetFromName(((DummyTweenAccessor)t.getTarget()).data);

			Tween tween = Tween.to(target, t.getType(), t.getDuration())
				.target(t.getTargetValues())
				.ease(t.getEasing())
				.delay(t.getDelay());

			tl.push(tween);
		}

		return tl;
	}

	private static Object getTargetFromName(String name) {
		for (Object target : targets)
			if (getRegisteredName(target).equals(name))
				return target;
		return null;
	}
}
