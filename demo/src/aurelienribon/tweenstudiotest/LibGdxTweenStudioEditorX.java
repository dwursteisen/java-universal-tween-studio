package aurelienribon.tweenstudiotest;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.Tweenable;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class LibGdxTweenStudioEditorX extends LibGdxTweenStudioEditor {
	private final List<Sprite> sprites = new ArrayList<Sprite>();
	private InputProcessor oldInputProcessor;
	private OrthographicCamera camera;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}

	@Override
	public void initialize() {
		super.initialize();

		oldInputProcessor = Gdx.input.getInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);

		sprites.clear();

		for (Tweenable tweenable : getRegisteredTweenables())
			if (tweenable instanceof SpriteTweenable)
				sprites.add(((SpriteTweenable)tweenable).getSprite());
	}

	@Override
	public void dispose() {
		super.dispose();
		Gdx.input.setInputProcessor(oldInputProcessor);
	}

	// -------------------------------------------------------------------------
	// InputProcessor
	// -------------------------------------------------------------------------

	private final InputProcessor inputProcessor = new InputAdapter() {
		private final Vector2 lastPoint = new Vector2();
		private Sprite currentSprite;
		private int currentTweenType;
		private boolean currentSpriteLocked = false;
		private int currentSpriteIdx = -1;

		@Override
		public boolean touchMoved(int x, int y) {
			Vector2 p = screenToWorld(x, y);

			if (!currentSpriteLocked) {
				Sprite selected = null;
				for (Sprite sp : sprites)
					if (isOver(p, sp))
						selected = sp;
				if (selected != currentSprite) {
					currentSprite = selected;
					blinkSprite(currentSprite);
				}
			}

			lastPoint.set(p);
			return true;
		}

		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			Vector2 p = screenToWorld(x, y);

			if (!currentSpriteLocked) {
				for (Sprite sp : sprites)
					if (isOver(p, sp))
						currentSprite = sp;
			}

			lastPoint.set(p);
			return true;
		}

		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			if (currentSprite != null) {
				currentTweenType = getTweenType();
				fireStateChanged(getTweenable(currentSprite), currentTweenType);
			}
			return true;
		}

		@Override
		public boolean touchDragged(int x, int y, int pointer) {
			Vector2 p = screenToWorld(x, y);

			if (currentSprite != null) {
				currentTweenType = getTweenType();
				Vector2 delta = new Vector2(p).sub(lastPoint);
				apply(currentSprite, currentTweenType, delta);
			}

			lastPoint.set(p);
			return true;
		}

		@Override
		public boolean keyDown(int keycode) {
			switch (keycode) {
				case Keys.TAB:
					currentSpriteLocked = true;
					currentSpriteIdx = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)
						? currentSpriteIdx - 1 
						: currentSpriteIdx + 1;
					if (currentSpriteIdx >= sprites.size()) currentSpriteIdx = 0;
					if (currentSpriteIdx < 0) currentSpriteIdx = sprites.size()-1;
					currentSprite = sprites.get(currentSpriteIdx);
					blinkSprite(currentSprite);
					break;

				case Keys.ESCAPE:
					currentSpriteLocked = false;
					currentSprite = null;
					break;
			}
			return true;
		}
	};

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private Vector2 screenToWorld(int x, int y) {
		Vector3 v3 = new Vector3(x, y, 0);
		camera.unproject(v3);
		return new Vector2(v3.x, v3.y);
	}

	private boolean isOver(Vector2 p, Sprite sp) {
		float left = sp.getX() + sp.getOriginX() * (1 - sp.getScaleX());
		float right = sp.getX() + sp.getWidth() + sp.getOriginX() * (sp.getScaleX() - 1);
		float bottom = sp.getY() + sp.getOriginY() * (1 - sp.getScaleY());
		float top = sp.getY() + sp.getHeight() + sp.getOriginY() * (sp.getScaleY() - 1);
		return left <= p.x && p.x <= right && bottom <= p.y && p.y <= top;
	}

	private Tweenable getTweenable(Sprite sp) {
		for (Tweenable tweenable : getRegisteredTweenables())
			if (tweenable instanceof SpriteTweenable)
				if (((SpriteTweenable)tweenable).getSprite() == sp)
					return tweenable;
		assert false;
		return null;
	}

	private int getTweenType() {
		if (Gdx.input.isKeyPressed(Keys.R)) return SpriteTweenable.ROTATION;
		if (Gdx.input.isKeyPressed(Keys.S)) return SpriteTweenable.SCALE_XY;
		if (Gdx.input.isKeyPressed(Keys.O)) return SpriteTweenable.OPACITY;
		return SpriteTweenable.POSITION_XY;
	}

	private void apply(Sprite sp, int tweenType, Vector2 delta) {
		switch (tweenType) {
			case SpriteTweenable.POSITION_XY: sp.translate(delta.x, delta.y); break;
			case SpriteTweenable.ROTATION: sp.rotate(delta.x); break;
			case SpriteTweenable.SCALE_XY: sp.scale(delta.x/20); break;
			case SpriteTweenable.OPACITY:
				Color c = sp.getColor();
				float ca = c.a + delta.x/100;
				ca = Math.min(ca, 1);
				ca = Math.max(ca, 0);
				sp.setColor(c.r, c.g, c.b, ca);
				break;
		}
	}

	private void blinkSprite(Sprite sp) {
		if (sp == null) return;
		Tween.to(getTweenable(sp), SpriteTweenable.SCALE_XY, 150, Quad.INOUT)
			.target(sp.getScaleX()*1.1f, sp.getScaleY()*1.1f)
			.addToManager(getTweenManager());
		Tween.to(getTweenable(sp), SpriteTweenable.SCALE_XY, 150, Quad.INOUT)
			.target(sp.getScaleX(), sp.getScaleY())
			.delay(200)
			.addToManager(getTweenManager());
	}
}
