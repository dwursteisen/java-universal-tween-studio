package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

class DrawableGrid implements Scrollable {
	private final TimelinePanel parent;
	private final int oneSecondWidth = 100;
	private final int leftDeadZone = 10;
	private final int paddingTop = 50;
	private final int lineHeight = 20;

	private final int nodeWidth = 7;
	
	private int leftAreaWidth = 120;
	private float timeScale = 1;
	private int mouseOverLine = -1;
	private int hScrollOffset = 0;
	private int vScrollOffset = 0;

	public DrawableGrid(TimelinePanel parent) {
		this.parent = parent;
	}

	// -------------------------------------------------------------------------
	// Drawing
	// -------------------------------------------------------------------------

	public void draw(Graphics2D gg) {
		float currentHeight = parent.getHeight() - 22;
		vScrollOffset = (int) Math.max(currentHeight - getPreferredHeight(), vScrollOffset);
		vScrollOffset = Math.min(0, vScrollOffset);

		// Background
		gg.setColor(Theme.COLOR_GRID_BACKGROUND);
		gg.fillRect(leftAreaWidth, 0, parent.getWidth(), parent.getHeight());

		// Grid line background
		gg.translate(0, vScrollOffset);
		gg.setColor(Theme.COLOR_GRID_LINES_BACKGROUND);
		gg.fillRect(leftAreaWidth, paddingTop+1, parent.getWidth(), getModelLineCnt() * lineHeight);
		gg.translate(0, -vScrollOffset);

		// Grid line separators
		gg.translate(0, vScrollOffset);
		for (int i=1; i<getModelLineCnt(); i++) {
			gg.setColor(Theme.COLOR_GRID_BACKGROUND);
			gg.setStroke(Theme.STROKE_SMALL);
			gg.drawLine(
				leftAreaWidth, 
				paddingTop + lineHeight * i, 
				parent.getWidth(), 
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
		gg.setColor(Theme.COLOR_BACKGROUND);
		gg.fillRect(0, 0, leftAreaWidth, parent.getHeight());
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.setStroke(Theme.STROKE_SMALL);
		gg.drawLine(leftAreaWidth, 0, leftAreaWidth, parent.getHeight());
		gg.translate(0, vScrollOffset);
		drawHighlightedLines(gg);
		drawNames(gg);
		gg.translate(0, -vScrollOffset);
	}
	
	private void drawNames(Graphics2D gg) {
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.setFont(Theme.FONT);
		int y = paddingTop + 14;

		for (String target : parent.getModel().getTargets()) {
			gg.drawString(target, 5, y);
			y += lineHeight;

			for (String attr : parent.getModel().getAttrs(target)) {
				gg.drawString(attr, 30, y);
				y += lineHeight;
			}
		}
	}

	private void drawHighlightedLines(Graphics2D gg) {
		if (parent.getSelectedLine() >= 0) {
			int y = getYFromLine(parent.getSelectedLine());
			gg.setColor(Theme.COLOR_HIGHLIGHT);
			gg.fillRect(5, y, 100, lineHeight);
		}

		if (mouseOverLine >= 0 && mouseOverLine != parent.getSelectedLine()) {
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
		int maxSeconds = (int) (parent.getWidth() / oneSecondWidth * timeScale + 1) + minSeconds + 5;
		
		int textOffset = -12;
		int smallTickHeight = 5;
		int bigTickHeight = 7;

		for (int i=minSeconds; i<=maxSeconds; i++) {
			int x = getXFromTime(i*1000);
			String str = String.valueOf(i);
			gg.drawString(str, x - fm.stringWidth(str)/2, paddingTop + textOffset);
			gg.setStroke(Theme.STROKE_LARGE);
			gg.drawLine(x, paddingTop - bigTickHeight, x, parent.getHeight()-vScrollOffset);

			gg.setStroke(Theme.STROKE_SMALL);
			for (int ii=1; ii<10; ii++) {
				int xx = getXFromTime(ii*100 + i*1000);
				gg.drawLine(xx, paddingTop - (ii == 5 ? bigTickHeight : smallTickHeight), xx, paddingTop);
			}
		}
	}

	private void drawTimeCursor(Graphics2D gg) {
		int x = getXFromTime(parent.getCurrentTime());
		int y = paddingTop;
		gg.setColor(Theme.COLOR_CURSOR);
		gg.fillPolygon(
			new int[] {x - 8, x, x + 8},
			new int[] {y - 9, y, y - 9},
			3);
		gg.fillRect(x - 1, y - 5, 3, parent.getHeight()-vScrollOffset);
	}

	private void drawNodeBackgrounds(final Graphics2D gg) {
		forAllNodes(new NodeAction() {
			@Override public boolean act(Node node, int line) {
				int x1 = getXFromTime(node.delayMillis);
				int x2 = getXFromTime(node.delayMillis + node.durationMillis);
				int y = getYFromLine(line);
				gg.setColor(Theme.COLOR_GRID_NODES_BACKGROUND);
				gg.fillRect(x1, y+2, x2-x1, lineHeight-3);
				return false;
			}
		});
	}

	private void drawNodes(final Graphics2D gg) {
		forAllNodes(new NodeAction() {
			@Override public boolean act(Node node, int line) {
				int x = getXFromTime(node.delayMillis + node.durationMillis);
				int y = getYFromLine(line);
				gg.setColor(Theme.COLOR_GRID_NODES_FILL);
				gg.fillOval(x-4, y+3, nodeWidth, lineHeight-6);
				gg.setColor(Theme.COLOR_GRID_BACKGROUND);
				gg.setStroke(Theme.STROKE_SMALL);
				gg.drawOval(x-4, y+3, nodeWidth, lineHeight-6);
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
		for (String target : parent.getModel().getTargets())
			cnt += 1 + parent.getModel().getAttrs(target).length;
		return cnt;
	}

	private boolean isLineValid(int line) {
		if (0 <= line && line < getModelLineCnt()) {
			int cnt = 0;
			for (String target : parent.getModel().getTargets()) {
				if (line == cnt)
					return false;
				cnt += 1 + parent.getModel().getAttrs(target).length;
			}
			return true;
		}
		return false;
	}

	private void forAllNodes(NodeAction action) {
		int line = -1;
		TimelineModel model = parent.getModel();
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
					if (action.act(node, line))
						return;
				}
			}
		}
	}

	private interface NodeAction {
		public boolean act(Node node, int line);
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
		float currentHeight = parent.getHeight() - 25;
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
				parent.setSelectedLine(mouseOverLine);
			parent.repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (draggedNode != null && e.isShiftDown()) {
				int time = getTimeFromX(e.getX());
				draggedNode.durationMillis = Math.max(0, time - draggedNode.delayMillis);
				parent.repaint();
			} else if (draggedNode != null && !e.isShiftDown()) {
				int time = getTimeFromX(e.getX());
				draggedNode.delayMillis = Math.max(0, time - draggedNode.durationMillis);
				parent.repaint();
			} else if (isCursorDragged) {
				int time = getTimeFromX(e.getX());
				parent.setCurrentTime(time);
				parent.repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isCursorDragged = false;
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			// Time cursor test
			int x = getXFromTime(parent.getCurrentTime());
			isCursorDragged = (x - 2 <= e.getX() && e.getX() <= x + 2) && (e.getY()-vScrollOffset >= paddingTop - 9);

			// Node test
			draggedNode = null;
			forAllNodes(new NodeAction() {
				@Override public boolean act(Node node, int line) {
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
			parent.setCursor(isCursorDragged || draggedNode != null ? Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR) : Cursor.getDefaultCursor());
			parent.repaint();
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			timeScale *= e.getWheelRotation() > 0 ? 1.1f : 0.9f;
			timeScale = Math.min(timeScale, 3);
			timeScale = Math.max(timeScale, 0.25f);
			parent.repaint();
		}
	};
}
