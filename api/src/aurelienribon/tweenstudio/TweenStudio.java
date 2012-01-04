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

	// Only needed in edition mode
	private static MainWindow editionWindow;
	private static Map<Class<? extends Editor>, Editor> editorsMap;
	private static Map<String, File> filesMap;
	private static Queue<AnimationDef> animationsFifo;
	private static AnimationDef currentAnimation;
	private static Editor nextEditor;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public static boolean isEditionEnabled() {
		return editionWindow != null;
	}

	public static void enableEdition() {
		enableEdition(1000, 500);
	}

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
						currentAnimation.timeline.start();
						currentAnimation.editor.stop();

						try {
							String str = ImportExportHelper.timelineToString(currentAnimation.timeline, currentAnimation.targetsNamesMap);
							FileUtils.writeStringToFile(str, filesMap.get(currentAnimation.name));
						} catch (IOException ex) {
							throw new RuntimeException(ex.getMessage());
						}

						currentAnimation = animationsFifo.poll();
						if (currentAnimation != null) {
							currentAnimation.editor.start(currentAnimation);
							editionWindow.initialize(currentAnimation);
						}
					}
				});

				editionWindow.setSize(width, height);
				editionWindow.setVisible(true);
				editionWindow.addWindowListener(new WindowAdapter() {
					@Override public void windowClosed(WindowEvent e) {
						editionWindow = null;
						if (currentAnimation != null) {
							currentAnimation.editor.stop();
							currentAnimation.timeline.start();
							while (!animationsFifo.isEmpty())
								animationsFifo.remove().timeline.start();
						}
					}
				});
			}});

			if (editorsMap == null) editorsMap = new HashMap<Class<? extends Editor>, Editor>();
			if (filesMap == null) filesMap = new HashMap<String, File>();
			if (animationsFifo == null) animationsFifo = new ArrayDeque<AnimationDef>();

		} catch (InterruptedException ex) {
		} catch (InvocationTargetException ex) {
		}
	}

	public static void loadAnimation(File file, String animationName) {
		try {
			String str = FileUtils.readFileToString(file);
			Timeline tl = ImportExportHelper.stringToTimeline(str);
			timelinesMap.put(animationName, tl);
			if (isEditionEnabled()) filesMap.put(animationName, file);
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

	public static void registerEditor(Class<? extends Editor> editorClass) {
		nextEditor = getEditor(editorClass);
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
		if (isEditionEnabled() && nextEditor == null) throw new RuntimeException("No editor was set.");
		
		Timeline timeline = buildTimeline(timelinesMap.get(animationName), nextTargetsNamesMap);
		
		if (isEditionEnabled()) {
			AnimationDef anim = new AnimationDef(
				animationName, timeline, nextEditor,
				new ArrayList<Object>(nextTargets),
				new HashMap<Object, String>(nextTargetsNamesMap));

			TweenManager.setAutoStart(timeline, false);

			animationsFifo.add(anim);
			if (currentAnimation == null) {
				currentAnimation = animationsFifo.poll();
				currentAnimation.editor.start(currentAnimation);
				editionWindow.initialize(currentAnimation);
			}
		}

		return timeline;
	}

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

	public static void render() {
		if (!isEditionEnabled()) return;
		if (nextEditor == null || !nextEditor.isEnabled()) return;
		nextEditor.render();
	}

	// -------------------------------------------------------------------------
	// Package API
	// -------------------------------------------------------------------------

	static void targetStateChanged(final Object target, final Set<Integer> tweenTypes) {
		if (!isEditionEnabled()) return;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {@Override public void run() {
				String name = currentAnimation.targetsNamesMap.get(target);
				editionWindow.targetStateChanged(target, name, tweenTypes);
			}});
		} catch (InterruptedException ex) {
		} catch (InvocationTargetException ex) {
		}
	}

	static void selectedObjectChanged(final Object obj) {
		if (!isEditionEnabled()) return;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {@Override public void run() {
				editionWindow.selectedObjectChanged(obj);
			}});
		} catch (InterruptedException ex) {
		} catch (InvocationTargetException ex) {
		}
	}

	static void mouseOverObjectChanged(final Object obj) {
		if (!isEditionEnabled()) return;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {@Override public void run() {
				editionWindow.mouseOverObjectChanged(obj);
			}});
		} catch (InterruptedException ex) {
		} catch (InvocationTargetException ex) {
		}
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
