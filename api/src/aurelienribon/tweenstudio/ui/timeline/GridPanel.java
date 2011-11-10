package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.ElementAction;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JPanel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class GridPanel extends JPanel implements Scrollable {
	private final int oneSecondWidth = 70;
	private final int paddingTop = 30;
	private final int paddingLeft = 15;
	private final int lineHeight = 20;
	private final int nodeWidth = 7;
	private final int timeScaleLimit = 2;

	private TimelineModel model;
	private Theme theme;
	private Callback callback;
	private float timeScale = 1;
	private int currentTime = 0;
	private Element selectedElement = null;
	private Element mouseOverElement = null;
	private Node selectedNode = null;
	private Node mouseOverNode = null;
	private int vOffset;
	private int hOffset;
	private int maxTime = 0;

	public GridPanel(Theme theme) {
		this.theme = theme;
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addKeyListener(keyAdapter);
		setFocusable(true);
	}

	public void setModel(TimelineModel model) {
		this.model = model;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
		repaint();
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void setSelectedElement(Element selectedElement) {
		if (this.selectedElement != selectedElement) {
			this.selectedElement = selectedElement;
			selectedNode = null;
			repaint();
		}
	}

	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}

	public void requestAddNode() {
		if (selectedElement != null) {
			selectedElement.addNode(currentTime, 0);
			repaint();
		}
	}

	public void requestDelNode() {
		if (selectedNode != null) {
			model.forAllElements(new ElementAction() {
				@Override public boolean apply(Element elem) {
					if (elem.getNodes().contains(selectedNode)) {
						elem.getNodes().remove(selectedNode);
						repaint();
						return true;
					}
					return false;
				}
			});
		}
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
		return getXFromTime(maxTime) + hOffset + 20;
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
		public void currentTimeChanged(int newTime);
		public void selectedElementChanged(Element selectedElement);
		public void lengthChanged();
	}

	// -------------------------------------------------------------------------
	// Painting
	// -------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gg = (Graphics2D)g;
		gg.setColor(theme.COLOR_GRIDPANEL_BACKGROUND);
		gg.fillRect(0, 0, getWidth(), getHeight());
		if (model == null) return;

		paintSections(gg);
		paintNodesTracks(gg);
		paintTimeline(gg);
		paintTimeCursor(gg);
		paintNodes(gg);

		updateMaxTime();
	}

	private void paintSections(final Graphics2D gg) {
		final int elemCnt = UiHelper.getLinesCount(model);

		model.forAllElements(new ElementAction() {
			private int line = 0;
			@Override public boolean apply(Element elem) {
				if (elem.isSelectable()) {
					gg.setColor(theme.COLOR_GRIDPANEL_SECTION);
				} else {
					gg.setColor(theme.COLOR_GRIDPANEL_SECTION_UNUSABLE);
				}

				gg.fillRect(0, paddingTop-vOffset+lineHeight*line, getWidth(), line != elemCnt-1 ? lineHeight-1 : lineHeight);
				line += 1;
				return false;
			}
		});
	}
	
	private void paintTimeline(Graphics2D gg) {
		gg.setFont(theme.FONT);
		FontMetrics fm = gg.getFontMetrics();

		int minSeconds = Math.max((int) (timeScale * hOffset / oneSecondWidth), 0);
		int maxSeconds = (int) (timeScale * getWidth() / oneSecondWidth) + minSeconds + 1;

		int textOffset = -12;
		int smallTickHeight = 5;
		int bigTickHeight = 7;

		for (int i=minSeconds; i<=maxSeconds; i++) {
			int x = getXFromTime(i*1000);

			String str = String.valueOf(i);
			gg.setColor(theme.COLOR_FOREGROUND);
			gg.drawString(str, x - fm.stringWidth(str)/2, paddingTop + textOffset);

			if (timeScale < timeScaleLimit) {
				gg.setColor(theme.COLOR_GRIDPANEL_TIMELINE);
				gg.drawRect(x-1, paddingTop - bigTickHeight, 2, getHeight());
				for (int ii=1; ii<10; ii++) {
					int xx = getXFromTime(ii*100 + i*1000);
					gg.drawLine(xx, paddingTop - (ii == 5 ? bigTickHeight : smallTickHeight), xx, paddingTop-1);
				}

			} else {
				gg.setColor(theme.COLOR_GRIDPANEL_TIMELINE);
				gg.drawLine(x, paddingTop - bigTickHeight, x, getHeight());
				int xx = getXFromTime(5*100 + i*1000);
				gg.drawLine(xx, paddingTop - bigTickHeight, xx, paddingTop-1);
			}
		}
	}

	private void paintTimeCursor(Graphics2D gg) {
		int x = getXFromTime(currentTime);
		int y = paddingTop;
		gg.setColor(theme.COLOR_GRIDPANEL_CURSOR);
		gg.fillPolygon(
			new int[] {x - 8, x, x + 8},
			new int[] {y - 9, y, y - 9},
			3);
		gg.fillRect(x - 1, y - 5, 3, getHeight());
	}

	private void paintNodesTracks(final Graphics2D gg) {
		model.forAllElements(new ElementAction() {
			private int line = 0;
			@Override public boolean apply(Element elem) {
				int y = getYFromLine(line);
				List<Node> nodes = elem.getNodes();

				for (Node node : nodes) {
					int x1 = getXFromTime(node.getStart());
					int x2 = getXFromTime(node.getEnd());

					if (node == selectedNode) {
						gg.setColor(theme.COLOR_GRIDPANEL_NODE_TRACK_SELECTED);
					} else if (node == mouseOverNode) {
						gg.setColor(theme.COLOR_GRIDPANEL_NODE_TRACK_MOUSEOVER);
					} else {
						gg.setColor(theme.COLOR_GRIDPANEL_NODE_TRACK);
					}

					gg.fillRect(x1, y+1, x2-x1, lineHeight-3);
				}

				for (int i=0, n=nodes.size(); i<n; i++) {
					Node n1 = nodes.get(i);
					for (int j=i+1; j<n; j++) {
						Node n2 = nodes.get(j);
						int[] ovl = getOverlap(n1, n2);
						if (ovl != null) {
							int x3 = getXFromTime(ovl[0]);
							int x4 = getXFromTime(ovl[1]);
							gg.setColor(Color.RED);
							gg.fillRect(x3, y+1, x4-x3, lineHeight-3);
						}
					}
				}

				line += 1;
				return false;
			}
		});
	}

	private void paintNodes(final Graphics2D gg) {
		model.forAllElements(new ElementAction() {
			private int line = 0;
			@Override public boolean apply(Element elem) {
				int y = getYFromLine(line);
				List<Node> nodes = elem.getNodes();

				for (Node node : nodes) {
					int x = getXFromTime(node.getEnd());

					if (node == selectedNode) {
						gg.setColor(theme.COLOR_GRIDPANEL_NODE_FILL_SELECTED);
					} else if (node == mouseOverNode) {
						gg.setColor(theme.COLOR_GRIDPANEL_NODE_FILL_MOUSEOVER);
					} else {
						gg.setColor(theme.COLOR_GRIDPANEL_NODE_FILL);
					}

					gg.fillOval(x-4, y+2, nodeWidth, lineHeight-6);

					if (node == selectedNode) {
						gg.setColor(theme.COLOR_GRIDPANEL_NODE_STROKE_SELECTED);
					} else if (node == mouseOverNode) {
						gg.setColor(theme.COLOR_GRIDPANEL_NODE_STROKE_MOUSEOVER);
					} else {
						gg.setColor(theme.COLOR_GRIDPANEL_NODE_STROKE);
					}

					gg.drawOval(x-4, y+2, nodeWidth, lineHeight-6);
				}

				for (int i=0, n=nodes.size(); i<n; i++) {
					Node n1 = nodes.get(i);
					int x = getXFromTime(n1.getEnd());
					for (int j=0; j<n; j++) {
						Node n2 = nodes.get(j);
						if (n1 == n2) continue;
						if ((n1.getEnd() > n2.getStart() && n1.getEnd() <= n2.getEnd()) || (n1.getEnd() == n2.getEnd())) {
							gg.setColor(Color.RED);
							gg.fillOval(x-4, y+2, nodeWidth, lineHeight-6);
							gg.setColor(theme.COLOR_GRIDPANEL_NODE_STROKE);
							gg.drawOval(x-4, y+2, nodeWidth, lineHeight-6);
						}
					}
				}

				line += 1;
				return false;
			}
		});
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private int getTimeFromX(int x) {
		int time = (int) ((x - paddingLeft + hOffset) * 1000f / oneSecondWidth * timeScale);
		time = time > 0 ? time : 0;
		if (timeScale < timeScaleLimit) {
			time = Math.round(time / 100f) * 100;
		} else {
			time = Math.round(time / 500f) * 500;
		}
		return Math.max(time, 0);
	}

	private int getXFromTime(int millis) {
		return (int) (millis / 1000f * oneSecondWidth / timeScale + paddingLeft - hOffset);
	}

	private int getLineFromY(int y) {
		if (y < paddingTop) return -1;
		return (y - paddingTop + vOffset) / lineHeight;
	}

	private int getYFromLine(int line) {
		return paddingTop - vOffset + lineHeight * line;
	}

	private int[] getOverlap(Node n1, Node n2) {
		int n1s = n1.getStart();
		int n1e = n1.getEnd();
		int n2s = n2.getStart();
		int n2e = n2.getEnd();
		if (n1s <= n2s && n1e >= n2e) return new int[] {n2s, n2e};
		if (n1s >= n2s && n1e <= n2e) return new int[] {n1s, n1e};
		if (n1s >= n2s && n1s < n2e && n1e >= n2e) return new int[] {n1s, n2e};
		if (n1s <= n2s && n1e <= n2e && n1e > n2s) return new int[] {n2s, n1e};
		return null;
	}

	private void updateMaxTime() {
		int oldTime = maxTime;
		maxTime = Math.max(model.getDuration(), currentTime);
		if (maxTime != oldTime) callback.lengthChanged();
	}

	// -------------------------------------------------------------------------
	// Input
	// -------------------------------------------------------------------------

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		private boolean isCursorDragged = false;
		private int lastTime;

		@Override
		public void mousePressed(MouseEvent e) {
			lastTime = getTimeFromX(e.getX());

			if (getLineFromY(e.getY()) < 0) {
				currentTime = lastTime;
				repaint();
			}

			if (selectedNode != mouseOverNode) {
				selectedNode = mouseOverNode;
				repaint();
			}

			if (selectedElement != mouseOverElement) {
				selectedElement = mouseOverElement;
				callback.selectedElementChanged(selectedElement);
				repaint();
			}

			requestFocusInWindow();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isCursorDragged = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (model == null) return;

			int newTime = getTimeFromX(e.getX());
			int deltaTime = newTime - lastTime;
			lastTime = newTime;

			if (getLineFromY(e.getY()) < 0) {
				currentTime = newTime;
				repaint();
			}

			 if (isCursorDragged) {
				currentTime = newTime;
				repaint();
				callback.currentTimeChanged(currentTime);

			} else if (selectedNode != null && e.isShiftDown()) {
				selectedNode.setDuration(Math.max(0, selectedNode.getDuration() + deltaTime));
				repaint();

			} else if (selectedNode != null && !e.isShiftDown()) {
				selectedNode.setStart(Math.max(0, selectedNode.getStart() + deltaTime));
				repaint();
			}
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			if (model == null) return;

			// Time cursor test
			int x = getXFromTime(currentTime);
			isCursorDragged = (x - 5 <= e.getX() && e.getX() <= x + 5) && (e.getY() >= paddingTop - 9);

			// Nodes test
			Node oldMouseOverNode = mouseOverNode;
			Element oldMouseOverElement = mouseOverElement;
			mouseOverNode = null;
			mouseOverElement = null;
			final int evTime = getTimeFromX(e.getX());
			final int evLine = getLineFromY(e.getY());

			model.forAllElements(new ElementAction() {
				private int line = 0;
				@Override public boolean apply(Element elem) {
					if (evLine == line && elem.isSelectable()) {
						mouseOverElement = elem;
						for (Node node : elem.getNodes()) {
							if (evTime >= node.getStart() && evTime <= node.getEnd()) {
								mouseOverNode = node;
								return true;
							}
						}
					}

					line += 1;
					return false;
				}
			});

			if (oldMouseOverNode != mouseOverNode || oldMouseOverElement != mouseOverElement)
				repaint();

			// Cursor change
			setCursor(isCursorDragged || mouseOverNode != null ? Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR) : Cursor.getDefaultCursor());
		}

		@Override
		public void mouseExited(MouseEvent e) {
			mouseOverNode = null;
			repaint();
		}
	};

	private final KeyAdapter keyAdapter = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_ENTER: requestAddNode(); break;
				case KeyEvent.VK_DELETE: requestDelNode(); break;
			}
		}
	};
}
