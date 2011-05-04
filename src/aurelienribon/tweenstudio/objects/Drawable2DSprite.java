package aurelienribon.tweenstudio.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Drawable2DSprite extends Drawable2D {
	private Sprite sprite;

	public Drawable2DSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public int getTweenValues(int tweenType, float[] returnValues) {
		switch (tweenType) {
			case ORIGIN_XY:
				returnValues[0] = sprite.getOriginX();
				returnValues[1] = sprite.getOriginY();
				return 2;

			case POSITION_XY:
				returnValues[0] = sprite.getX();
				returnValues[1] = sprite.getY();
				return 2;

			case SCALE_XY:
				returnValues[0] = sprite.getScaleX();
				returnValues[1] = sprite.getScaleY();
				return 2;

			case ROTATION: 
				returnValues[0] = sprite.getRotation();
				return 1;
				
			case OPACITY:
				returnValues[0] = sprite.getColor().a;
				return 1;

			default:
				assert false;
				return -1;
		}
	}

	@Override
	public void tweenUpdated(int tweenType, float[] newValues) {
		switch (tweenType) {
			case OPACITY:
				Color c = sprite.getColor();
				c.set(c.r, c.g, c.b, newValues[0]);
				sprite.setColor(c);
				break;

			case ORIGIN_XY:
				sprite.setOrigin(newValues[0], newValues[1]);
				break;

			case POSITION_XY:
				sprite.setPosition(newValues[0], newValues[1]);
				break;

			case ROTATION:
				sprite.setRotation(newValues[0]);
				break;

			case SCALE_XY:
				sprite.setScale(newValues[0], newValues[1]);
				break;

			default: assert false;
		}
	}
}
