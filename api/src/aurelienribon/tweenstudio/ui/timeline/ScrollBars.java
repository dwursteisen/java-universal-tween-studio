package aurelienribon.tweenstudio.ui.timeline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScrollBars {
	/*private final int thickness = 14;
	private final int padding = 2;
	private final float hRatio = 0.50f;

	private float hPos = 0.5f;
	private int w;
	private int h;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void paint(Graphics2D gg, int w, int h) {
		this.w = w;
		this.h = h;
		paintVerticalContainer(gg);
		paintHorizontalContainer(gg);
		paintHorizontalSelector(gg);
		paintVerticalSelector(gg);
	}

	// -------------------------------------------------------------------------
	// Drawings
	// -------------------------------------------------------------------------

	private void paintHorizontalContainer(Graphics2D gg) {
		gg.setColor(Color.RED);
		gg.fillRect(0, h-thickness-1, w, thickness);
	}

	private void paintVerticalContainer(Graphics2D gg) {
		gg.setColor(Color.RED);
		gg.fillRect(w-thickness-1, 0, thickness, h);
	}

	private void paintHorizontalSelector(Graphics2D gg) {
		Rectangle selector = getHorizontalSelector();
		gg.setColor(Color.BLUE);
		gg.fillRect(selector.x, selector.y, selector.width, selector.height);
	}

	private void paintVerticalSelector(Graphics2D gg) {
		Rectangle selector = getVerticalSelector();
		gg.setColor(Color.BLUE);
		gg.fillRect(selector.x, selector.y, selector.width, selector.height);
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private int getMaxHorizontalSelectorLength() {
		return getWidth() - thickness - padding*2;
	}

	private int getMaxVerticalSelectorLength() {
		return getHeight() - thickness - padding*2;
	}

	private Rectangle getHorizontalSelector() {
		int w = (int) (getMaxHorizontalSelectorLength() * hRatio);
		w = Math.min(w, getMaxHorizontalSelectorLength());
		int h = thickness - containerLineThickness - padding*2;
		int x = (int) (padding + hPos * (getMaxHorizontalSelectorLength() - w));
		int y = getHeight() - thickness + containerLineThickness + padding;
		return new Rectangle(x, y, w, h);
	}

	private Rectangle getVerticalSelector() {
		int w = thickness - containerLineThickness - padding*2;
		int h = (int) (getMaxVerticalSelectorLength() * getVerticalRatio());
		h = Math.min(h, getMaxVerticalSelectorLength());
		int x = getWidth() - thickness + containerLineThickness + padding;
		int y = (int) (padding + (getVerticalPosition() * (getMaxVerticalSelectorLength() - h)));
		return new Rectangle(x, y, w, h);
	}

	public float getVerticalRatio() {
		if (scrollable == null)
			return 1;
		float currentHeight = getHeight() - thickness;
		float ratio = currentHeight / scrollable.getPreferredHeight();
		ratio = Math.min(ratio, 1);
		return ratio;
	}

	public float getVerticalPosition() {
		if (scrollable == null)
			return 0;
		float currentHeight = getHeight() - thickness;
		return scrollable.getVerticalOffset() / (scrollable.getPreferredHeight() - currentHeight);
	}

	// -------------------------------------------------------------------------
	// Input
	// -------------------------------------------------------------------------

	public MouseAdapter mouseAdapter = new MouseAdapter() {
		private float hSelectorDragPoint = -1;
		private float vSelectorDragPoint = -1;

		@Override
		public void mousePressed(MouseEvent e) {
			hSelectorDragPoint = -1;
			if (getHorizontalSelector().contains(e.getPoint())) {
				hSelectorDragPoint = e.getX() - getHorizontalSelector().x;
				System.out.println(hSelectorDragPoint);
			}

			vSelectorDragPoint = -1;
			if (getVerticalSelector().contains(e.getPoint())) {
				vSelectorDragPoint = e.getY() - getVerticalSelector().y;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			hPos = 0.5f;
			getParent().repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (hSelectorDragPoint >= 0) {
				int w = getHorizontalSelector().width;
				hPos = (float)(e.getX() - hSelectorDragPoint - padding) / (getMaxHorizontalSelectorLength() - w);
				hPos = Math.min(hPos, 1);
				hPos = Math.max(hPos, 0);
				getParent().repaint();
			}

			if (vSelectorDragPoint >= 0) {
				int h = getVerticalSelector().height;
				float newPos = (float)(e.getY() - vSelectorDragPoint - padding) / (getMaxVerticalSelectorLength() - h);
				newPos = Math.min(newPos, 1);
				newPos = Math.max(newPos, 0);
				scrollable.requestVerticalScroll(newPos);
				getParent().repaint();
			}
		}
	};*/
}
