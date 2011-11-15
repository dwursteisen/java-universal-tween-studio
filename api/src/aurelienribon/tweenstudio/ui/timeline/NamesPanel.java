package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
	
	private TimelineModel model;
	private Theme theme;
	private Callback callback;
	private Element selectedElement;
	private Element mouseOverElement;
	private int vOffset;

	public NamesPanel(Theme theme) {
		this.theme = theme;
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
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

	public void setSelectedElementSilently(Element elem) {
		this.selectedElement = elem;
		repaint();
	}

	@Override
	public int getViewLength() {
		return getHeight();
	}

	@Override
	public int getLength() {
		int height = model != null
			? paddingTop + UiHelper.getLinesCount(model) * lineHeight
			: 0;
		return Math.max(0, height) + lineHeight;
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
		public void selectedElementChanged(Element selectedElem);
		public void verticalOffsetChanged(int vOffset);
	}

	// -------------------------------------------------------------------------
	// Painting
	// -------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gg = (Graphics2D) g;
		gg.setColor(theme.COLOR_NAMESPANEL_BACKGROUND);
		gg.fillRect(0, 0, getWidth(), getHeight());
		if (model == null) return;

		gg.translate(0, paddingTop - vOffset);
		drawSections(gg);
		drawNames(gg);
	}

	private void drawSections(final Graphics2D gg) {
		int line = 0;
		for (Element elem : model.getElements()) {
			if (!elem.isSelectable()) {
				gg.setColor(theme.COLOR_NAMESPANEL_SECTION_UNUSABLE);
			} else if (elem == selectedElement) {
				gg.setColor(theme.COLOR_NAMESPANEL_SECTION_SELECTED);
			} else if (elem == mouseOverElement) {
				gg.setColor(theme.COLOR_NAMESPANEL_SECTION_MOUSEOVER);
			} else {
				gg.setColor(theme.COLOR_NAMESPANEL_SECTION);
			}

			gg.fillRect(0, line*lineHeight, getWidth(), lineHeight);
			line += 1;
		}
	}

	private void drawNames(final Graphics2D gg) {
		gg.setFont(theme.FONT);
		gg.setColor(theme.COLOR_FOREGROUND);

		int line = 0;
		for (Element elem : model.getElements()) {
			int level = elem.getLevel();
			int x = paddingLeft + level*paddingIncremental;
			int y = line*lineHeight-5;
			Image img = elem.getChildren().isEmpty() ? imgIdxNone : imgIdxClosed;

			gg.drawImage(img, x, y + 7, null);
			gg.drawString(elem.getName(), x + 20, y + 20);

			line += 1;
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private int getLineFromY(int y) {
		if (y < paddingTop) return -1;
		return (y - paddingTop) / lineHeight;
	}

	// -------------------------------------------------------------------------
	// Input
	// -------------------------------------------------------------------------

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (selectedElement != mouseOverElement) {
				selectedElement = mouseOverElement;
				callback.selectedElementChanged(selectedElement);
			}
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseOverElement = null;
			final int evLine = getLineFromY(e.getY());

			int line = 0;
			for (Element elem : model.getElements()) {
				if (evLine == line && elem.isSelectable()) {
					mouseOverElement = elem;
					break;
				}
				line += 1;
			}
			
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			mouseOverElement = null;
			repaint();
		}
	};
}
