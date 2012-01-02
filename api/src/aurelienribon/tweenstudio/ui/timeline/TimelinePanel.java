package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
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

	private final List<Node> selectedNodes = new ArrayList<Node>();
	private Element selectedElement = null;
	private int currentTime = 0;
	private boolean isPlaying = false;

	public TimelinePanel() {
		this.theme = new Theme();

		menuBarPanel = new MenuBarPanel(this);
		namesPanel = new NamesPanel(this);
		gridPanel = new GridPanel(this);
		vScrollBar = new ScrollBar(this);
		hScrollBar = new ScrollBar(this);
		cornerPanel = new JPanel();

		buildLayout();
		addEventListeners();
    }

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void setModel(TimelineModel model) {
		this.model = model;
		gridPanel.modelChanged(model);
		namesPanel.modelChanged(model);
	}

	public TimelineModel getModel() {
		return model;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
		menuBarPanel.themeChanged(theme);
		namesPanel.themeChanged(theme);
		gridPanel.themeChanged(theme);
		vScrollBar.themeChanged(theme);
		hScrollBar.themeChanged(theme);
		cornerPanel.setBackground(theme.COLOR_GRIDPANEL_BACKGROUND);
	}

	public Theme getTheme() {
		return theme;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
		if (isPlaying) menuBarPanel.setPauseBtnVisible();
		else menuBarPanel.setPlayBtnVisible();
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setSelectedElement(Element elem) {
		if (elem != null) clearSelectedNodes();
		Element oldElem = selectedElement;
		selectedElement = elem;
		if (oldElem != elem) fireSelectedElementChanged(elem, oldElem);
	}

	public Element getSelectedElement() {
		return selectedElement;
	}

	public void addSelectedNode(Node node) {
		setSelectedElement(null);
		assert node != null;
		if (!selectedNodes.contains(node)) {
			List<Node> oldNodes = Collections.unmodifiableList(selectedNodes);
			selectedNodes.add(node);
			List<Node> newNodes = Collections.unmodifiableList(selectedNodes);
			fireSelectedNodesChanged(newNodes, oldNodes);
		}
	}

	public void removeSelectedNode(Node node) {
		setSelectedElement(null);
		assert node != null;
		if (selectedNodes.contains(node)) {
			List<Node> oldNodes = Collections.unmodifiableList(selectedNodes);
			selectedNodes.remove(node);
			List<Node> newNodes = Collections.unmodifiableList(selectedNodes);
			fireSelectedNodesChanged(newNodes, oldNodes);
		}
	}

	public void setSelectedNode(Node node) {
		setSelectedElement(null);
		assert node != null;
		clearSelectedNodes();
		addSelectedNode(node);
	}

	public void clearSelectedNodes() {
		if (!selectedNodes.isEmpty()) {
			List<Node> oldNodes = Collections.unmodifiableList(selectedNodes);
			selectedNodes.clear();
			List<Node> newNodes = Collections.unmodifiableList(selectedNodes);
			fireSelectedNodesChanged(newNodes, oldNodes);
		}
	}

	public List<Node> getSelectedNodes() {
		return Collections.unmodifiableList(selectedNodes);
	}

	public void setCurrentTime(int time) {
		if (time != currentTime) {
			int oldTime = currentTime;
			currentTime = time;
			menuBarPanel.setTime(time);
			fireCurrentTimeChanged(time, oldTime);
		}
	}

	public int getCurrentTime() {
		return currentTime;
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
			@Override public void lengthChanged() {hScrollBar.repaint();}
			@Override public void scrollRequired(int amount) {vScrollBar.scroll(amount);}
		});

		menuBarPanel.setCallback(new MenuBarPanel.Callback() {
			@Override public void magnifyRequested() {gridPanel.requestMagnification();}
			@Override public void minifyRequested() {gridPanel.requestMinification();}
			@Override public void playRequested() {firePlayRequested();}
			@Override public void pauseRequested() {firePauseRequested();}
		});

		namesPanel.setCallback(new NamesPanel.Callback() {
			@Override public void verticalOffsetChanged(int vOffset) {gridPanel.setVerticalOffset(vOffset);}
			@Override public void scrollRequired(int amount) {vScrollBar.scroll(amount);}
		});
	}

	// -------------------------------------------------------------------------
	// Events
	// -------------------------------------------------------------------------

	private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
	public void addListener(Listener listener) {listeners.add(listener);}

	public interface Listener {
		public void playRequested();
		public void pauseRequested();
		public void selectedElementChanged(Element newElem, Element oldElem);
		public void selectedNodesChanged(List<Node> newNodes, List<Node> oldNodes);
		public void currentTimeChanged(int newTime, int oldTime);
	}

	private void firePlayRequested() {
		for (Listener listener : listeners)
			listener.playRequested();
	}

	private void firePauseRequested() {
		for (Listener listener : listeners)
			listener.pauseRequested();
	}

	private void fireSelectedElementChanged(Element newElem, Element oldElem) {
		for (Listener listener : listeners)
			listener.selectedElementChanged(newElem, oldElem);
	}

	private void fireSelectedNodesChanged(List<Node> newNodes, List<Node> oldNodes) {
		for (Listener listener : listeners)
			listener.selectedNodesChanged(newNodes, oldNodes);
	}

	private void fireCurrentTimeChanged(int newTime, int oldTime) {
		for (Listener listener : listeners)
			listener.currentTimeChanged(newTime, oldTime);
	}
}
