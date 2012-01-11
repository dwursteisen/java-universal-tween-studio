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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The Universal Tween Studio features multiple static calls to let you either
 * edit or play your animations directly in your applications.
 * <br/><br/>
 *
 * A usage example could be as follows:
 * <br/><br/>
 *
 * public class MyGame {
 *     public static void main(String[] args) {
 *         TweenStudio.enableEdition();
 *         << start your game >>
 *     }
 *
 *     public MyGame() {
 *         TweenStudio.loadAnimation(new File("data/anim1.timeline"), "My title animation");
 *
 *         << load and create your objects >>
 *
 *         TweenStudio.registerEditor(MyTweenStudioEditor.class);
 *         TweenStudio.registerTarget(sprites[0], "Player");
 *         TweenStudio.registerTarget(sprites[1], "Enemy");
 *         TweenStudio.registerTarget(sprites[2], "Ground");
 *         TweenStudio.registerTarget(sprites[3], "Sun");
 *
 *         TweenStudio.createTimeline("My title animation").start(tweenManager);
 *     }
 *
 *     public void update(int deltaTime) {
 *         TweenStudio.update(deltaTime);
 *         tweenManager.update(deltaTime);
 *         << update your game >>
 *     }
 *
 *     public void render() {
 *         << render your game >>
 *         TweenStudio.render();
 *     }
 * }
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TweenStudio {
	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	private static final Map<String, Timeline> timelinesMap = new HashMap<String, Timeline>(5);
	private static final List<Object> nextTargets = new ArrayList<Object>(5);
	private static final Map<Object, String> nextTargetsNamesMap = new HashMap<Object, String>(5);

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public static boolean isEditionEnabled() {
		return false;
	}

	public static void enableEdition() {
	}

	public static void enableEdition(final int width, final int height) {
	}

	public static void loadAnimation(File animationFile, String animationName) {
		try {
			String str = FileUtils.readFileToString(animationFile);
			Timeline tl = ImportExportHelper.stringToTimeline(str);
			timelinesMap.put(animationName, tl);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void unloadAnimation(String animationName) {
		timelinesMap.remove(animationName);
	}

	public static void unloadAllAnimations() {
		timelinesMap.clear();
	}

	public static <T extends Editor> T getEditor(Class<T> editorClass) {
		return null;
	}

	public static void registerEditor(Class<? extends Editor> editorClass) {
	}

	public static void registerTarget(Object target, String name) {
		if (!nextTargets.contains(target)) nextTargets.add(target);
		nextTargetsNamesMap.put(target, name);
	}

	public static void unregisterTarget(Object target) {
		nextTargets.remove(target);
		nextTargetsNamesMap.remove(target);
	}

	public static void unregisterAllTargets() {
		nextTargets.clear();
		nextTargetsNamesMap.clear();
	}

	public static Timeline createTimeline(String animationName) {
		if (!timelinesMap.containsKey(animationName)) throw new RuntimeException(animationName + " is not loaded.");
		Timeline timeline = buildTimeline(timelinesMap.get(animationName), nextTargetsNamesMap);
		timeline.start();
		return timeline;
	}

	public static void update(final int deltaMillis) {
	}

	public static void render() {
	}

	// -------------------------------------------------------------------------
	// Package API
	// -------------------------------------------------------------------------

	static void targetStateChanged(final Object target, final Set<Integer> tweenTypes) {
	}

	static void selectedObjectChanged(final Object obj) {
	}

	static void mouseOverObjectChanged(final Object obj) {
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

	private static Timeline buildTimeline(Timeline loadedTimeline, Map<Object, String> targetsNamesMap) {
		Timeline tl = Timeline.createParallel();

		for (BaseTween child : loadedTimeline.getChildren()) {
			Tween t = (Tween) child;
			Object target = getTargetFromName(((DummyTweenAccessor)t.getTarget()).data, targetsNamesMap);

			Tween tween = Tween.to(target, t.getType(), t.getDuration())
				.target(t.getTargetValues())
				.ease(t.getEasing())
				.delay(t.getDelay());

			tl.push(tween);
		}

		return tl;
	}

	private static Object getTargetFromName(String name, Map<Object, String> targetsNamesMap) {
		for (Object target : nextTargets)
			if (name.equals(targetsNamesMap.get(target)))
				return target;
		return null;
	}

	// -------------------------------------------------------------------------
	// Inner class: Animation Tuple
	// -------------------------------------------------------------------------

	public static class AnimationDef {
		public final String name;
		public final Timeline timeline;
		public final Editor editor;
		public final List<Object> targets;
		public final Map<Object, String> targetsNamesMap;

		public AnimationDef(String name, Timeline timeline, Editor editor, List<Object> targets, Map<Object, String> targetsNamesMap) {
			this.name = name;
			this.timeline = timeline;
			this.editor = editor;
			this.targets = Collections.unmodifiableList(targets);
			this.targetsNamesMap = Collections.unmodifiableMap(targetsNamesMap);
		}
	}
}
