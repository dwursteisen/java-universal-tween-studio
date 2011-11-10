package aurelienribon.tweenstudio.ui.timeline;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class ResourcesHelper {
	public static BufferedImage getGfx(String name) {
		URL url = ResourcesHelper.class.getResource("gfx/" + name);
		if (url == null) throw new RuntimeException(name + " not found in timeline gfx");

		BufferedImage img = null;
		try {img = ImageIO.read(url);}
		catch (IOException ex) {}
		return img;
	}
}
