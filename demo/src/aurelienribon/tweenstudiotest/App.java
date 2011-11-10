package aurelienribon.tweenstudiotest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.Map;

public class App implements ApplicationListener {
	private static final float SCREEN_WIDTH_METERS = 10.0f;

	private SpriteBatch sb;
	private OrthographicCamera camera;
	private Texture logoTexture;
	private Sprite logoSprite1;
	private Sprite logoSprite2;
	private Sprite logoSprite3;
	private TweenSprite logoSpriteTween1;
	private TweenSprite logoSpriteTween2;
	private TweenSprite logoSpriteTween3;

	@Override
	public void create() {
		sb = new SpriteBatch();
		float ratio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		camera = new OrthographicCamera(SCREEN_WIDTH_METERS, SCREEN_WIDTH_METERS / ratio);
		camera.update();
		
		logoTexture = new Texture(Gdx.files.internal("test-data/logo.png"));
		logoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		logoSprite1 = new Sprite(logoTexture);
		logoSprite1.setSize(2, 2);
		logoSprite1.setOrigin(logoSprite1.getWidth()/2, logoSprite1.getHeight()/2);
		logoSprite1.setPosition(0 - logoSprite1.getOriginX(), 0 - logoSprite1.getOriginY());

		logoSprite2 = new Sprite(logoTexture);
		logoSprite2.setSize(2, 2);
		logoSprite2.setOrigin(logoSprite2.getWidth()/2, logoSprite2.getHeight()/2);
		logoSprite2.setPosition(-2 - logoSprite2.getOriginX(), 0 - logoSprite2.getOriginY());

		logoSprite3 = new Sprite(logoTexture);
		logoSprite3.setSize(2, 2);
		logoSprite3.setOrigin(logoSprite3.getWidth()/2, logoSprite3.getHeight()/2);
		logoSprite3.setPosition(+2 - logoSprite3.getOriginX(), 0 - logoSprite3.getOriginY());

		logoSpriteTween1 = new TweenSprite(logoSprite1);
		logoSpriteTween2 = new TweenSprite(logoSprite2);
		logoSpriteTween3 = new TweenSprite(logoSprite3);

		TweenStudio studio = createStudio();
		studio.edit();
	}

	@Override
	public void resume() {
	}

	@Override
	public void render() {
		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		sb.begin();
		camera.apply(gl);
		logoSprite1.draw(sb);
		logoSprite2.draw(sb);
		logoSprite3.draw(sb);
		sb.end();
	}

	@Override
	public void resize(int i, int i1) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void dispose() {
	}

	// -------------------------------------------------------------------------

	private TweenStudio createStudio() {
		return new TweenStudio(
			TweenSequence.set(
				Tween.to(logoSpriteTween1, TweenStudioObject.POSITION_XY, Cubic.INOUT, 500, -1.0625f, 3.5625f).delay(0),
				Tween.to(logoSpriteTween1, TweenStudioObject.SCALE_XY, Cubic.INOUT, 500, 0.3f, 0.3f).delay(-400),
				Tween.to(logoSpriteTween1, TweenStudioObject.POSITION_XY, Cubic.INOUT, 800, -0.96875f, -5.96875f).delay(0),
				Tween.to(logoSpriteTween1, TweenStudioObject.SCALE_XY, Cubic.INOUT, 800, 1.0f, 1.0f).delay(-600),
				Tween.to(logoSpriteTween1, TweenStudioObject.POSITION_XY, Cubic.INOUT, 500, -1.0f, -1.0f).delay(0),
				Tween.to(logoSpriteTween1, TweenStudioObject.SCALE_XY, Cubic.INOUT, 500, 3.53125f, 3.40625f).delay(-300),
				Tween.to(logoSpriteTween1, TweenStudioObject.OPACITY, Cubic.INOUT, 500, 0.203125f).delay(-500)
			),

			new TweenStudioEditor() {
				@Override protected void getFieldNames(Map<TweenStudioObject, String> map) {
					map.put(logoSpriteTween1, "logoSpriteTween1");
					map.put(logoSpriteTween2, "logoSpriteTween2");
					map.put(logoSpriteTween3, "logoSpriteTween3");
				}

				@Override
				protected InputProcessor getCurrentInputProcessor() {
					return null;
				}

				@Override protected Vector2 getPositionFromInput(Vector2 inputPos) {
					inputPos.y = Gdx.graphics.getHeight() - inputPos.y;

					float metersPerPixel = SCREEN_WIDTH_METERS / Gdx.graphics.getWidth();
					inputPos.mul(metersPerPixel * camera.zoom);

					float screenHeightMeters = SCREEN_WIDTH_METERS * (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
					inputPos.x += camera.position.x - SCREEN_WIDTH_METERS / 2 * camera.zoom;
					inputPos.y += camera.position.y - screenHeightMeters / 2 * camera.zoom;

					return inputPos;
				}
			}
		);
	}
}
