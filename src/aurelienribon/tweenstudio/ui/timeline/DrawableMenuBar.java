package aurelienribon.tweenstudio.ui.timeline;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

final class DrawableMenuBar {
	private final TimelinePanel parent;
	private int margin = 3;
	private int imageWidth = 16;
	private int imageHeight = 16;
	private int height = imageHeight + margin * 2;

	private final BufferedImage addImage;
	private final BufferedImage firstImage;
	private final BufferedImage previousImage;
	private final BufferedImage playImage;
	private final BufferedImage nextImage;
	private final BufferedImage lastImage;

	public DrawableMenuBar(TimelinePanel parent) {
		this.parent = parent;
		addImage = loadImage("gfx/ic_timeline_add.png");
		firstImage = loadImage("gfx/ic_timeline_first.png");
		previousImage = loadImage("gfx/ic_timeline_previous.png");
		playImage = loadImage("gfx/ic_timeline_play.png");
		nextImage = loadImage("gfx/ic_timeline_next.png");
		lastImage = loadImage("gfx/ic_timeline_last.png");
	}

	public void draw(Graphics2D gg) {
		gg.setColor(Theme.COLOR_BACKGROUND);
		gg.fillRect(0, 0, parent.getWidth(), height);
		gg.setColor(Theme.COLOR_FOREGROUND);
		gg.setStroke(Theme.STROKE_SMALL);
		gg.drawLine(0, height, parent.getWidth(), height);
		
		int w = parent.getWidth();
		gg.drawImage(lastImage, null, w - margin*1 - imageWidth*1, margin);
		gg.drawImage(nextImage, null, w - margin*2 - imageWidth*2, margin);
		gg.drawImage(playImage, null, w - margin*3 - imageWidth*3, margin);
		gg.drawImage(previousImage, null, w - margin*4 - imageWidth*4, margin);
		gg.drawImage(firstImage, null, w - margin*5 - imageWidth*5, margin);
		gg.drawImage(addImage, null, w - margin*6 - imageWidth*6 - 70, margin);

		gg.setFont(Theme.FONT);
		String str = String.format("%02d,%03d", parent.getCurrentTime() / 1000, parent.getCurrentTime() % 1000);
		int strW = gg.getFontMetrics().stringWidth(str);
		gg.drawString(str, w - margin*6 - imageWidth*6 - 90 - strW, 16);
	}

	public int getHeight() {
		return height;
	}

	private BufferedImage loadImage(String classpath) {
		BufferedImage img = null;
		try {
			URL url = getClass().getResource(classpath);
			if (url != null)
				img = ImageIO.read(url);
		} catch (IOException ex) {
		}
		return img;
	}

	public MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			int x1 = parent.getWidth() - margin*6 - imageWidth*6 - 70;
			int x2 = x1 + imageWidth;
			if (e.getY() <= height && x1 <= e.getX() && e.getX() <= x2) {
				parent.requestNewNode();
			}
		}
	};
}
