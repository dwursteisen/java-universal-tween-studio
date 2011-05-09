package aurelienribon.tweenstudio.ui.timeline2;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import aurelienribon.tweenstudio.ui.timeline.components.Scrollable;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JPanel;

public class GridPanel extends JPanel implements Scrollable {
	private final int oneSecondWidth = 100;
	private final int leftDeadZone = 15;
	private final int paddingTop = 30;
	private final int lineHeight = 20;
	private final int nodeWidth = 7;

	private TimelineModel model;
	private int leftAreaWidth = 120;
	private float timeScale = 1;
	private int hScrollOffset = 0;
	private int vScrollOffset = 0;
	private int selectedLine = -1;
	private int mouseOverLine = -1;
	private int currentTime = 0;

	public GridPanel() {
		TimelineEvents.instance().addListener(new TimelineEvents.ModelChangedListener() {
			@Override
			public void onEvent(TimelineModel newModel) {
				model = newModel;
			}
		});

		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);
		setOpaque(false);
	}

	// -------------------------------------------------------------------------
	// Drawing
	// -------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gg = (Graphics2D)g;
		vScrollOffset = (int) Math.max(getHeight() - getPreferredHeight(), vScrollOffset);
		vScrollOffset = Math.min(0, vScrollOffset);

		// Grid line background
		gg.translate(0, vScrollOffset);
		gg.setColor(Theme.COLOR_GRID_LINES_BACKGROUND);
		gg.fillRect(leftAreaWidth, paddingTop+1, getWidth(), getModelLineCnt() * lineHeight);
		gg.translate(0, -vScrollOffset);

		// Grid line separators
		gg.translate(0, vScrollOffset);
		for (int i=1; i<getModelLineCnt(); i++) {
			gg.setColor(Theme.COLOR_GRID_BACKGROUND);
			gg.drawLine(
				leftAreaWidth,
				paddingTop + lineHeight * i,
				getWidth(),
				paddingTop + lineHeight * i);
		}
		gg.translate(0, -vScrollOffset);

		// Others
		gg.translate(0, vScrollOffset);
		drawNodeBackgrounds(gg);
		drawTimeline(gg);
		drawTimeCursor(gg);
		drawNodes(gg);
		gg.translate(0, -vScrollOffset);

		// Left area
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.drawLine(leftAreaWidth, 0, leftAreaWidth, getHeight());
		gg.translate(0, vScrollOffset);
		drawHighlightedLines(gg);
		drawNames(gg);
		gg.translate(0, -vScrollOffset);
	}

	private void drawNames(Graphics2D gg) {
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.setFont(Theme.FONT);
		int y = paddingTop + 14;

		for (String target : model.getTargets()) {
			gg.drawString(target, 5, y);
			y += lineHeight;

			for (String attr : model.getAttrs(target)) {
				gg.drawString(attr, 30, y);
				y += lineHeight;
			}
		}
	}

	private void drawHighlightedLines(Graphics2D gg) {
		if (selectedLine >= 0) {
			int y = getYFromLine(selectedLine);
			gg.setColor(Theme.COLOR_HIGHLIGHT);
			gg.fillRect(5, y, 100, lineHeight);
		}

		if (mouseOverLine >= 0 && mouseOverLine != selectedLine) {
			int y = getYFromLine(mouseOverLine);
			gg.setColor(Theme.COLOR_HIGHLIGHT_ALT);
			gg.fillRect(5, y, 100, lineHeight);
		}
	}

	private void drawTimeline(Graphics2D gg) {
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.setFont(Theme.FONT);
		FontMetrics fm = gg.getFontMetrics();

		int minSeconds = (int) (-hScrollOffset / oneSecondWidth * timeScale);
		int maxSeconds = (int) (getWidth() / oneSecondWidth * timeScale + 1) + minSeconds + 5;

		int textOffset = -12;
		int smallTickHeight = 5;
		int bigTickHeight = 7;

		for (int i=minSeconds; i<=maxSeconds; i++) {
			int x = getXFromTime(i*1000);
			String str = String.valueOf(i);
			gg.drawString(str, x - fm.stringWidth(str)/2, paddingTop + textOffset);
			gg.drawRect(x, paddingTop - bigTickHeight, 2, getHeight());

			for (int ii=1; ii<10; ii++) {
				int xx = getXFromTime(ii*100 + i*1000);
				gg.drawLine(xx, paddingTop - (ii == 5 ? bigTickHeight : smallTickHeight), xx, paddingTop);
			}
		}
	}

	private void drawTimeCursor(Graphics2D gg) {
		int x = getXFromTime(currentTime);
		int y = paddingTop;
		gg.setColor(Theme.COLOR_CURSOR);
		gg.fillPolygon(
			new int[] {x - 8, x, x + 8},
			new int[] {y - 9, y, y - 9},
			3);
		gg.fillRect(x - 1, y - 5, 3, getHeight()-vScrollOffset);
	}

	private void drawNodeBackgrounds(final Graphics2D gg) {
		forAllNodes(new NodeAction() {
			@Override public boolean act(String target, String attr, Node node, int line) {
				int x1 = getXFromTime(node.delayMillis);
				int x2 = getXFromTime(node.delayMillis + node.durationMillis);
				int y = getYFromLine(line);
				gg.setColor(Theme.COLOR_GRID_NODES_BACKGROUND);
				gg.fillRect(x1, y+2, x2-x1, lineHeight-3);
				return false;
			}
		});

		forAllNodes(new NodeAction() {
			@Override
			public boolean act(String target, String attr, Node node, int line) {
				int y = getYFromLine(line);
				Node[] nodes = model.getNodes(target, attr);
				for (Node n : nodes) {
					if (n != node
					&& n.delayMillis < node.delayMillis + node.durationMillis
					&& n.delayMillis >= node.delayMillis) {
						int x3 = getXFromTime(n.delayMillis);
						int x4 = getXFromTime(Math.min(n.delayMillis + n.durationMillis, node.delayMillis + node.durationMillis));
						gg.setColor(Color.RED);
						gg.fillRect(x3, y+2, x4-x3, lineHeight-3);
					}
				}
				return false;
			}
		});
	}

	private void drawNodes(final Graphics2D gg) {
		forAllNodes(new NodeAction() {
			@Override public boolean act(String target, String attr, Node node, int line) {
				int x = getXFromTime(node.delayMillis + node.durationMillis);
				int y = getYFromLine(line);
				gg.setColor(Theme.COLOR_GRID_NODES_FILL);
				gg.fillOval(x-4, y+3, nodeWidth, lineHeight-6);
				gg.setColor(Theme.COLOR_GRID_BACKGROUND);
				gg.drawOval(x-4, y+3, nodeWidth, lineHeight-6);
				return false;
			}
		});

		forAllNodes(new NodeAction() {
			@Override public boolean act(String target, String attr, Node node, int line) {
				int x = getXFromTime(node.delayMillis + node.durationMillis);
				int y = getYFromLine(line);
				Node[] nodes = model.getNodes(target, attr);
				for (Node n : nodes) {
					if (n != node && n.durationMillis + n.delayMillis == node.durationMillis + node.delayMillis) {
						gg.setColor(Color.RED);
						gg.fillOval(x-4, y+3, nodeWidth, lineHeight-6);
						gg.setColor(Theme.COLOR_GRID_BACKGROUND);
						gg.drawOval(x-4, y+3, nodeWidth, lineHeight-6);
					}
				}
				return false;
			}
		});
	}

	// -------------------------------------------------------------------------
	// Utils
	// -------------------------------------------------------------------------

	private int getTimeFromX(int x) {
		int time = (int) ((x - leftAreaWidth - leftDeadZone - hScrollOffset) * 1000f / oneSecondWidth * timeScale);
		time = time > 0 ? time : 0;
		time = Math.round(time / 100f) * 100;
		return Math.max(time, 0);
	}

	private int getXFromTime(int millis) {
		return (int) (millis / 1000f * oneSecondWidth / timeScale + leftAreaWidth + leftDeadZone + hScrollOffset);
	}

	private int getLineFromY(int y) {
		if (y < paddingTop) return -1;
		return (y - paddingTop) / lineHeight;
	}

	private int getYFromLine(int line) {
		return paddingTop + lineHeight * line;
	}

	private int getModelLineCnt() {
		int cnt = 0;
		for (String target : model.getTargets())
			cnt += 1 + model.getAttrs(target).length;
		return cnt;
	}

	private boolean isLineValid(int line) {
		if (0 <= line && line < getModelLineCnt()) {
			int cnt = 0;
			for (String target : model.getTargets()) {
				if (line == cnt)
					return false;
				cnt += 1 + model.getAttrs(target).length;
			}
			return true;
		}
		return false;
	}

	private void forAllNodes(NodeAction action) {
		int line = -1;
		String[] targets = model.getTargets();
		for (int i=0; i<targets.length; i++) {
			line += 1;
			String target = targets[i];
			String[] attrs = model.getAttrs(target);
			for (int j=0; j<attrs.length; j++) {
				line += 1;
				String attr = attrs[j];
				Node[] nodes = model.getNodes(target, attr);
				for (Node node : nodes) {
					if (action.act(target, attr, node, line))
						return;
				}
			}
		}
	}

	private interface NodeAction {
		public boolean act(String target, String attr, Node node, int line);
	}

	// -------------------------------------------------------------------------
	// Scrollable
	// -------------------------------------------------------------------------

	@Override
	public void requestHorizontalScroll(float speed) {
		hScrollOffset -= speed * 20;
		hScrollOffset = Math.min(hScrollOffset, 0);
	}

	@Override
	public void requestVerticalScroll(float position) {
		float currentHeight = getHeight() - 25;
		float preferredHeight = getModelLineCnt() * lineHeight + paddingTop;
		vScrollOffset = (int) -(position * (preferredHeight - currentHeight));
	}

	@Override
	public int getPreferredHeight() {
		return getModelLineCnt() * lineHeight + paddingTop;
	}

	@Override
	public int getVerticalOffset() {
		return -vScrollOffset;
	}

	// -------------------------------------------------------------------------
	// Input
	// -------------------------------------------------------------------------

	public final MouseAdapter mouseAdapter = new MouseAdapter() {
		private boolean isCursorDragged = false;
		private Node draggedNode = null;

		@Override
		public void mousePressed(MouseEvent e) {
			if (mouseOverLine >= 0)
				selectedLine = mouseOverLine;
			getParent().repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (draggedNode != null && e.isShiftDown()) {
				int time = getTimeFromX(e.getX());
				draggedNode.durationMillis = Math.max(0, time - draggedNode.delayMillis);
				getParent().repaint();
			} else if (draggedNode != null && !e.isShiftDown()) {
				int time = getTimeFromX(e.getX());
				draggedNode.delayMillis = Math.max(0, time - draggedNode.durationMillis);
				getParent().repaint();
			} else if (isCursorDragged) {
				int time = getTimeFromX(e.getX());
				currentTime = time;
				getParent().repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isCursorDragged = false;
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			// Time cursor test
			int x = getXFromTime(currentTime);
			isCursorDragged = (x - 2 <= e.getX() && e.getX() <= x + 2) && (e.getY()-vScrollOffset >= paddingTop - 9);

			// Node test
			draggedNode = null;
			forAllNodes(new NodeAction() {
				@Override public boolean act(String target, String attr, Node node, int line) {
					if ((getTimeFromX(e.getX()) == node.delayMillis + node.durationMillis) && getLineFromY(e.getY()-vScrollOffset) == line) {
						draggedNode = node;
						return true;
					}
					return false;
				}
			});

			// Selected Lines
			mouseOverLine = -1;
			if (e.getX() <= leftAreaWidth) {
				int line = getLineFromY(e.getY()-vScrollOffset);
				if (isLineValid(line))
					mouseOverLine = line;
			}

			// Cursor change
			setCursor(isCursorDragged || draggedNode != null ? Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR) : Cursor.getDefaultCursor());
			getParent().repaint();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			timeScale *= e.getWheelRotation() > 0 ? 1.1f : 0.9f;
			timeScale = Math.min(timeScale, 3);
			timeScale = Math.max(timeScale, 0.25f);
			getParent().repaint();
		}
	};
}
