package aurelienribon.tweenstudio.ui.timeline;

import aurelienribon.tweenstudio.ui.timeline.TimelineModel.Element;
import aurelienribon.tweenstudio.ui.timeline.gfx.ResourcesHelper;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class NamesPanel extends JPanel {
	private final BufferedImage imgIdxClosed = ResourcesHelper.getGfx("img_idxClosed.png");
	private final BufferedImage imgIdxOpened = ResourcesHelper.getGfx("img_idxOpened.png");
	private final BufferedImage imgIdxNone = ResourcesHelper.getGfx("img_idxNone.png");
	private final int paddingTop = 30;
	private final int paddingLeft = 10;
	private final int paddingIncremental = 30;
	private final int lineHeight = 20;
	
	private TimelineModel model;

	public void setModel(TimelineModel model) {
		this.model = model;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gg = (Graphics2D) g;
		gg.setColor(Theme.COLOR_NAMESPANEL_BACKGROUND);
		gg.fillRect(0, 0, getWidth(), getHeight());
		if (model == null) return;

		gg.translate(0, paddingTop);
		drawSections(gg);
		drawNames(gg);
	}

	private void drawSections(final Graphics2D gg) {
		gg.setColor(Theme.COLOR_NAMESPANEL_SECTION);
		for (int i=0, n=UiHelper.getLinesCount(model); i<n; i++)
			gg.fillRect(0, i*lineHeight, getWidth(), lineHeight);
	}

	private void drawNames(final Graphics2D gg) {
		gg.setFont(Theme.FONT);
		gg.setColor(Theme.COLOR_FOREGROUND);

		model.forAllElements(new TimelineModel.ElementAction() {
			private int line = 0;
			@Override public boolean apply(Element elem) {
				int level = model.getPath(elem).length;
				int x = paddingLeft + (level-1)*paddingIncremental;
				int y = line*lineHeight-5;
				Image img = elem.getChildren().isEmpty() ? imgIdxNone : imgIdxClosed;

				gg.drawImage(img, x, y + 7, null);
				gg.drawString(elem.getName(), x + 20, y + 20);

				line += 1;
				return false;
			}
		});
	}
}
