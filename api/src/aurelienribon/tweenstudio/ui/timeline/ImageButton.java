package aurelienribon.tweenstudio.ui.timeline;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class ImageButton extends JButton {
	private final BufferedImage mouseOverImage;
	private final BufferedImage mousePressedImage;
	private final List<BufferedImage> images = new ArrayList<BufferedImage>();
	private final Dimension size;

	private boolean isMouseOver = false;
	private boolean isMousePressed = false;
	private int imageIdx = 0;

    public ImageButton(String gfxName) {
		this.mouseOverImage = ResourcesHelper.getGfx("ic_btnMouseOver.png");
		this.mousePressedImage = ResourcesHelper.getGfx("ic_btnMousePressed.png");
		this.images.add(ResourcesHelper.getGfx(gfxName));
		this.size = new Dimension(20, 20);

		addMouseListener(mouseAdapter);
		setFocusable(false);
		setBorder(null);
	}

	public ImageButton addImage(String gfxName) {
		images.add(ResourcesHelper.getGfx(gfxName));
		return this;
	}

	public void setImageIdx(int imageIdx) {
		this.imageIdx = imageIdx;
		repaint();
	}

	public int getImageIdx() {
		return imageIdx;
	}

	@Override
	public Dimension getPreferredSize() {
		return size;
	}

	@Override
	public Dimension getMaximumSize() {
		return size;
	}

	@Override
	public Dimension getMinimumSize() {
		return size;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (isMousePressed) {
			g.drawImage(mousePressedImage, 0, 0, null);
		} else if (isMouseOver) {
			g.drawImage(mouseOverImage, 0, 0, null);
		}

		g.drawImage(images.get(imageIdx), 2, 2, null);
	}

	private MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			isMouseOver = true;
			getParent().repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			isMouseOver = false;
			getParent().repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			isMousePressed = true;
			getParent().repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isMousePressed = false;
			getParent().repaint();
		}
	};
}
