package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Node;
import aurelienribon.tweenstudio.ui.timeline.TimelinePanel.PushBehavior;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class NamesPanel extends JPanel implements Scrollable {
	private final BufferedImage imgIdxClosed = ResourcesHelper.getGfx("img_idxClosed.png");
	private final BufferedImage imgIdxOpened = ResourcesHelper.getGfx("img_idxOpened.png");
	private final BufferedImage imgIdxNone = ResourcesHelper.getGfx("img_idxNone.png");
	private final int paddingTop = 30;
	private final int paddingLeft = 10;
	private final int paddingIncremental = 30;
	private final int lineHeight = 20;
	private final int lineGap = 1;

	private final TimelinePanel parent;
	private Callback callback;
	private int vOffset;

	public NamesPanel(TimelinePanel parent) {
		this.parent = parent;
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);

		parent.addListener(new TimelinePanel.Listener() {
			@Override public void playRequested() {}
			@Override public void pauseRequested() {}
			@Override public void selectedElementsChanged(List<Element> newElems, List<Element> oldElems) {repaint();}
			@Override public void selectedNodesChanged(List<Node> newNodes, List<Node> oldNodes) {}
			@Override public void mouseOverElementChanged(Element newElem, Element oldElem) {repaint();}
			@Override public void currentTimeChanged(int newTime, int oldTime) {}
		});
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void modelChanged(TimelineModel model) {
		callback.lengthChanged();
		repaint();
	}

	public void themeChanged(Theme theme) {
		repaint();
	}

	@Override
	public int getViewLength() {
		return getHeight();
	}

	@Override
	public int getLength() {
		TimelineModel model = parent.getModel();
		int height = model != null
			? paddingTop + getLineCount(model.getRoot()) * (lineHeight + lineGap)
			: 0;
		return Math.max(0, height) + lineHeight + lineGap;
	}

	@Override
	public int getOffset() {
		return vOffset;
	}

	@Override
	public void setOffset(int offset) {
		this.vOffset = offset;
		callback.verticalOffsetChanged(vOffset);
		repaint();
	}

	// -------------------------------------------------------------------------
	// Callback
	// -------------------------------------------------------------------------

	public interface Callback {
		public void verticalOffsetChanged(int vOffset);
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

		Graphics2D gg = (Graphics2D) g;
		gg.setColor(theme.COLOR_NAMESPANEL_BACKGROUND);
		gg.fillRect(0, 0, getWidth(), getHeight());
		if (model == null) return;

		gg.translate(0, paddingTop - vOffset);
		drawSections(gg);
		drawNames(gg);
	}

	private void drawSections(final Graphics2D gg) {
		Theme theme = parent.getTheme();
		TimelineModel model = parent.getModel();
		List<Element> selectedElements = parent.getSelectedElements();
		Element mouseOverElement = parent.getMouseOverElement();

		int line = 0;
		for (Element elem : model.getElements()) {
			if (elem.isDescendantOf(selectedElements)) {
				gg.setColor(theme.COLOR_NAMESPANEL_SECTION_SELECTED);
				gg.fillRect(0, line*(lineHeight+lineGap), getWidth(), lineHeight+lineGap);
			} else if (elem.isDescendantOf(mouseOverElement)) {
				gg.setColor(theme.COLOR_NAMESPANEL_SECTION_MOUSEOVER);
				gg.fillRect(0, line*(lineHeight+lineGap), getWidth(), lineHeight+lineGap);
			}

			line += 1;
		}
	}

	private void drawNames(final Graphics2D gg) {
		Theme theme = parent.getTheme();
		TimelineModel model = parent.getModel();
		
		gg.setFont(theme.FONT);
		gg.setColor(theme.COLOR_FOREGROUND);

		int line = 0;
		for (Element elem : model.getElements()) {
			int x = paddingLeft + (elem.getLevel()-1)*paddingIncremental;
			int y = line*(lineHeight+lineGap)-5;
			Image img = elem.getChildren().isEmpty() ? imgIdxNone : imgIdxClosed;

			gg.drawImage(img, x, y + 7, null);
			gg.drawString(elem.getName(), x + 20, y + 20);

			line += 1;
		}
	}

	// -------------------------------------------------------------------------
	// Input
	// -------------------------------------------------------------------------

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			PushBehavior behavior = e.isControlDown() ? PushBehavior.ADD_OR_REMOVE : PushBehavior.SET;
			parent.pushSelectedElement(parent.getMouseOverElement(), behavior);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			TimelineModel model = parent.getModel();
			int evLine = getLineFromY(e.getY());
			
			Element mouseOverElement = null;

			int line = 0;
			for (Element elem : model.getElements()) {
				if (evLine == line) {
					mouseOverElement = elem;
					while (mouseOverElement.getParent() != model.getRoot())
						mouseOverElement = mouseOverElement.getParent();
					break;
				}
				line += 1;
			}

			parent.setMouseOverElement(mouseOverElement);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			parent.setMouseOverElement(null);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			callback.scrollRequired(e.getWheelRotation() * 40);
		}
	};

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private int getLineFromY(int y) {
		y += vOffset;
		if (y < paddingTop) return -1;
		return (y - paddingTop) / (lineHeight + lineGap);
	}

	private int getLineCount(Element elem) {
		int cnt = 1;
		for (Element child : elem.getChildren())
			cnt += getLineCount(child);
		return cnt;
	}
}
