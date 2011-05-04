package aurelienribon.tweenstudio.ui.timeline;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

class DrawableScrollBars {
	private final TimelinePanel parent;
	private final Scrollable scrollable;
	private final int thickness = 12;
	private final int margin = 5;
	private final int topOffset = 23;
	private final float horizontalRatio = 0.50f;

	private float horizontalPosition = 0.5f;

	public DrawableScrollBars(TimelinePanel parent, Scrollable scrollable) {
		this.parent = parent;
		this.scrollable = scrollable;

		parent.registerUpdateRunnable(updateRunnable);
	}

	private Runnable updateRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						scrollable.requestHorizontalScroll(horizontalPosition * 2 - 1);
						parent.repaint();
					}
				});
			} catch (InterruptedException ex) {
			} catch (InvocationTargetException ex) {
			}
		}
	};

	// -------------------------------------------------------------------------
	// Drawing
	// -------------------------------------------------------------------------

	public void draw(Graphics2D gg) {
		drawVerticalContainer(gg);
		drawHorizontalContainer(gg);
		drawCorner(gg);
		drawHorizontalSelector(gg);
		drawVerticalSelector(gg);
	}

	private void drawHorizontalContainer(Graphics2D gg) {
		int x = margin;
		int y = parent.getHeight() - thickness - margin - 1;
		int w = getHorizontalContainerWidth();
		int h = thickness;

		gg.setColor(Theme.COLOR_BACKGROUND);
		gg.fillRect(x, y, w, h);
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.drawRect(x, y, w, h);
	}

	private void drawVerticalContainer(Graphics2D gg) {
		int x = parent.getWidth() - thickness - margin - 1;
		int y = topOffset + margin;
		int w = thickness;
		int h = getVerticalContainerHeight();

		gg.setColor(Theme.COLOR_BACKGROUND);
		gg.fillRect(x, y, w, h);
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.drawRect(x, y, w, h);
	}

	private void drawCorner(Graphics2D gg) {
		int x = parent.getWidth() - thickness - margin - 1;
		int y = parent.getHeight() - thickness - margin - 1;
		int w = thickness;
		int h = thickness;

		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.fillRect(x, y, w, h);
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.drawRect(x, y, w, h);
	}

	private void drawHorizontalSelector(Graphics2D gg) {
		Rectangle selector = getHorizontalSelector();
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.fillRect(selector.x, selector.y, selector.width, selector.height);
	}

	private void drawVerticalSelector(Graphics2D gg) {
		Rectangle selector = getVerticalSelector();
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.fillRect(selector.x, selector.y, selector.width, selector.height);
	}

	// -------------------------------------------------------------------------
	// Utils
	// -------------------------------------------------------------------------

	private int getHorizontalContainerWidth() {
		return parent.getWidth() - margin*2 - thickness - 1;
	}

	private Rectangle getHorizontalSelector() {
		int w = (int) ((getHorizontalContainerWidth() - 5) * horizontalRatio);
		int h = thickness - 5;
		int x = (int) (margin + 3 + (horizontalPosition * (getHorizontalContainerWidth() - 5 - w)));
		int y = parent.getHeight() - thickness - margin + 3 - 1;

		return new Rectangle(x, y, w, h);
	}

	private int getVerticalContainerHeight() {
		return parent.getHeight() - margin*2 - thickness - topOffset - 1;
	}

	private Rectangle getVerticalSelector() {
		int w = thickness - 5;
		int h = (int) ((getVerticalContainerHeight() - 5) * getVerticalRatio());
		h = Math.max(h, 20);
		h = Math.min(h, getVerticalContainerHeight() - 5);
		int x = parent.getWidth() - thickness - margin - 1 + 3;
		int y = (int) (topOffset + margin + 3 + (getVerticalPosition() * (getVerticalContainerHeight() - 5 - h)));

		return new Rectangle(x, y, w, h);
	}

	public float getVerticalRatio() {
		float currentHeight = parent.getHeight() - 22;
		float ratio = currentHeight / scrollable.getPreferredHeight();
		return ratio;
	}

	public float getVerticalPosition() {
		float currentHeight = parent.getHeight() - 25;
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
			}

			vSelectorDragPoint = -1;
			if (getVerticalSelector().contains(e.getPoint())) {
				vSelectorDragPoint = e.getY() - getVerticalSelector().y;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			horizontalPosition = 0.5f;
			parent.repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (hSelectorDragPoint > 0) {
				int w = getHorizontalSelector().width;
				horizontalPosition = (float)(e.getX() - hSelectorDragPoint - margin - 3) / (getHorizontalContainerWidth() - 5 - w);
				horizontalPosition = Math.min(horizontalPosition, 1);
				horizontalPosition = Math.max(horizontalPosition, 0);
			}

			if (vSelectorDragPoint > 0) {
				int h = getVerticalSelector().height;
				float newPos = (float)(e.getY() - vSelectorDragPoint - margin - 3 - topOffset) / (getVerticalContainerHeight() - 5 - h);
				newPos = Math.min(newPos, 1);
				newPos = Math.max(newPos, 0);

				scrollable.requestVerticalScroll(newPos);
				parent.repaint();
			}
		}
	};
}
