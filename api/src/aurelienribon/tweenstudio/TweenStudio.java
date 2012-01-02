package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenstudio.ui.MainWindow;
import aurelienribon.tweenstudio.ui.timeline.TimelineHelper;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import aurelienribon.utils.io.FileUtils;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	// Static API
	// -------------------------------------------------------------------------

	private static final Map<Class<? extends Editor>, Editor> editors = new HashMap<Class<? extends Editor>, Editor>(1);
	private static MainWindow wnd;

	public static void registerEditor(Class<? extends Editor> editorClass) {
		editors.put(editorClass, null);
	}

	public static Editor getRegisteredEditor(Class<? extends Editor> editorClass) {
		if (wnd != null) return getEditorOrCreate(editorClass);
		else return editors.get(editorClass);
	}

	public static void spawn() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
		} catch (InstantiationException ex) {
		} catch (IllegalAccessException ex) {
		} catch (UnsupportedLookAndFeelException ex) {
		}

		wnd = new MainWindow();
		wnd.setSize(1000, 500);
		wnd.setVisible(true);
		wnd.addWindowListener(new WindowAdapter() {
			@Override public void windowClosed(WindowEvent e) {
				wnd = null;
			}
		});
	}

	private static Editor getEditorOrCreate(Class<? extends Editor> editorClass) {
		Editor ed = editors.get(editorClass);
		if (ed == null) {
			try {
				ed = editorClass.newInstance();
				editors.put(editorClass, ed);
			} catch (InstantiationException ex) {
				throw new RuntimeException(ex.getMessage());
			} catch (IllegalAccessException ex) {
				throw new RuntimeException(ex.getMessage());
			}
		}
		return ed;
	}

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	// Always needed
	private final List<Object> targets = new ArrayList<Object>(5);
	private final Map<Object, String> namesMap = new HashMap<Object, String>(5);
	private final float[] buffer = new float[Tween.MAX_COMBINED_TWEENS];
	private Timeline timeline;

	// Only needed in editor mode
	private Editor editor;
	private Map<Object, InitialState> initialStatesMap;
	private TimelineModel model;
	private int playTime;
	private int playDuration;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void registerTarget(Object target, String name) {
		targets.add(target);
		namesMap.put(target, name);
	}

	public void playOrEdit(Class<? extends Editor> editorClass, String animationName, File file) {
		if (!editors.containsKey(editorClass)) throw new RuntimeException("No such editor registered");
		if (wnd == null) play(file);
		else edit(editorClass, animationName, file);
	}

	public void update(int deltaMillis) {
		if (editor == null || !editor.isUsed()) return;
		if (wnd != null && wnd.isPlaying()) {
			playTime += deltaMillis;
			if (playTime <= playDuration) {
				wnd.setCurrentTime(playTime);
			} else {
				wnd.setCurrentTime(playDuration);
				wnd.setPlaying(false);
			}	
		}
	}

	public void render() {
		if (editor == null || !editor.isUsed()) return;
		editor.render();
	}

	// -------------------------------------------------------------------------
	// Package API
	// -------------------------------------------------------------------------

	List<Object> getTargets() {
		return targets;
	}

	String getName(Object target) {
		return namesMap.get(target);
	}

	void targetStateChanged(Object target, int tweenType) {
		Set<Integer> tweenTypes = new HashSet<Integer>();
		tweenTypes.add(tweenType);
		targetStateChanged(target, tweenTypes);
	}

	void targetStateChanged(Object target, Set<Integer> tweenTypes) {
		if (wnd == null) return;

		String name = namesMap.get(target);
		int currentTime = wnd.getCurrentTime();

		for (int tweenType : tweenTypes) {
			String propertyName = editor.getProperty(target.getClass(), tweenType).getName();
			Element elem = model.getElement(name + "/" + propertyName);
			Node node = TimelineHelper.getNodeOrCreate(elem, currentTime);
			NodeData nodeData = (NodeData) node.getUserData();

			TweenAccessor accessor = Tween.getRegisteredAccessor(target.getClass());
			accessor.getValues(target, tweenType, buffer);
			nodeData.setTargets(buffer);
		}

		createTimeline();
		wnd.nodeDataChanged();
	}

	void selectedObjectChanged(Object obj) {
		if (wnd == null) return;
		wnd.selectedObjectChanged(obj);
	}

	void mouseOverObjectChanged(Object obj) {
		if (wnd == null) return;
		wnd.mouseOverObjectChanged(obj);
	}

	// -------------------------------------------------------------------------
	// Helpers -- initialization
	// -------------------------------------------------------------------------

	private void play(File file) {
		try {
			String str = FileUtils.readFileToString(file);
			timeline = ImportExportHelper.stringToTimeline(str);
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
	}

	private void edit(Class<? extends Editor> editorClass, String animationName, File file) {
		try {
			editor = getEditorOrCreate(editorClass);
			editor.setStudio(this);
			editor.initialize();
			createInitialStates(editor);
			createModel(editor);
			String fileContent = FileUtils.readFileToString(file);
			ImportExportHelper.stringToModel(fileContent, model);
			createTimeline();
			wnd.initialize(editor, model, wndCallback, animationName, file);
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
	}

	private void createInitialStates(Editor editor) {
		initialStatesMap = new HashMap<Object, InitialState>();

		for (Object target : targets) {
			InitialState state = new InitialState(editor, target);
			initialStatesMap.put(target, state);
		}
	}

	private void createModel(Editor editor) {
		model = new TimelineModel();
		model.addListener(new TimelineModel.Listener() {
			@Override public void stateChanged() {if (wnd != null) createTimeline();}
		});

		for (Object target : targets) {
			List<Property> properties = editor.getProperties(target.getClass());
			Element elem = model.addElement(namesMap.get(target));
			elem.setSelectable(false);
			elem.setUserData(new ElementData(target, null));

			for (Property property : properties) {
				elem = model.addElement(namesMap.get(target) + "/" + property.getName());
				elem.setUserData(new ElementData(target, property));
			}
		}
	}

	// -------------------------------------------------------------------------
	// Helpers -- timeline creation
	// -------------------------------------------------------------------------

	private void createTimeline() {
		if (timeline != null) timeline.free();
		timeline = Timeline.createParallel();

		for (Element elem : model.getElements()) {
			if (!elem.isSelectable()) continue;
			
			for (Node node : elem.getNodes())
				if (node.getUserData() == null)
					createNodeData(node);

			ElementData elemData = (ElementData) elem.getUserData();
			
			createTweens(elem, elemData.getTarget(), elemData.getProperty().getId());
			setToInitialState(elemData.getTarget(), elemData.getProperty().getId());
		}

		timeline.start();

		int time = -1, lastTime = 0, acc = 0;

		while (true) {
			time = TimelineHelper.getNextTime(model.getRoot(), time, true);
			if (time < 0) break;

			timeline.update(time - lastTime);

			acc += time - lastTime;
			lastTime = time;
		}

		assert acc == model.getDuration();
		timeline.update(wnd.getCurrentTime() - acc);
	}

	private void createTweens(Element elem, Object target, int tweenType) {
		for (Node node : elem.getNodes()) {
			NodeData nodeData = (NodeData) node.getUserData();

			int duration = TimelineHelper.getDuration(node);
			int delay = node.getTime() - duration;
			
			Tween tween = Tween.to(target, tweenType, duration)
				.target(nodeData.getTargets())
				.ease(nodeData.getEquation())
				.delay(delay);

			timeline.push(tween);
		}
	}

	private void createNodeData(Node node) {
		ElementData elemData = (ElementData) node.getParent().getUserData();
		Property property = elemData.getProperty();

		NodeData nodeData = new NodeData(property.getFields().length);
		TweenAccessor accessor = Tween.getRegisteredAccessor(elemData.getTarget().getClass());
		accessor.getValues(elemData.getTarget(), property.getId(), nodeData.getTargets());
		node.setUserData(nodeData);
	}

	private void setToInitialState(Object target, int tweenType) {
		InitialState initState = initialStatesMap.get(target);
		float[] initValues = initState.getValues(tweenType);

		TweenAccessor accessor = Tween.getRegisteredAccessor(target.getClass());
		accessor.setValues(target, tweenType, initValues);
	}

	// -------------------------------------------------------------------------
	// Window Callback
	// -------------------------------------------------------------------------

	private MainWindow.Callback wndCallback = new MainWindow.Callback() {
		@Override
		public void currentTimeChanged(int newTime, int oldTime) {
			timeline.update(newTime-oldTime);
		}

		@Override
		public void nodeInfoChanged(Node node) {
			createTimeline();
		}

		@Override
		public void playRequested() {
			playDuration = model.getDuration();
			playTime = 0;
			wnd.setPlaying(true);
		}

		@Override
		public void pauseRequested() {
			wnd.setPlaying(false);
		}
	};
}
