package aurelienribon.tweenstudiotest;

import aurelienribon.tweenstudio.Editor;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class LibGdxTweenStudioEditor extends Editor {
	@Override
	public void initialize() {
		registerProperty(TweenSprite.class, TweenSprite.POSITION_XY, "position");
		registerProperty(TweenSprite.class, TweenSprite.ROTATION, "rotation");
		registerProperty(TweenSprite.class, TweenSprite.OPACITY, "opacity");
		registerProperty(TweenSprite.class, TweenSprite.SCALE_XY, "scale");
	}
}
