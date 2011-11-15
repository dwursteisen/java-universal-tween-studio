package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.MatteBorder;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TimelinePanel extends JPanel {
	private final MenuBarPanel menuBarPanel;
	private final NamesPanel namesPanel;
	private final GridPanel gridPanel;
	private final ScrollBar vScrollBar;
	private final ScrollBar hScrollBar;
	private final JPanel cornerPanel;

	private Theme theme;
	private TimelineModel model;
	private boolean playing;

	public TimelinePanel() {
		this.theme = new Theme(null);

		menuBarPanel = new MenuBarPanel(theme);
		namesPanel = new NamesPanel(theme);
		gridPanel = new GridPanel(theme);
		vScrollBar = new ScrollBar(theme);
		hScrollBar = new ScrollBar(theme);
		cornerPanel = new JPanel();

		buildLayout();
		addEventListeners();
    }

	public void setModel(TimelineModel model) {
		this.model = model;
		gridPanel.setModel(model);
		namesPanel.setModel(model);
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
		menuBarPanel.setTheme(theme);
		namesPanel.setTheme(theme);
		gridPanel.setTheme(theme);
		vScrollBar.setTheme(theme);
		hScrollBar.setTheme(theme);
		cornerPanel.setBackground(theme.COLOR_GRIDPANEL_BACKGROUND);
		repaint();
	}

	public Theme getTheme() {
		return theme;
	}

	public int getTimeCursorPosition() {
		return gridPanel.getCurrentTime();
	}

	public void setTimeCursorPosition(int millis) {
		gridPanel.setCurrentTime(millis);
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
		gridPanel.setPlaying(playing);
		
		if (playing) {
			menuBarPanel.setPauseBtnVisible();
		} else {
			menuBarPanel.setPlayBtnVisible();
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void buildLayout() {
		vScrollBar.setPreferredSize(new Dimension(15, 0));
		hScrollBar.setPreferredSize(new Dimension(0, 15));

		cornerPanel.setPreferredSize(new Dimension(15, 15));
		cornerPanel.setBackground(theme.COLOR_GRIDPANEL_BACKGROUND);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(hScrollBar, BorderLayout.CENTER);
		southPanel.add(cornerPanel, BorderLayout.EAST);

		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPanel.setBorder(null);
		splitPanel.setDividerLocation(200);
		splitPanel.setDividerSize(3);
		splitPanel.add(namesPanel, JSplitPane.LEFT);
		splitPanel.add(gridPanel, JSplitPane.RIGHT);

		menuBarPanel.setBorder(new MatteBorder(0, 0, 1, 0, theme.COLOR_SEPARATOR));

		setLayout(new BorderLayout());
		add(menuBarPanel, BorderLayout.NORTH);
		add(vScrollBar, BorderLayout.EAST);
		add(southPanel, BorderLayout.SOUTH);
		add(splitPanel, BorderLayout.CENTER);

		vScrollBar.setOrientation(ScrollBar.Orientation.VERTICAL);
		hScrollBar.setOrientation(ScrollBar.Orientation.HORIZONTAL);
		vScrollBar.setScrollable(namesPanel);
		hScrollBar.setScrollable(gridPanel);
	}

	private void addEventListeners() {
		namesPanel.addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent e) {vScrollBar.repaint();}
		});

		gridPanel.addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent e) {hScrollBar.repaint();}
		});

		gridPanel.setCallback(new GridPanel.Callback() {
			@Override public void currentTimeChanged(int oldTime, int newTime) {menuBarPanel.setTime(newTime); fireTimeCursorPositionChanged(oldTime, newTime);}
			@Override public void selectedElementChanged(Element selectedElement) {namesPanel.setSelectedElementSilently(selectedElement); fireSelectedElementChanged(selectedElement);}
			@Override public void selectedNodeChanged(Node selectedNode) {fireSelectedNodeChanged(selectedNode);}
			@Override public void lengthChanged() {hScrollBar.repaint();}
		});

		menuBarPanel.setCallback(new MenuBarPanel.Callback() {
			@Override public void magnifyRequested() {gridPanel.requestMagnification();}
			@Override public void minifyRequested() {gridPanel.requestMinification();}
			@Override public void addNodeRequested() {gridPanel.requestAddNode();}
			@Override public void delNodeRequested() {gridPanel.requestDelNode();}
			@Override public void playRequested() {firePlayRequested();}
			@Override public void pauseRequested() {firePauseRequested();}
			@Override public void goToFirstRequested() {setTimeCursorPosition(TimelineHelper.getFirstTime(model));}
			@Override public void goToPreviousRequested() {setTimeCursorPosition(TimelineHelper.getPreviousTime(model, getTimeCursorPosition()));}
			@Override public void goToNextRequested() {setTimeCursorPosition(TimelineHelper.getNextTime(model, getTimeCursorPosition()));}
			@Override public void goToLastRequested() {setTimeCursorPosition(TimelineHelper.getLastTime(model));}
		});

		namesPanel.setCallback(new NamesPanel.Callback() {
			@Override public void selectedElementChanged(Element selectedElem) {gridPanel.setSelectedElementSilently(selectedElem);fireSelectedElementChanged(selectedElem);}
			@Override public void verticalOffsetChanged(int vOffset) {gridPanel.setVerticalOffset(vOffset);}
		});
	}

	// -------------------------------------------------------------------------
	// Events
	// -------------------------------------------------------------------------

	private final List<EventListener> listeners = new CopyOnWriteArrayList<EventListener>();
	public void addListener(EventListener listener) {listeners.add(listener);}

	public interface EventListener {
		public void selectedElementChanged(Element element);
		public void selectedNodeChanged(Node node);
		public void playRequested();
		public void pauseRequested();
		public void timeCursorPositionChanged(int oldTime, int newTime);
	}

	private void fireSelectedElementChanged(Element selectedElement) {
		for (EventListener listener : listeners)
			listener.selectedElementChanged(selectedElement);
	}

	private void fireSelectedNodeChanged(Node selectedNode) {
		for (EventListener listener : listeners)
			listener.selectedNodeChanged(selectedNode);
	}

	private void firePlayRequested() {
		for (EventListener listener : listeners)
			listener.playRequested();
	}

	private void firePauseRequested() {
		for (EventListener listener : listeners)
			listener.pauseRequested();
	}

	private void fireTimeCursorPositionChanged(int oldTime, int newTime) {
		for (EventListener listener : listeners)
			listener.timeCursorPositionChanged(oldTime, newTime);
	}
}
