package aurelienribon.tweenstudiotest;

import aurelienribon.tweenstudio.TweenStudio;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class App implements ApplicationListener {
	private SpriteBatch spriteBatch;
	private Sprite[] sprites;
	private SpriteTweenable[] spriteTweenables;
	private TweenStudio tweenStudio;

	@Override
	public void create() {
		spriteBatch = new SpriteBatch();
		createSprites();

		// Create the studio...
		createStudio();
		// ...then spawn it when you want !
		tweenStudio.edit(LibGdxTweenStudioEditor.class, "data/anim.tweens");
	}

	private void createSprites() {
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/pack"));

		sprites = new Sprite[7];
		sprites[0] = atlas.createSprite("logoLibgdx");
		sprites[1] = atlas.createSprite("logoTweenEngine");
		sprites[2] = atlas.createSprite("logoTween");
		sprites[3] = atlas.createSprite("logoStudio");
		sprites[4] = atlas.createSprite("logoWave");
		sprites[5] = atlas.createSprite("logoWave");
		sprites[6] = atlas.createSprite("logoWave");

		for (Sprite sp : sprites)
			sp.setOrigin(sp.getWidth()/2, sp.getHeight()/2);
		
		spriteTweenables = new SpriteTweenable[sprites.length];
		for (int i=0; i<sprites.length; i++)
			spriteTweenables[i] = new SpriteTweenable(sprites[i]);
	}

	private void createStudio() {
		// Registration of the editor/player (only needed once per application)
		TweenStudio.registerEditor(new LibGdxTweenStudioEditor());
		TweenStudio.registerPlayer(new LibGdxTweenStudioPlayer());

		// Instantiation of the studio
		tweenStudio = new TweenStudio();

		// Registration of the Tweenables we want to animate
		tweenStudio.registerTweenable(spriteTweenables[0], "Logo LibGDX");
		tweenStudio.registerTweenable(spriteTweenables[1], "Logo Tween Engine");
		tweenStudio.registerTweenable(spriteTweenables[2], "Logo Tween");
		tweenStudio.registerTweenable(spriteTweenables[3], "Logo Studio");
		tweenStudio.registerTweenable(spriteTweenables[4], "Wave 1");
		tweenStudio.registerTweenable(spriteTweenables[5], "Wave 2");
		tweenStudio.registerTweenable(spriteTweenables[6], "Wave 3");
	}

	@Override
	public void render() {
		tweenStudio.update();

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		for (int i=0; i<sprites.length; i++)
			sprites[i].draw(spriteBatch);
		spriteBatch.end();
	}

	@Override public void resize(int w, int h) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}
}
