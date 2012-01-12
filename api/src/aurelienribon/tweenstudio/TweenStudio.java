package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
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
	private static Editor nextEditor;
	private static Callback nextCallback;

	// Only needed in edition mode
	private static MainWindow editionWindow;
	private static Map<Class<? extends Editor>, Editor> editorsMap;
	private static Map<String, File> filesMap;
	private static Queue<AnimationDef> animationsFifo;
	private static AnimationDef currentAnimation;

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
	 * Returns true if the edition window is opened.
	 */
	public static boolean isEditionEnabled() {
		return editionWindow != null;
	}

	/**
	 * Enables the edition of the application timelines. The edition window
	 * will be spawned.
	 */
	public static void enableEdition() {
		enableEdition(1000, 500);
	}

	/**
	 * Enables the edition of the application timelines. The edition window
	 * will be spawned. Lets you define the size of the window.
	 */
	public static void enableEdition(final int width, final int height) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {@Override public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException ex) {
				} catch (InstantiationException ex) {
				} catch (IllegalAccessException ex) {
				} catch (UnsupportedLookAndFeelException ex) {
				}

				editionWindow = new MainWindow(new MainWindow.Callback() {
					@Override public void editionComplete() {
						try {
							String str = ImportExportHelper.timelineToString(currentAnimation.timeline, currentAnimation.targetsNamesMap);
							FileUtils.writeStringToFile(str, filesMap.get(currentAnimation.name));
						} catch (IOException ex) {
							throw new RuntimeException(ex.getMessage());
						}

						currentAnimation.callback.animationReady(currentAnimation.name, currentAnimation.timeline);
						callNextAnimation();
					}

					@Override public void editionDiscarded() {
						currentAnimation.callback.animationReady(currentAnimation.name, currentAnimation.timeline);
						callNextAnimation();
					}
				});

				editionWindow.setSize(width, height);
				editionWindow.setVisible(true);
				editionWindow.addWindowListener(new WindowAdapter() {
					@Override public void windowClosed(WindowEvent e) {
						editionWindow = null;
						if (currentAnimation != null) {
							currentAnimation.editor.stop();
							currentAnimation.callback.animationReady(currentAnimation.name, currentAnimation.timeline);
							while (!animationsFifo.isEmpty()) {
								currentAnimation = animationsFifo.remove();
								currentAnimation.callback.animationReady(currentAnimation.name, currentAnimation.timeline);
							}
						}
					}
				});
			}});

			if (editorsMap == null) editorsMap = new HashMap<Class<? extends Editor>, Editor>();
			if (filesMap == null) filesMap = new HashMap<String, File>();
			if (animationsFifo == null) animationsFifo = new ArrayDeque<AnimationDef>();

		} catch (InterruptedException ex) {
			editionWindow = null;
			throw new RuntimeException(ex);
		} catch (InvocationTargetException ex) {
			editionWindow = null;
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Loads the file content and create a timeline out of it. If edition is
	 * enabled, the file path is also stored for future modification.
	 */
	public static void preloadAnimation(File animationFile, String animationName) {
		try {
			String str = FileUtils.readFileToString(animationFile);
			Timeline tl = ImportExportHelper.stringToTimeline(str);
			timelinesMap.put(animationName, tl);
			if (isEditionEnabled()) filesMap.put(animationName, animationFile);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Removes the animation from memory.
	 */
	public static void unloadAnimation(String animationName) {
		timelinesMap.remove(animationName);
		if (filesMap != null) filesMap.remove(animationName);
	}

	/**
	 * Removes every animation from memory.
	 */
	public static void unloadAllAnimations() {
		timelinesMap.clear();
		if (filesMap != null) filesMap.clear();
	}

	/**
	 * If edition is enabled, gets an instance of the given editor class. It
	 * may be useful if the editor needs some custom parameters depending on
	 * the current context. Returns always null is edition is disabled.
	 */
	public static <T extends Editor> T getEditor(Class<T> editorClass) {
		if (isEditionEnabled()) {
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

	/**
	 * Registers the given editor class to be used in the future animation
	 * editions.
	 */
	public static void registerEditor(Class<? extends Editor> editorClass) {
		nextEditor = getEditor(editorClass);
	}

	/**
	 * Registers a callback to be used with the future animations.
	 */
	public static void registerCallback(Callback callback) {
		nextCallback = callback;
	}

	/**
	 * Registers the given object as a target of the future animations.
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
		if (isEditionEnabled() && nextEditor == null) throw new NullPointerException("No editor was registered.");
		
		Timeline timeline = buildTimeline(timelinesMap.get(animationName), nextTargetsNamesMap);
		
		if (isEditionEnabled()) {
			AnimationDef anim = new AnimationDef(
				animationName, timeline, nextEditor, nextCallback,
				new ArrayList<Object>(nextTargets),
				new HashMap<Object, String>(nextTargetsNamesMap));

			animationsFifo.add(anim);
			if (currentAnimation == null) {
				currentAnimation = animationsFifo.poll();
				currentAnimation.editor.start(currentAnimation, editionWindow);
				editionWindow.initialize(currentAnimation);
			}
		} else {
			nextCallback.animationReady(animationName, timeline);
		}
	}

	/**
	 * Updates the edition engine. Does nothing if animation edition is
	 * not enabled, or if there is no animation to edit.
	 */
	public static void update(final int deltaMillis) {
		if (!isEditionEnabled()) return;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {@Override public void run() {
				editionWindow.update(deltaMillis);
			}});
		} catch (InterruptedException ex) {
		} catch (InvocationTargetException ex) {
		}
	}

	/**
	 * Renders the current editor painting. Does nothing if animation edition is
	 * not enabled, or if there is no animation to edit.
	 */
	public static void render() {
		if (!isEditionEnabled()) return;
		if (nextEditor == null || !nextEditor.isEnabled()) return;
		nextEditor.render();
	}

	// -------------------------------------------------------------------------
	// Package API
	// -------------------------------------------------------------------------

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

	private static void callNextAnimation() {
		currentAnimation.editor.stop();
		currentAnimation = animationsFifo.poll();
		if (currentAnimation != null) {
			currentAnimation.editor.start(currentAnimation, editionWindow);
			editionWindow.initialize(currentAnimation);
		}
	}

	// -------------------------------------------------------------------------
	// Inner class: Animation Tuple
	// -------------------------------------------------------------------------

	public static class AnimationDef {
		public final String name;
		public final Timeline timeline;
		public final Editor editor;
		public final Callback callback;
		public final List<Object> targets;
		public final Map<Object, String> targetsNamesMap;

		public AnimationDef(String name, Timeline timeline, Editor editor, Callback callback, List<Object> targets, Map<Object, String> targetsNamesMap) {
			this.name = name;
			this.timeline = timeline;
			this.editor = editor;
			this.callback = callback;
			this.targets = Collections.unmodifiableList(targets);
			this.targetsNamesMap = Collections.unmodifiableMap(targetsNamesMap);
		}
	}
}
