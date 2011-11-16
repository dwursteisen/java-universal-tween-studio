package aurelienribon.tweenstudio;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.Tweenable;
import aurelienribon.tweenstudio.ui.MainWindow;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TweenStudio {
	private static final float[] buffer = new float[Tween.MAX_COMBINED_TWEENS];

	private final List<Tweenable> tweenables = new ArrayList<Tweenable>(5);
	private final Map<Tweenable, String> namesMap = new HashMap<Tweenable, String>(5);
	private final TweenManager tweenManager = new TweenManager();

	private Editor editor;
	private Map<Tweenable, InitialState> initialStatesMap;
	private TimelineModel model;
	private MainWindow wnd;

	private long playStartTime;
	private int playDuration;

	private OutputStream outputStream;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void registerTweenable(Tweenable tweenable, String name) {
		tweenables.add(tweenable);
		namesMap.put(tweenable, name);
	}

	public void play(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			play(is);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {if (is != null) is.close();} catch (IOException ex) {}
		}
	}

	public void play(InputStream is) {
		try {
			String str = IoHelper.readStream(is);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {is.close();} catch (IOException ex) {}
		}
	}

	public void edit(Editor editor, File file) {
		try {
			InputStream is = new FileInputStream(file);
			OutputStream os = new FileOutputStream(file);
			edit(editor, is, os);
		} catch (FileNotFoundException ex) {
		}
	}

	public void edit(Editor editor, InputStream is, OutputStream os) {
		try {
			String str = IoHelper.readStream(is);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		this.editor = editor;

		editor.initialize();

		initializeProperties(editor);
		initializeInitialStates(editor);
		createModel(editor);
		createWindow(model, editor);
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
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void initializeProperties(Editor editor) {
		for (Tweenable tweenable : tweenables) {
			List<Property> properties = editor.getProperties(tweenable.getClass());

			for (Property property : properties) {
				int cnt = tweenable.getTweenValues(property.getId(), buffer);
				property.setCombinedTweensCount(cnt);
			}
		}
	}

	private void initializeInitialStates(Editor editor) {
		initialStatesMap = new HashMap<Tweenable, InitialState>();

		for (Tweenable tweenable : tweenables) {
			InitialState state = new InitialState(editor, tweenable);
			initialStatesMap.put(tweenable, state);
		}
	}

	private void createModel(Editor editor) {
		model = new TimelineModel();
		model.addListener(new TimelineModel.EventListener() {
			@Override public void stateChanged() {resetTweens();}
		});

		for (Tweenable tweenable : tweenables) {
			List<Property> properties = editor.getProperties(tweenable.getClass());
			Element elem = model.addElement(namesMap.get(tweenable));
			elem.setSelectable(false);

			for (Property property : properties) {
				elem = model.addElement(namesMap.get(tweenable) + "/" + property.getName());
				elem.setUserData(new ElementData(tweenable, property.getId()));
			}
		}
	}

	private void createWindow(final TimelineModel model, Editor editor) {
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
	}

	private void resetTweens() {
		tweenManager.clear();

		for (Element elem : model.getElements()) {
			if (!elem.isSelectable()) continue;

			ElementData elemData = (ElementData) elem.getUserData();
			Tweenable tweenable = elemData.getTweenable();
			int tweenType = elemData.getTweenType();

			createTweens(elem, tweenable, tweenType);
			setToInitialState(tweenable, tweenType);
		}

		int accTime = 0, delta = 100, duration = model.getDuration();
		while (accTime <= duration + delta) {
			accTime += delta;
			tweenManager.update(delta);
		}

		int currentTime = wnd.getTimeCursorPosition();
		tweenManager.update(-accTime-1);
		tweenManager.update(currentTime+1);
	}

	private void createTweens(Element elem, Tweenable tweenable, int tweenType) {
		for (Node node : elem.getNodes()) {
			if (node.getUserData() == null) createNodeData(node);
			NodeData nodeData = (NodeData) node.getUserData();
			
			Tween.to(tweenable, tweenType, node.getDuration(), nodeData.getEquation())
				.target(nodeData.getTargets())
				.delay(node.getStart())
				.repeat(0, Integer.MAX_VALUE - node.getEnd())
				.addToManager(tweenManager);
		}
	}

	private void createNodeData(Node node) {
		ElementData elemData = (ElementData) node.getParent().getUserData();
		Property property = editor.getProperty(elemData.getTweenable().getClass(), elemData.getTweenType());

		NodeData nodeData = new NodeData(property.getCombinedTweensCount());
		elemData.getTweenable().getTweenValues(elemData.getTweenType(), nodeData.getTargets());
		node.setUserData(nodeData);
	}

	private void setToInitialState(Tweenable tweenable, int tweenType) {
		InitialState initState = initialStatesMap.get(tweenable);
		float[] initValues = initState.getValues(tweenType);
		tweenable.onTweenUpdated(tweenType, initValues);
	}

	// -------------------------------------------------------------------------
	// Window Callback
	// -------------------------------------------------------------------------

	private MainWindow.Callback wndCallback = new MainWindow.Callback() {
		@Override
		public void timeCursorPositionChanged(int oldTime, int newTime) {
			tweenManager.update(-oldTime-1);
			tweenManager.update(newTime+1);
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
