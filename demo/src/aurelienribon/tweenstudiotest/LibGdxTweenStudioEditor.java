package aurelienribon.tweenstudiotest;

import aurelienribon.tweenstudio.Editor;
import aurelienribon.tweenstudio.Property.Field;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class LibGdxTweenStudioEditor extends Editor {
	@Override
	protected void initializeOverride() {
		registerProperty(Sprite.class, SpriteTweenAccessor.POSITION_XY, "position", new Field("x", 1), new Field("y", 1));
		registerProperty(Sprite.class, SpriteTweenAccessor.ROTATION, "rotation", new Field("rotation", 1));
		registerProperty(Sprite.class, SpriteTweenAccessor.OPACITY, "opacity", new Field("opacity", 0, 1, 0.1f));
		registerProperty(Sprite.class, SpriteTweenAccessor.SCALE_XY, "scale", new Field("scaleX", 1), new Field("scaleY", 1));
	}

	@Override
	protected void disposeOverride() {
	}

	@Override
	public void render() {
	}

	@Override
	public void selectedObjectChanged(Object obj) {
	}

	@Override
	public void mouseOverObjectChanged(Object obj) {
	}
}
