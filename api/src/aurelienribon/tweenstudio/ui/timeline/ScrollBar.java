package aurelienribon.tweenstudio.ui.timeline;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class ScrollBar extends JPanel {
	public enum Orientation {VERTICAL, HORIZONTAL}

	private final int padding = 2;
	private Scrollable scrollable = null;
	private Orientation orientation = Orientation.VERTICAL;
	private Theme theme;

	public ScrollBar(Theme theme) {
		this.theme = theme;
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}

	public void setScrollable(Scrollable scrollable) {
		this.scrollable = scrollable;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
		repaint();
	}

	public void scroll(int amount) {
		int sLen = scrollable.getLength();
		int vLen = scrollable.getViewLength();
		if (vLen >= sLen || getAvailableLength() == 0) return;

		int offset = scrollable.getOffset() + amount;
		if (offset > sLen - vLen) {
			offset = sLen - vLen;
		} else if (offset < 0) {
			offset = 0;
		}
		
		scrollable.setOffset(offset);
		repaint();
	}

	// -------------------------------------------------------------------------
	// Painting
	// -------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics g) {
		if (scrollable != null) {
			int sLen = scrollable.getLength();
			int sOff = scrollable.getOffset();
			int vLen = scrollable.getViewLength();
			if (vLen >= sLen) scrollable.setOffset(0);
			else if (sOff > sLen - vLen) scrollable.setOffset(sLen - vLen);
		}

		Graphics2D gg = (Graphics2D) g;
		paintContainer(gg);
		if (scrollable != null) paintSelector(gg);
	}

	private void paintContainer(Graphics2D gg) {
		gg.setColor(theme.COLOR_SCROLLBAR_CONTAINER_FILL);
		gg.fillRect(0, 0, getWidth(), getHeight());
	}

	private void paintSelector(Graphics2D gg) {
		int selectorLength = getSelectorLength();
		int selectorPos = getSelectorPos();
		gg.setColor(theme.COLOR_SCROLLBAR_SELECTOR);
		if (orientation == Orientation.VERTICAL) {
			gg.fillRect(padding, selectorPos - selectorLength/2, getWidth()-padding*2, selectorLength);
		} else {
			gg.fillRect(selectorPos - selectorLength/2, padding, selectorLength, getHeight()-padding*2);
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private int getAvailableLength() {
		return orientation == Orientation.VERTICAL
			? getHeight() - getSelectorLength() - padding*2
			: getWidth() - getSelectorLength() - padding*2;
	}

	private int getSelectorLength() {
		float ratio = Math.min((float)scrollable.getViewLength() / scrollable.getLength(), 1f);
		int length = orientation == Orientation.VERTICAL
			? (int) ((getHeight() - padding*2) * ratio)
			: (int) ((getWidth() - padding*2) * ratio);
		return Math.max(length, 10);
	}

	private int getSelectorPos() {
		float ratio = Math.min((float)scrollable.getOffset() / (scrollable.getLength() - scrollable.getViewLength()), 1f);
		return (int) (getSelectorLength()/2 + padding + getAvailableLength() * ratio);
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		private int lastPos;
		private int lastOffset;

		@Override
		public void mousePressed(MouseEvent e) {
			lastPos = orientation == Orientation.VERTICAL ? e.getY() : e.getX();
			lastOffset = scrollable.getOffset();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (scrollable == null) return;

			int sLen = scrollable.getLength();
			int vLen = scrollable.getViewLength();
			if (vLen >= sLen || getAvailableLength() == 0) return;

			int delta = orientation == Orientation.VERTICAL ? e.getY() - lastPos : e.getX() - lastPos;

			int offset = delta * (sLen - vLen) / getAvailableLength() + lastOffset;
			if (offset > sLen - vLen) {
				offset = sLen - vLen;
			} else if (offset < 0) {
				offset = 0;
			}

			scrollable.setOffset(offset);
			repaint();
		}
	};
}
