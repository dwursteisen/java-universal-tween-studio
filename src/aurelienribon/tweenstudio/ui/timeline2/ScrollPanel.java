package aurelienribon.tweenstudio.ui.timeline2;

import aurelienribon.tweenstudio.ui.timeline.components.Scrollable;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JPanel;

public class ScrollPanel extends JPanel {
	private final Scrollable scrollable;
	private final int thickness = 12;
	private final int padding = 2;
	private final float horizontalRatio = 0.50f;

	private float horizontalPosition = 0.5f;

    public ScrollPanel(Scrollable scrollable) {
		this.scrollable = scrollable;
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
		gg.drawLine(0, getHeight() - thickness, getWidth(), getHeight() - thickness);
	}

	private void drawVerticalContainer(Graphics2D gg) {
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.drawLine(getWidth() - thickness, 0, getWidth() - thickness, getHeight());
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

	private int getHorizontalContainerWidth() {
		return getWidth() - thickness;
	}

	private Rectangle getHorizontalSelector() {
		int w = (int) ((getHorizontalContainerWidth() - padding*2) * horizontalRatio);
		int h = thickness - padding*2 - 1;
		int x = (int) (padding + horizontalPosition * (getHorizontalContainerWidth() - padding*2 - w));
		int y = getHeight() - thickness + padding + 1;
		return new Rectangle(x, y, w, h);
	}

	private int getVerticalContainerHeight() {
		return getHeight() - thickness - 1;
	}

	private Rectangle getVerticalSelector() {
		int w = thickness - 5;
		int h = (int) ((getVerticalContainerHeight() - 5) * getVerticalRatio());
		h = Math.max(h, 20);
		h = Math.min(h, getVerticalContainerHeight() - 5);
		int x = getWidth() - thickness - 1 + 3;
		int y = (int) (3 + (getVerticalPosition() * (getVerticalContainerHeight() - 5 - h)));

		return new Rectangle(x, y, w, h);
	}

	public float getVerticalRatio() {
		float currentHeight = getHeight() - 22;
		float ratio = currentHeight / scrollable.getPreferredHeight();
		return ratio;
	}

	public float getVerticalPosition() {
		float currentHeight = getHeight() - 25;
		return scrollable.getVerticalOffset() / (scrollable.getPreferredHeight() - currentHeight);
	}
}
