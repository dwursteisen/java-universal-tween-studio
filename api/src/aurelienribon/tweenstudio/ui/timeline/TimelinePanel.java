package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TimelinePanel extends JPanel {
	private final MenuBarPanel menuBarPanel;
	private final NamesPanel namesPanel;
	private final GridPanel gridPanel;
	private final ScrollBar vScrollBar;
	private final ScrollBar hScrollBar;

	public TimelinePanel() {
		Theme theme = new Theme(null);

		menuBarPanel = new MenuBarPanel(theme);
		namesPanel = new NamesPanel(theme);
		gridPanel = new GridPanel(theme);
		vScrollBar = new ScrollBar(theme);
		hScrollBar = new ScrollBar(theme);

		buildLayout();
		addEventListeners();
    }

	public void setModel(TimelineModel model) {
		gridPanel.setModel(model);
		namesPanel.setModel(model);
	}

	public void setTheme(Theme theme) {
		menuBarPanel.setTheme(theme);
		namesPanel.setTheme(theme);
		gridPanel.setTheme(theme);
		vScrollBar.setTheme(theme);
		hScrollBar.setTheme(theme);
		repaint();
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void buildLayout() {
		vScrollBar.setPreferredSize(new Dimension(15, 0));
		hScrollBar.setPreferredSize(new Dimension(0, 15));

		JPanel cornerPanel = new JPanel();
		cornerPanel.setPreferredSize(new Dimension(15, 15));
		cornerPanel.setBackground(Color.RED);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(hScrollBar, BorderLayout.CENTER);
		southPanel.add(cornerPanel, BorderLayout.EAST);

		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPanel.add(namesPanel, JSplitPane.LEFT);
		splitPanel.add(gridPanel, JSplitPane.RIGHT);

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

		gridPanel.addListener(new GridPanel.EventListener() {
			@Override public void timeCursorMoved(int newTime) {menuBarPanel.setTime(newTime);}
			@Override public void selectedElementChanged(Element selectedElement) {namesPanel.setSelectedElement(selectedElement);}
			@Override public void lengthChanged() {hScrollBar.repaint();}
		});

		menuBarPanel.addListener(new MenuBarPanel.EventListener() {
			@Override public void magnifyRequested() {gridPanel.requestMagnification();}
			@Override public void minifyRequested() {gridPanel.requestMinification();}
			@Override public void addNodeRequested() {gridPanel.requestAddNode();}
			@Override public void delNodeRequested() {gridPanel.requestDelNode();}
			@Override public void playRequested() {firePlayRequested();}
			@Override public void goToFirstRequested() {fireGoToFirstRequested();}
			@Override public void goToPreviousRequested() {fireGoToPreviousRequested();}
			@Override public void goToNextRequested() {fireGoToNextRequested();}
			@Override public void goToLastRequested() {fireGoToLastRequested();}
		});

		namesPanel.addListener(new NamesPanel.EventListener() {
			@Override public void selectedElementChanged(Element selectedElem) {gridPanel.setSelectedElement(selectedElem);}
			@Override public void verticalOffsetChanged(int vOffset) {gridPanel.setVerticalOffset(vOffset);}
		});
	}

	// -------------------------------------------------------------------------
	// Events
	// -------------------------------------------------------------------------

	private final List<EventListener> listeners = new ArrayList<EventListener>();
	public void addListener(EventListener listener) {listeners.add(listener);}

	public interface EventListener {
		public void playRequested();
		public void goToFirstRequested();
		public void goToPreviousRequested();
		public void goToNextRequested();
		public void goToLastRequested();
	}

	private void firePlayRequested() {
		for (EventListener listener : listeners)
			listener.playRequested();
	}

	private void fireGoToFirstRequested() {
		for (EventListener listener : listeners)
			listener.goToFirstRequested();
	}

	private void fireGoToPreviousRequested() {
		for (EventListener listener : listeners)
			listener.goToPreviousRequested();
	}

	private void fireGoToNextRequested() {
		for (EventListener listener : listeners)
			listener.goToNextRequested();
	}

	private void fireGoToLastRequested() {
		for (EventListener listener : listeners)
			listener.goToLastRequested();
	}
}
