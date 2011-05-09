package aurelienribon.tweenstudio.ui.timeline2;

import aurelienribon.tweenstudio.ui.timeline.components.Scrollable;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ScrollPanel extends JPanel {
	private final Scrollable scrollable;
	private final int thickness = 14;
	private final int padding = 2;
	private final int containerLineThickness = 1;
	private final float horizontalRatio = 0.50f;

	private float horizontalPosition = 0.5f;

    public ScrollPanel(Scrollable scrollable) {
		this.scrollable = scrollable;
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);

		setLayout(new BorderLayout());
		add((JComponent)scrollable, BorderLayout.CENTER);
		add(Box.createHorizontalStrut(thickness), BorderLayout.EAST);
		add(Box.createVerticalStrut(thickness), BorderLayout.SOUTH);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gg = (Graphics2D)g;

		drawVerticalContainer(gg);
		drawHorizontalContainer(gg);
		drawCorner(gg);
		drawHorizontalSelector(gg);
		drawVerticalSelector(gg);
	}

	private void drawHorizontalContainer(Graphics2D gg) {
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.fillRect(0, getHeight() - thickness, getWidth(), containerLineThickness);
	}

	private void drawVerticalContainer(Graphics2D gg) {
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.fillRect(getWidth() - thickness, 0, containerLineThickness, getHeight());
	}

	private void drawCorner(Graphics2D gg) {
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.fillRect(getWidth() - thickness, getHeight() - thickness, thickness, thickness);
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

	private int getMaxHorizontalSelectorLength() {
		return getWidth() - thickness - padding*2;
	}

	private int getMaxVerticalSelectorLength() {
		return getHeight() - thickness - padding*2;
	}

	private Rectangle getHorizontalSelector() {
		int w = (int) (getMaxHorizontalSelectorLength() * horizontalRatio);
		w = Math.min(w, getMaxHorizontalSelectorLength());
		int h = thickness - containerLineThickness - padding*2;
		int x = (int) (padding + horizontalPosition * (getMaxHorizontalSelectorLength() - w));
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
			horizontalPosition = 0.5f;
			getParent().repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (hSelectorDragPoint >= 0) {
				int w = getHorizontalSelector().width;
				horizontalPosition = (float)(e.getX() - hSelectorDragPoint - padding) / (getMaxHorizontalSelectorLength() - w);
				horizontalPosition = Math.min(horizontalPosition, 1);
				horizontalPosition = Math.max(horizontalPosition, 0);
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
	};
}
