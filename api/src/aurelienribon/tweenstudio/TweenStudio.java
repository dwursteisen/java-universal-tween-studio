package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenstudio.ui.MainWindow;
import aurelienribon.tweenstudio.ui.timeline.TimelineHelper;
import aurelienribon.tweenstudio.ui.timeline.TimelineHelper.NodePart;
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

	private final List<Object> targets = new ArrayList<Object>(5);
	private final Map<Object, String> namesMap = new HashMap<Object, String>(5);
	private final TweenManager tweenManager = new TweenManager();
	private final float[] buffer = new float[Tween.MAX_COMBINED_TWEENS];

	private Editor editor;
	private String filepath;
	private Map<Object, InitialState> initialStatesMap;
	private TimelineModel model;
	private MainWindow wnd;
	private TweenManager editorTweenManager;

	private long playStartTime;
	private int playDuration;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void registerTarget(Object target, String name) {
		targets.add(target);
		namesMap.put(target, name);
	}

	public void play(Class<? extends Player> playerClass, String filepath) {
		if (!players.containsKey(playerClass))
			throw new RuntimeException("No such player registered");

		Player player = players.get(playerClass);
		String fileContent = player.getFileContent(filepath);
		ImportExportHelper.stringToTweens(fileContent, tweenManager);
	}

	public void edit(Class<? extends Editor> editorClass, String filepath) {
		if (!editors.containsKey(editorClass))
			throw new RuntimeException("No such editor registered");

		this.editor = editors.get(editorClass);
		this.filepath = filepath;

		editorTweenManager = new TweenManager();
		editor.setStudio(this);
		editor.initialize();

		initializeProperties(editor);
		initializeInitialStates(editor);
		createModel(editor);

		String fileContent = editor.getFileContent(filepath);
		ImportExportHelper.stringToModel(fileContent, model);

		createWindow(model);
	}

	public void update() {
		if (wnd.isPlaying()) {
			int currentTime = (int) (System.currentTimeMillis() - playStartTime);
			if (currentTime <= playDuration) {
				wnd.setTimeCursorPosition(currentTime);
			} else {
				wnd.setTimeCursorPosition(playDuration);
				wnd.setPlaying(false);
			}
		}

		if (editorTweenManager != null) editorTweenManager.update();
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
		int currentTime = wnd.getTimeCursorPosition();

		for (int tweenType : tweenTypes) {
			String propertyName = editor.getProperty(target.getClass(), tweenType).getName();
			Element elem = model.getElement(name + "/" + propertyName);
			Node node = getNodeAtTime(elem, currentTime);
			NodeData nodeData = (NodeData) node.getUserData();

			TweenAccessor accessor = Tween.getDefaultAccessor(target.getClass());
			accessor.getValues(target, tweenType, buffer);
			nodeData.setTargets(buffer);
		}

		resetTweens();
		wnd.updateTargetsValues();
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void initializeProperties(Editor editor) {
		for (Object target : targets) {
			List<Property> properties = editor.getProperties(target.getClass());
			TweenAccessor accessor = Tween.getDefaultAccessor(target.getClass());

			for (Property property : properties) {
				int cnt = accessor.getValues(target, property.getTweenType(), buffer);
				property.setCombinedTweensCount(cnt);
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
		model.addListener(new TimelineModel.EventListener() {
			@Override public void stateChanged() {if (wnd != null) resetTweens();}
		});

		for (Object target : targets) {
			List<Property> properties = editor.getProperties(target.getClass());
			Element elem = model.addElement(namesMap.get(target));
			elem.setSelectable(false);

			for (Property property : properties) {
				elem = model.addElement(namesMap.get(target) + "/" + property.getName());
				elem.setUserData(new ElementData(target, property.getTweenType()));
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

		resetTweens();
	}

	private void resetTweens() {
		tweenManager.clear();

		for (Element elem : model.getElements()) {
			if (!elem.isSelectable()) continue;

			ElementData elemData = (ElementData) elem.getUserData();
			Object target = elemData.getTarget();
			int tweenType = elemData.getTweenType();

			elem.sortNodes();
			createTweens(elem, target, tweenType);
			setToInitialState(target, tweenType);
		}

		int time = -1, lastTime = 0, duration = model.getDuration();
		while (true) {
			int t1 = TimelineHelper.getNextTime(model, time, NodePart.START);
			int t2 = TimelineHelper.getNextTime(model, time, NodePart.END);
			if (t1 == t2 && t1 == duration) break;
			if (t1 == t2) time = t1;
			if (t1 < t2) {if (t1 > time) time = t1; else time = t2;}
			if (t2 < t1) {if (t2 > time) time = t2; else time = t1;}

			int delta = time - lastTime;
			lastTime = time;

			tweenManager.update(delta-1);
			tweenManager.update(+2);
			tweenManager.update(-1);
		}

		int currentTime = wnd.getTimeCursorPosition();
		tweenManager.update(-duration-1);
		tweenManager.update(+currentTime+2);
		tweenManager.update(-1);
	}

	private void createTweens(Element elem, Object target, int tweenType) {
		for (Node node : elem.getNodes()) {
			if (node.getUserData() == null) createNodeData(node);
			NodeData nodeData = (NodeData) node.getUserData();
			
			Tween.to(target, tweenType, node.getDuration())
				.target(nodeData.getTargets())
				.ease(nodeData.getEquation())
				.delay(node.getStart())
				.repeat(0, Integer.MAX_VALUE - node.getEnd())
				.addToManager(tweenManager);
		}
	}

	private void createNodeData(Node node) {
		ElementData elemData = (ElementData) node.getParent().getUserData();
		Property property = editor.getProperty(elemData.getTarget().getClass(), elemData.getTweenType());

		NodeData nodeData = new NodeData(property.getCombinedTweensCount());
		TweenAccessor accessor = Tween.getDefaultAccessor(elemData.getTarget().getClass());
		accessor.getValues(elemData.getTarget(), elemData.getTweenType(), nodeData.getTargets());
		node.setUserData(nodeData);
	}

	private void setToInitialState(Object target, int tweenType) {
		InitialState initState = initialStatesMap.get(target);
		float[] initValues = initState.getValues(tweenType);

		TweenAccessor accessor = Tween.getDefaultAccessor(target.getClass());
		accessor.setValues(target, tweenType, initValues);
	}

	private Node getNodeAtTime(Element elem, int time) {
		Node node = null;
		for (Node n : elem.getNodes()) {
			if (n.getEnd() == time) {
				node = n;
				break;
			}
		}

		if (node == null) node = elem.addNode(time, 0);
		return node;
	}

	// -------------------------------------------------------------------------
	// Window Callback
	// -------------------------------------------------------------------------

	private MainWindow.Callback wndCallback = new MainWindow.Callback() {
		@Override
		public void timeCursorPositionChanged(int oldTime, int newTime) {
			tweenManager.update(-oldTime-1);
			tweenManager.update(+newTime+2);
			tweenManager.update(-1);
		}

		@Override
		public void nodeInfoChanged(Node node) {
			resetTweens();
		}

		@Override
		public void playRequested() {
			playDuration = model.getDuration();
			playStartTime = System.currentTimeMillis();
			wnd.setPlaying(true);
		}

		@Override
		public void pauseRequested() {
			wnd.setPlaying(false);
		}
	};
}
