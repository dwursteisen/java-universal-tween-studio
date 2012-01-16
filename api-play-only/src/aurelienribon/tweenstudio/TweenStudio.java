package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.utils.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Universal Tween Studio features multiple static calls to let you either
 * edit or play your animations directly in your applications.
 * <br/><br/>
 *
 * A usage example could be cast follows:
 * <br/><br/>
 *
 * public class MyGame {
 *     public static void main(String[] args) {
 *         TweenStudio.enableEdition();
 *         << start your game >>
 *     }
 *
 *     public MyGame() {
 *         << load and create your objects >>
 *
 *         TweenStudio.preloadAnimation(new File("data/anim1.timeline"), "My animation");
 *
 *         TweenStudio.registerEditor(MyTweenStudioEditor.class);
 *         TweenStudio.registerTarget(sprites[0], "Player");
 *         TweenStudio.registerTarget(sprites[1], "Enemy");
 *         TweenStudio.registerTarget(sprites[2], "Ground");
 *         TweenStudio.registerTarget(sprites[3], "Sun");
 *
 *         TweenStudio.registerCallback(new TweenStudio.Callback() {
 *             @Override public void animationReady(String animationName, Timeline animation) {
 * 	               animation.start(tweenManager);
 * 	           }
 *         });
 *
 *         TweenStudio.createAnimation("My animation");
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

	// Always needed
	private static final Map<String, Timeline> timelinesMap = new HashMap<String, Timeline>(5);
	private static final List<Object> nextTargets = new ArrayList<Object>(5);
	private static final Map<Object, String> nextTargetsNamesMap = new HashMap<Object, String>(5);
	private static Callback nextCallback;

	// -------------------------------------------------------------------------
	// Callback
	// -------------------------------------------------------------------------

	public static interface Callback {
		public void animationReady(String animationName, Timeline animation);
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Method stub
	 */
	public static boolean isEditionEnabled() {
		return false;
	}

	/**
	 * Method stub
	 */
	public static void enableEdition() {
	}

	/**
	 * Method stub
	 */
	public static void enableEdition(final int width, final int height) {

	}

	/**
	 * Loads the file content and create a timeline out of it. If edition is
	 * enabled, the file path is also stored for future modification.
	 */
	public static void preloadAnimation(File animationFile, String animationName) {
		try {
			String str = FileUtils.readFileToString(animationFile);
			Timeline tl = ImportExportHelper.stringToDummyTimeline(str);
			timelinesMap.put(animationName, tl);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Removes the animation from memory.
	 */
	public static void unloadAnimation(String animationName) {
		timelinesMap.remove(animationName);
	}

	/**
	 * Removes every animation from memory.
	 */
	public static void unloadAllAnimations() {
		timelinesMap.clear();
	}

	/**
	 * Method stub
	 */
	public static <T extends Editor> T getEditor(Class<T> editorClass) {
		return null;
	}

	/**
	 * Method stub
	 */
	public static void registerEditor(Class<? extends Editor> editorClass) {
	}

	/**
	 * Registers a callback to be used with the future animations.
	 */
	public static void registerCallback(Callback callback) {
		nextCallback = callback;
	}

	/**
	 * Registers the given object cast a target of the future animations.
	 */
	public static void registerTarget(Object target, String name) {
		if (!nextTargets.contains(target)) nextTargets.add(target);
		nextTargetsNamesMap.put(target, name);
	}

	/**
	 * Removes a target from the future animation editions.
	 */
	public static void unregisterTarget(Object target) {
		nextTargets.remove(target);
		nextTargetsNamesMap.remove(target);
	}

	/**
	 * Removes every target from the future animation editions.
	 */
	public static void unregisterAllTargets() {
		nextTargets.clear();
		nextTargetsNamesMap.clear();
	}

	/**
	 * Creates a {@link Timeline} from the loaded file associated to the
	 * given animation name.
	 */
	public static void createAnimation(String animationName) {
		if (!timelinesMap.containsKey(animationName)) throw new RuntimeException(animationName + " is not loaded.");
		if (nextCallback == null) throw new NullPointerException("No callback was registered");

		Timeline timeline = TimelineCreationHelper.buildTimelineFromDummy(
			timelinesMap.get(animationName),
			nextTargets,
			nextTargetsNamesMap);
		
		nextCallback.animationReady(animationName, timeline);
	}

	/**
	 * Method stub
	 */
	public static void update(final int deltaMillis) {
	}

	/**
	 * Method stub
	 */
	public static void render() {
	}

	// -------------------------------------------------------------------------
	// Inner class: Animation Tuple
	// -------------------------------------------------------------------------

	public static class AnimationDef {
		public final String name;
		public final File file;
		public final Timeline timeline;
		public final Editor editor;
		public final Callback callback;
		public final List<Object> targets;
		public final Map<Object, String> targetsNamesMap;

		public AnimationDef(String name, File file, Timeline timeline, Editor editor, Callback callback, List<Object> targets, Map<Object, String> targetsNamesMap) {
			this.name = name;
			this.file = file;
			this.timeline = timeline;
			this.editor = editor;
			this.callback = callback;
			this.targets = Collections.unmodifiableList(targets);
			this.targetsNamesMap = Collections.unmodifiableMap(targetsNamesMap);
		}
	}
}
