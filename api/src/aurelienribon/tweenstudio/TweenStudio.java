package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenstudio.ui.MainWindow;
import aurelienribon.tweenstudio.ui.timeline.TimelineHelper;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

	private static Map<Class<? extends Editor>, Editor> editors = new HashMap<Class<? extends Editor>, Editor>(1);
	private static Map<Class<? extends Player>, Player> players = new HashMap<Class<? extends Player>, Player>(1);

	public static void registerEditor(Editor editor) {
		editors.put(editor.getClass(), editor);
	}

	public static void registerPlayer(Player player) {
		players.put(player.getClass(), player);
	}

	public static Editor getRegisteredEditor(Class<? extends Editor> editorClass) {
		return editors.get(editorClass);
	}

	public static Player getRegisteredPlayer(Class<? extends Player> playerClass) {
		return players.get(playerClass);
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
	private String filepath;
	private Map<Object, InitialState> initialStatesMap;
	private TimelineModel model;
	private MainWindow wnd;
	private int playTime;
	private int playDuration;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void registerTarget(Object target, String name) {
		targets.add(target);
		namesMap.put(target, name);
	}

	public void play(Class<? extends Player> playerClass, String filepath) {
		if (!players.containsKey(playerClass)) throw new RuntimeException("No such player registered");

		Player player = players.get(playerClass);
		String fileContent = player.getFileContent(filepath);
		timeline = ImportExportHelper.stringToTimeline(fileContent);
	}

	public void edit(Class<? extends Editor> editorClass, String filepath) {
		if (!editors.containsKey(editorClass)) throw new RuntimeException("No such editor registered");

		this.editor = editors.get(editorClass);
		this.filepath = filepath;

		editor.setStudio(this);
		editor.initialize();

		initializeProperties(editor);
		initializeInitialStates(editor);
		createModel(editor);

		String fileContent = editor.getFileContent(filepath);
		ImportExportHelper.stringToModel(fileContent, model);

		createWindow(model);
		createTimeline();
	}

	public void update(int deltaMillis) {
		if (wnd.isPlaying()) {
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
		if (editor != null) editor.render();
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
		String name = namesMap.get(target);
		int currentTime = wnd.getCurrentTime();

		for (int tweenType : tweenTypes) {
			String propertyName = editor.getProperty(target.getClass(), tweenType).getName();
			Element elem = model.getElement(name + "/" + propertyName);
			Node node = getNodeOrCreate(elem, currentTime);
			NodeData nodeData = (NodeData) node.getUserData();

			TweenAccessor accessor = Tween.getRegisteredAccessor(target.getClass());
			accessor.getValues(target, tweenType, buffer);
			nodeData.setTargets(buffer);
		}

		createTimeline();
		wnd.updateTargetsValues();
	}

	// -------------------------------------------------------------------------
	// Helpers -- initialization
	// -------------------------------------------------------------------------

	private void initializeProperties(Editor editor) {
		for (Object target : targets) {
			List<Property> properties = editor.getProperties(target.getClass());
			TweenAccessor accessor = Tween.getRegisteredAccessor(target.getClass());

			for (Property property : properties) {
				int cnt = accessor.getValues(target, property.getId(), buffer);
				property.setAttributesCount(cnt);
			}
		}
	}

	private void initializeInitialStates(Editor editor) {
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

			for (Property property : properties) {
				elem = model.addElement(namesMap.get(target) + "/" + property.getName());
				elem.setUserData(new ElementData(target, property.getId()));
			}
		}
	}

	private void createWindow(final TimelineModel model) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
		} catch (InstantiationException ex) {
		} catch (IllegalAccessException ex) {
		} catch (UnsupportedLookAndFeelException ex) {
		}

		wnd = new MainWindow();
		wnd.setTimelineModel(model);
		wnd.setSize(1000, 500);
		wnd.setCallback(wndCallback);
		wnd.setVisible(true);
		wnd.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				String str = ImportExportHelper.modelToString(model);
				editor.setFileContent(filepath, str);
				editor.dispose();
			}
		});
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
			
			createTweens(elem, elemData.getTarget(), elemData.getTweenType());
			setToInitialState(elemData.getTarget(), elemData.getTweenType());
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
		Property property = editor.getProperty(elemData.getTarget().getClass(), elemData.getTweenType());

		NodeData nodeData = new NodeData(property.getAttributesCount());
		TweenAccessor accessor = Tween.getRegisteredAccessor(elemData.getTarget().getClass());
		accessor.getValues(elemData.getTarget(), elemData.getTweenType(), nodeData.getTargets());
		node.setUserData(nodeData);
	}

	private void setToInitialState(Object target, int tweenType) {
		InitialState initState = initialStatesMap.get(target);
		float[] initValues = initState.getValues(tweenType);

		TweenAccessor accessor = Tween.getRegisteredAccessor(target.getClass());
		accessor.setValues(target, tweenType, initValues);
	}

	// -------------------------------------------------------------------------
	// Helpers -- misc
	// -------------------------------------------------------------------------

	private Node getNodeOrCreate(Element elem, int time) {
		Node node = null;

		for (Node n : elem.getNodes()) {
			if (n.getTime() == time) {
				node = n;
				break;
			}
		}

		if (node == null) node = elem.addNode(time);
		return node;
	}

	// -------------------------------------------------------------------------
	// Window Callback
	// -------------------------------------------------------------------------

	private MainWindow.Callback wndCallback = new MainWindow.Callback() {
		@Override
		public void timeCursorPositionChanged(int oldTime, int newTime) {
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
