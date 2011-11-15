package aurelienribon.tweenstudiotest;

import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenstudio.TweenStudio;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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

	private TweenStudio tweenStudio;

	@Override
	public void create() {
		sb = new SpriteBatch();
		float ratio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		camera = new OrthographicCamera(SCREEN_WIDTH_METERS, SCREEN_WIDTH_METERS / ratio);
		camera.update();
		
		logoTexture = new Texture(Gdx.files.internal("data/logo.png"));
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

		tweenStudio = new TweenStudio();
		tweenStudio.registerTweenable(logoSpriteTween1, "Logo 1");
		tweenStudio.registerTweenable(logoSpriteTween2, "Logo 2");
		tweenStudio.registerTweenable(logoSpriteTween3, "Logo 3");
		tweenStudio.edit(new LibGdxTweenStudioEditor());
	}

	@Override
	public void resume() {
	}

	@Override
	public void render() {
		tweenStudio.update();

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
}
