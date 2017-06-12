package aurelienribon.tweenstudio.ui.timeline;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
class ResourcesHelper {
	public static BufferedImage getGfx(String name) {
		InputStream url = ResourcesHelper.class.getResourceAsStream("/gfx/" + name);
		if (url == null) throw new RuntimeException(name + " not found in timeline gfx");


		BufferedImage img = null;
		try {img = ImageIO.read(url);}
		catch (IOException ex) {}
		return img;
	}
}
