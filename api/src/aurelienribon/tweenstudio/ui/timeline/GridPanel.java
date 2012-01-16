package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import aurelienribon.tweenstudio.ui.timeline.TimelinePanel.PushBehavior;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class GridPanel extends JPanel implements Scrollable {
	private final int oneSecondWidth = 60;
	private final int paddingTop = 30;
	private final int paddingLeft = 15;
	private final int lineHeight = 20;
	private final int lineGap = 1;
	private final int linePadding = 3;
	private final int nodeWidth = 8;

	private final TimelinePanel parent;
	private Callback callback;
	private float timeScale = 1;
	private Element mouseOverProperty = null;
	private Node mouseOverNode = null;
	private boolean isOverTrack = false;
	private boolean isOverTrackGrip = false;
	private Rectangle selectionRect = null;

	private int vOffset;
	private int hOffset;
	private int maxTime = 0;

	public GridPanel(TimelinePanel parent) {
		this.parent = parent;
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);
		addKeyListener(keyAdapter);
		setFocusable(true);

		parent.addListener(new TimelinePanel.Listener() {
			@Override public void playRequested() {}
			@Override public void pauseRequested() {}
			@Override public void selectedElementsChanged(List<Element> newElems, List<Element> oldElems) {}
			@Override public void selectedNodesChanged(List<Node> newNodes, List<Node> oldNodes) {repaint();}
			@Override public void mouseOverElementChanged(Element newElem, Element oldElem) {}
			@Override public void currentTimeChanged(int newTime, int oldTime) {repaint();}
		});
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void modelChanged(TimelineModel model) {
		model.addListener(new TimelineModel.Listener() {
			@Override public void stateChanged() {repaint();}
		});
		repaint();
	}

	public void themeChanged(Theme theme) {
		repaint();
	}

	public void requestMagnification() {
		timeScale /= 1.2f;
		repaint();
		callback.lengthChanged();
	}

	public void requestMinification() {
		timeScale *= 1.2f;
		repaint();
		callback.lengthChanged();
	}

	public void setVerticalOffset(int vOffset) {
		this.vOffset = vOffset;
		repaint();
	}

	@Override
	public int getViewLength() {
		return getWidth();
	}

	@Override
	public int getLength() {
		return getXFromTime(maxTime) + hOffset + 200;
	}

	@Override
	public int getOffset() {
		return hOffset;
	}

	@Override
	public void setOffset(int offset) {
		this.hOffset = offset;
		repaint();
	}

	// -------------------------------------------------------------------------
	// Callback
	// -------------------------------------------------------------------------

	public interface Callback {
		public void lengthChanged();
		public void scrollRequired(int amount);
	}

	// -------------------------------------------------------------------------
	// Painting
	// -------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics g) {
		Theme theme = parent.getTheme();
		TimelineModel model = parent.getModel();
		int currentTime = parent.getCurrentTime();
		
		Graphics2D gg = (Graphics2D)g;
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		gg.setColor(theme.COLOR_GRIDPANEL_BACKGROUND);
		gg.fillRect(0, 0, getWidth(), getHeight());
		
		if (model == null) return;

		paintSections(gg);
		paintNodesTracks(gg);
		paintNodes(gg);
		paintTimeline(gg);
		paintTimeCursor(gg);
		paintSelectionRect(gg);

		int oldMaxTime = maxTime;
		maxTime = Math.max(model.getDuration(), currentTime);
		if (maxTime != oldMaxTime) callback.lengthChanged();
	}

	private void paintSections(final Graphics2D gg) {
		Theme theme = parent.getTheme();
		TimelineModel model = parent.getModel();

		int line = 0;
		for (Element elem : model.getElements()) {
			int y = getYFromLine(line);
			
			if (elem.isSelectable()) gg.setColor(theme.COLOR_GRIDPANEL_SECTION);
			else gg.setColor(theme.COLOR_GRIDPANEL_SECTION_UNUSABLE);

			gg.fillRect(0, y, getWidth(), lineHeight);
			line += 1;
		}
	}

	private void paintNodesTracks(final Graphics2D gg) {
		Theme theme = parent.getTheme();
		TimelineModel model = parent.getModel();

		int line = 0;
		for (Element elem : model.getElements()) {
			int y = getYFromLine(line), lastX = getXFromTime(0);

			for (Node node : elem.getNodes()) {
				if (node.isLinked()) {
					gg.setColor(isOverTrack && elem == mouseOverProperty
						? theme.COLOR_GRIDPANEL_TRACK_MOUSEOVER
						: theme.COLOR_GRIDPANEL_TRACK);
				} else {
					gg.setColor(isOverTrack && elem == mouseOverProperty
						? theme.COLOR_GRIDPANEL_TRACK_UNLINKED_MOUSEOVER
						: theme.COLOR_GRIDPANEL_TRACK_UNLINKED);
				}

				int x = getXFromTime(node.getTime());
				gg.fillRect(lastX, y+linePadding, x-lastX, lineHeight-linePadding*2);
				lastX = x;
			}

			if (elem.getNodes().size() > 1) {
				gg.setColor(isOverTrackGrip && mouseOverProperty == elem ? theme.COLOR_GRIDPANEL_TRACK_GRIP_MOUSEOVER : theme.COLOR_GRIDPANEL_TRACK_GRIP);
				gg.fillRect(lastX, y+linePadding, 10, lineHeight-linePadding*2);
			}

			line += 1;
		}
	}

	private void paintNodes(final Graphics2D gg) {
		Theme theme = parent.getTheme();
		TimelineModel model = parent.getModel();
		List<Node> selectedNodes = parent.getSelectedNodes();

		int line = 0;
		for (Element elem : model.getElements()) {
			int y = getYFromLine(line);
			List<Node> nodes = elem.getNodes();

			for (Node node : nodes) {
				int x = getXFromTime(node.getTime());

				Color fill = theme.COLOR_GRIDPANEL_NODE_FILL;
				Color stroke = theme.COLOR_GRIDPANEL_NODE_STROKE;

				if (selectedNodes.contains(node)) {
					fill = theme.COLOR_GRIDPANEL_NODE_FILL_SELECTED;
					stroke = theme.COLOR_GRIDPANEL_NODE_STROKE_SELECTED;
				} else if (node == mouseOverNode) {
					fill = theme.COLOR_GRIDPANEL_NODE_FILL_MOUSEOVER;
					stroke = theme.COLOR_GRIDPANEL_NODE_STROKE_MOUSEOVER;
				}

				gg.setColor(fill);
				gg.fillOval(x-4, y+linePadding+1, nodeWidth, lineHeight-(linePadding+1)*2-1);

				gg.setColor(stroke);
				gg.drawOval(x-4, y+linePadding+1, nodeWidth, lineHeight-(linePadding+1)*2-1);
			}

			line += 1;
		}
	}

	private void paintTimeline(Graphics2D gg) {
		Theme theme = parent.getTheme();
		int minSeconds = Math.max((int) (timeScale * hOffset / oneSecondWidth), 0);
		int maxSeconds = (int) (timeScale * getWidth() / oneSecondWidth) + minSeconds + 1;
		int textOffset = -12;
		int tickHeight = 5;

		gg.setColor(theme.COLOR_GRIDPANEL_BACKGROUND);
		gg.fillRect(0, 0, getWidth(), paddingTop);

		gg.setFont(theme.FONT);
		FontMetrics fm = gg.getFontMetrics();

		for (int i=minSeconds; i<=maxSeconds; i++) {
			int x = getXFromTime(i*1000000);

			String str = String.valueOf(i);
			gg.setColor(theme.COLOR_FOREGROUND);
			gg.drawString(str, x - fm.stringWidth(str)/2, paddingTop + textOffset);

			gg.setColor(theme.COLOR_GRIDPANEL_TIMELINE);
			gg.fillRect(x-1, paddingTop - tickHeight, 2, getHeight());
			for (int ii=1; ii<10; ii++) {
				int xx = getXFromTime(ii*100000 + i*1000000);
				gg.drawLine(xx, paddingTop - tickHeight, xx, paddingTop-1);
			}
		}
	}

	private void paintTimeCursor(Graphics2D gg) {
		Theme theme = parent.getTheme();
		int currentTime = parent.getCurrentTime();

		int x = getXFromTime(currentTime);
		int y = paddingTop;

		gg.setColor(theme.COLOR_GRIDPANEL_CURSOR);
		gg.fillPolygon(new int[] {x - 8, x, x + 8}, new int[] {y - 9, y, y - 9}, 3);
		gg.fillRect(x, y - 5, 1, getHeight());
	}

	private void paintSelectionRect(Graphics2D gg) {
		if (selectionRect == null) return;
		Theme theme = parent.getTheme();

		int x = Math.min(selectionRect.x, selectionRect.x + selectionRect.width);
		int y = Math.min(selectionRect.y, selectionRect.y + selectionRect.height);
		int w = Math.abs(selectionRect.width);
		int h = Math.abs(selectionRect.height);

		Stroke stroke = gg.getStroke();
		gg.setStroke(new BasicStroke(3));

		gg.setColor(theme.COLOR_GRIDPANEL_SELECTION_FILL);
		gg.fillRect(x, y, w, h);
		gg.setColor(theme.COLOR_GRIDPANEL_SELECTION_STROKE);
		gg.drawRect(x, y, w, h);

		gg.setStroke(stroke);
	}

	// -------------------------------------------------------------------------
	// Input -- Mouse
	// -------------------------------------------------------------------------

	private enum MouseState {DRAW_SELECTION, DRAG_CURSOR, DRAG_NODES, DRAG_TRACK, DRAG_GRIP}

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		private MouseState state;
		private boolean isPressed = false;
		private boolean isDragged = false;
		private int lastX;

		@Override
		public void mousePressed(MouseEvent e) {
			if (parent.getModel() == null || parent.isPlaying()) return;
			if (e.getButton() != MouseEvent.BUTTON1) return;

			int eLine = getLineFromY(e.getY());

			if (eLine < 0) {
				state = MouseState.DRAG_CURSOR;
				parent.setCurrentTime(getRoundedTime(getTimeFromX(e.getX()), 100000));

			} else if (mouseOverNode != null) {
				state = MouseState.DRAG_NODES;
				if (e.isControlDown()) {
					parent.pushSelectedNode(mouseOverNode, PushBehavior.ADD_OR_REMOVE);
				} else if (!parent.getSelectedNodes().contains(mouseOverNode)) {
					parent.pushSelectedNode(mouseOverNode, PushBehavior.SET);
				}

			} else if (isOverTrack) {
				state = MouseState.DRAG_TRACK;

			} else if (isOverTrackGrip) {
				state = MouseState.DRAG_GRIP;
				
			} else {
				state = MouseState.DRAW_SELECTION;
				selectionRect = new Rectangle(e.getPoint());
			}

			isPressed = true;
			lastX = e.getX();
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (parent.getModel() == null || parent.isPlaying()) return;
			if (e.getButton() != MouseEvent.BUTTON1) return;

			switch (state) {
				case DRAG_NODES:
					if (isDragged) {
						for (Node node : parent.getSelectedNodes())
							node.setTime(getRoundedTime(node.getTime(), 100000));
						correctTimeline();
						parent.getModel().mute(false);
						parent.setCurrentTime(mouseOverNode.getTime());
					} else {
						parent.setCurrentTime(mouseOverNode.getTime());
					}
					break;

				case DRAG_TRACK:
					if (isDragged) {
						for (Node node : parent.getSelectedNodes())
							node.setTime(getRoundedTime(node.getTime(), 100000));
						parent.getModel().mute(false);
					} else {
						parent.clearSelectedNodes();
						parent.setCurrentTime(getRoundedTime(getTimeFromX(e.getX()), 100000));
					}
					break;

				case DRAG_GRIP:
					if (isDragged) {
						correctTimeline();
						parent.getModel().mute(false);
					} else {
						parent.clearSelectedNodes();
						parent.setCurrentTime(getRoundedTime(getTimeFromX(e.getX()), 100000));
					}
					break;

				case DRAW_SELECTION:
					if (isDragged) {
						if (!e.isControlDown()) parent.clearSelectedNodes();
						PushBehavior behavior = e.isControlDown() ? PushBehavior.ADD_OR_REMOVE : PushBehavior.ADD;
						int rx = Math.min(selectionRect.x, selectionRect.x + selectionRect.width);
						int ry = Math.min(selectionRect.y, selectionRect.y + selectionRect.height);
						int rw = Math.abs(selectionRect.width);
						int rh = Math.abs(selectionRect.height);
						selectionRect = null;
						Rectangle rect = new Rectangle(rx, ry, rw, rh);
						int line = 0;
						for (Element elem : parent.getModel().getElements()) {
							int y = getYFromLine(line) + lineHeight/2;
							for (Node node : elem.getNodes()) {
								int x = getXFromTime(node.getTime());
								if (rect.contains(x, y)) parent.pushSelectedNode(node, behavior);
							}
							line += 1;
						}
					} else {
						parent.clearSelectedNodes();
						parent.setCurrentTime(getRoundedTime(getTimeFromX(e.getX()), 100000));
					}
					break;
			}

			isPressed = false;
			isDragged = false;
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (parent.getModel() == null || parent.isPlaying()) return;

			int deltaTime = (int) ((e.getX() - lastX) * 1000000f / oneSecondWidth * timeScale);

			switch (state) {
				case DRAG_CURSOR:
					parent.setCurrentTime(getRoundedTime(getTimeFromX(e.getX()), 100000));
					break;

				case DRAG_NODES:
					parent.getModel().mute(true);
					if (deltaTime < 0) deltaTime = Math.max(deltaTime, -getMinTime(parent.getSelectedNodes()));
					for (Node n : parent.getSelectedNodes()) n.setTime(n.getTime() + deltaTime);
					break;

				case DRAG_TRACK:
					parent.getModel().mute(true);
					if (deltaTime < 0) deltaTime = Math.max(deltaTime, -getMinTime(mouseOverProperty.getNodes()));
					for (Node n : mouseOverProperty.getNodes()) n.setTime(n.getTime() + deltaTime);
					break;

				case DRAG_GRIP:
					parent.getModel().mute(true);
					int totalTime = mouseOverProperty.getLastNode().getTime();
					for (Node n : mouseOverProperty.getNodes()) n.setTime((int) (n.getTime() + deltaTime * (1 - (totalTime - n.getTime()) / (double)totalTime)));
					break;

				case DRAW_SELECTION:
					selectionRect.setSize(e.getX() - selectionRect.x, e.getY() - selectionRect.y);
					break;
			}
			
			lastX = e.getX();
			isDragged = true;
			repaint();
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			if (parent.getModel() == null || parent.isPlaying()) return;

			int eTime = getTimeFromX(e.getX());
			int eTime1 = getTimeFromX(e.getX() - 5);
			int eTime2 = getTimeFromX(e.getX() + 5);
			int eLine = getLineFromY(e.getY());

			Node oldMouseOverNode = mouseOverNode;
			Element oldMouseOverProperty = mouseOverProperty;
			boolean oldIsOverTrack = isOverTrack;
			boolean oldIsOverTrackGrip = isOverTrackGrip;

			mouseOverNode = null;
			mouseOverProperty = null;
			isOverTrack = isOverTrackGrip = false;

			int line = 0;
			for (Element elem : parent.getModel().getElements()) {
				if (eLine == line) {
					mouseOverProperty = elem;
					break;
				}
				line += 1;
			}

			if (mouseOverProperty != null) {
				for (Node node : mouseOverProperty.getNodes()){
					if (eTime1 <= node.getTime() && node.getTime() <= eTime2) {
						mouseOverNode = node;
						break;
					}
				}
			}
			
			if (mouseOverProperty != null && mouseOverProperty.getNodes().size() > 1) {
				Node n = mouseOverProperty.getLastNode();
				int nX = getXFromTime(n.getTime());
				isOverTrack = mouseOverNode == null ? 0 <= eTime && eTime <= n.getTime() : false;
				isOverTrackGrip = mouseOverNode == null && nX <= e.getX() && e.getX() <= nX+10;
			}

			if (oldMouseOverNode != mouseOverNode || oldMouseOverProperty != mouseOverProperty
				|| oldIsOverTrack != isOverTrack || oldIsOverTrackGrip != isOverTrackGrip) repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!isPressed) {
				mouseOverProperty = null;
				mouseOverNode = null;
				selectionRect = null;
				isOverTrack = false;
				isOverTrackGrip = false;
				repaint();
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			callback.scrollRequired(e.getWheelRotation() * 40);
		}
	};
	
	// -------------------------------------------------------------------------
	// Input -- Keyboard
	// -------------------------------------------------------------------------

	private final KeyAdapter keyAdapter = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (parent.getModel() == null || parent.isPlaying()) return;
			
			switch (e.getKeyCode()) {
				case KeyEvent.VK_DELETE:
					List<Node> nodes = new ArrayList<Node>(parent.getSelectedNodes());
					parent.clearSelectedNodes();
					for (Node node : nodes) node.getParent().removeNode(node);
					break;

				case KeyEvent.VK_ENTER:
					if (mouseOverProperty == null) {
						for (Element elem : parent.getSelectedElements()) {
							for (Element child : elem.getChildren())
								child.addNode(parent.getCurrentTime());
						}
					} else {
						mouseOverProperty.addNode(parent.getCurrentTime());
					}
					break;
			}
		}
	};

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private int getTimeFromX(int x) {
		int time = (int) ((x - paddingLeft + hOffset) * 1000000f / oneSecondWidth * timeScale);
		time = time > 0 ? time : 0;
		return Math.max(time, 0);
	}

	private int getXFromTime(int time) {
		return (int) (time / 1000000f * oneSecondWidth / timeScale + paddingLeft - hOffset);
	}

	private int getLineFromY(int y) {
		if (y < paddingTop) return -1;
		return (y - paddingTop + vOffset) / (lineHeight + lineGap);
	}

	private int getYFromLine(int line) {
		return paddingTop - vOffset + (lineHeight + lineGap) * line;
	}

	private int getMinTime(List<Node> nodes) {
		int t = -1;
		for (Node n : nodes)
			t = t == -1 ? n.getTime() : Math.min(t, n.getTime());
		return t;
	}

	private int getRoundedTime(int time, int roundFactor) {
		return (int) (Math.round(time / (float) roundFactor)) * roundFactor;
	}

	private void correctTimeline() {
		for (Element elem : parent.getModel().getElements()) {
			List<Node> nodes = new ArrayList<Node>();

			for (int i=0; i<elem.getNodes().size(); i++) {
				Node n1 = elem.getNodes().get(i);
				boolean isAlone = true;

				for (int j=i+1; j<elem.getNodes().size(); j++) {
					Node n2 = elem.getNodes().get(j);
					if (n1.getTime() == n2.getTime()) {
						isAlone = false;
						break;
					}
				}

				if (isAlone) nodes.add(n1);
			}

			elem.setNodes(nodes);
			elem.sortNodes();
		}
	}
}
