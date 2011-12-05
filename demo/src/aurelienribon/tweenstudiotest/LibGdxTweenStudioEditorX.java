package aurelienribon.tweenstudiotest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class LibGdxTweenStudioEditorX extends LibGdxTweenStudioEditor {
	private final List<Sprite> sprites = new ArrayList<Sprite>();
	private InputProcessor oldInputProcessor;
	private OrthographicCamera camera;

	private final ImmediateModeRenderer imr = new ImmediateModeRenderer();
	private final SpriteBatch spriteBatch = new SpriteBatch();
	private final BitmapFont font = new BitmapFont();
	private Sprite mouseOverSprite;
	private Sprite selectedSprite;
	private boolean selectionLocked;

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

		for (Object target : getRegisteredTargets())
			if (target instanceof Sprite)
				sprites.add((Sprite) target);
	}

	@Override
	public void dispose() {
		super.dispose();
		
		Gdx.input.setInputProcessor(oldInputProcessor);
	}

	@Override
	public void render() {
		super.render();

		GL10 gl = Gdx.gl10;
		gl.glEnable(GL10.GL_BLEND);

		camera.apply(gl);
		if (selectedSprite != null) drawBoundingBox(gl, selectedSprite, new Color(0.2f, 0.2f, 0.8f, 1.0f));
		if (mouseOverSprite != null) drawBoundingBox(gl, mouseOverSprite, new Color(0.2f, 0.2f, 0.8f, 0.3f));

		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.begin();
		font.setColor(Color.BLUE);
		if (selectedSprite != null) {
			String name = getRegisteredName(selectedSprite);
			font.draw(spriteBatch, "Selected: " + name, 5, Gdx.graphics.getHeight());
		}
		if (mouseOverSprite != null) {
			String name = getRegisteredName(mouseOverSprite);
			font.draw(spriteBatch, "Mouseover :" + name, 5, Gdx.graphics.getHeight() - 20);
		}
		spriteBatch.end();
	}

	// -------------------------------------------------------------------------
	// InputProcessor
	// -------------------------------------------------------------------------

	private final InputProcessor inputProcessor = new InputAdapter() {
		private final Set<Integer> tweenTypes = new HashSet<Integer>();
		private int lastX;
		private int lastY;
		private int selectedSpriteIdx = -1;

		@Override
		public boolean touchMoved(int x, int y) {
			if (!selectionLocked) {
				mouseOverSprite = null;
				Vector2 p = screenToWorld(new Vector2(x, y));

				for (Sprite sp : sprites)
					if (isOver(p, sp))
						mouseOverSprite = sp;
			}

			lastX = x;
			lastY = y;
			return true;
		}

		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			if (!selectionLocked) selectedSprite = mouseOverSprite;
			lastX = x;
			lastY = y;
			return true;
		}

		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			if (selectedSprite != null)
				fireStateChanged(selectedSprite, tweenTypes);
			tweenTypes.clear();
			return true;
		}

		@Override
		public boolean touchDragged(int x, int y, int pointer) {
			if (selectedSprite != null) {
				int tweenType = getTweenType();
				tweenTypes.add(tweenType);
				Vector2 delta = new Vector2(x, y).sub(lastX, lastY);
				apply(selectedSprite, tweenType, delta);
			}

			lastX = x;
			lastY = y;
			return true;
		}

		@Override
		public boolean keyDown(int keycode) {
			switch (keycode) {
				case Keys.TAB:
					selectionLocked = true;
					selectedSpriteIdx = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)
						? selectedSpriteIdx - 1
						: selectedSpriteIdx + 1;
					if (selectedSpriteIdx >= sprites.size()) selectedSpriteIdx = 0;
					if (selectedSpriteIdx < 0) selectedSpriteIdx = sprites.size()-1;
					selectedSprite = sprites.get(selectedSpriteIdx);
					break;

				case Keys.ESCAPE:
					selectionLocked = false;
					break;
			}
			return true;
		}
	};

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private int getTweenType() {
		if (Gdx.input.isKeyPressed(Keys.R)) return SpriteTweenAccessor.ROTATION;
		if (Gdx.input.isKeyPressed(Keys.S)) return SpriteTweenAccessor.SCALE_XY;
		if (Gdx.input.isKeyPressed(Keys.O)) return SpriteTweenAccessor.OPACITY;
		return SpriteTweenAccessor.POSITION_XY;
	}

	private void apply(Sprite sp, int tweenType, Vector2 screenDelta) {
		Vector2 worldDelta = screenToWorld(screenDelta).sub(screenToWorld(new Vector2(0, 0)));
		switch (tweenType) {
			case SpriteTweenAccessor.POSITION_XY: sp.translate(worldDelta.x, worldDelta.y); break;
			case SpriteTweenAccessor.ROTATION: sp.rotate(screenDelta.x); break;
			case SpriteTweenAccessor.SCALE_XY: sp.scale(screenDelta.x/20); break;
			case SpriteTweenAccessor.OPACITY:
				Color c = sp.getColor();
				float ca = c.a + screenDelta.x/100;
				ca = Math.min(ca, 1);
				ca = Math.max(ca, 0);
				sp.setColor(c.r, c.g, c.b, ca);
				break;
		}
	}

	private Vector2 screenToWorld(Vector2 v) {
		Vector3 v3 = new Vector3(v.x, v.y, 0);
		camera.unproject(v3);
		return new Vector2(v3.x, v3.y);
	}

	private Rectangle getBoundingBox(Sprite sp) {
		float left = sp.getX() + sp.getOriginX() * (1 - sp.getScaleX());
		float right = sp.getX() + sp.getWidth() + sp.getOriginX() * (sp.getScaleX() - 1);
		float bottom = sp.getY() + sp.getOriginY() * (1 - sp.getScaleY());
		float top = sp.getY() + sp.getHeight() + sp.getOriginY() * (sp.getScaleY() - 1);
		return new Rectangle(left, bottom, right-left, top-bottom);
	}

	private boolean isOver(Vector2 p, Sprite sp) {
		Vector2 orig = new Vector2(sp.getX() + sp.getOriginX(), sp.getY() + sp.getOriginY());
		Vector2 p2 = rotate(p, orig, -sp.getRotation());
		Rectangle bb = getBoundingBox(sp);
		return bb.x <= p2.x && p2.x <= bb.x + bb.width && bb.y <= p2.y && p2.y <= bb.y + bb.height;
	}

	private Vector2 rotate(Vector2 v, Vector2 o, float angleDeg) {
		float cos = MathUtils.cosDeg(angleDeg);
		float sin = MathUtils.sinDeg(angleDeg);
		float x = v.x;
		float y = v.y;
		float newX = cos*(x-o.x) - sin*(y-o.y) + o.x;
		float newY = sin*(x-o.x) + cos*(y-o.y) + o.y;
		return new Vector2(newX, newY);
	}
	
	private void drawBoundingBox(GL10 gl, Sprite sp, Color color) {
		gl.glPushMatrix();
		gl.glTranslatef(+sp.getX()+sp.getOriginX(), +sp.getY()+sp.getOriginY(), 0);
		gl.glRotatef(sp.getRotation(), 0, 0, 1);
		gl.glTranslatef(-sp.getX()-sp.getOriginX(), -sp.getY()-sp.getOriginY(), 0);

		Rectangle bb = getBoundingBox(sp);
		drawRect(bb.x, bb.y, bb.width, bb.height, color);

		if (selectionLocked) {
			Vector2 size = screenToWorld(new Vector2(10, -10)).sub(screenToWorld(new Vector2(0, 0)));
			drawRect(bb.x+bb.width-size.x/2, bb.y+bb.height-size.y/2, size.x, size.y, color);
		}

		gl.glPopMatrix();
	}

	private void drawRect(float x, float y, float w, float h, Color c) {
		Gdx.gl10.glLineWidth(2);
		imr.begin(GL10.GL_LINE_LOOP);
		imr.color(c.r, c.g, c.b, c.a); imr.vertex(x, y, 0);
		imr.color(c.r, c.g, c.b, c.a); imr.vertex(x, y+h, 0);
		imr.color(c.r, c.g, c.b, c.a); imr.vertex(x+w, y+h, 0);
		imr.color(c.r, c.g, c.b, c.a); imr.vertex(x+w, y, 0);
		imr.end();
	}
}
