package aurelienribon.tweenstudiotest;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenstudio.TweenStudio;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dwursteisen.tween.studio.Import;
import com.dwursteisen.tween.studio.model.Shape;

import java.util.List;
import java.util.stream.Collectors;

public class App implements ApplicationListener {
    private static final String ANIMATION_1 = "First animation";

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private TweenManager tweenManager;
    private List<Shape> shapes;

    public App() {
        // Tween Engine initialization
        Tween.registerAccessor(Sprite.class, new SpriteTweenAccessor());

        // ---------------------------------------------------------------------
        // 0. Tween Studio initialization: this single call ("enableEdition()")
        //    lets you toggle between edition and play modes!
//		int res = JOptionPane.showConfirmDialog(null, "Do you want to enable the edition of the animations ?", "Tween Studio", JOptionPane.YES_NO_OPTION);
//		if (res == 0) TweenStudio.enableEdition(1000, 480);
        // ---------------------------------------------------------------------
        TweenStudio.enableEdition(1000, 480);
    }

    @Override
    public void create() {
        // Common game creation stuff
        float ratio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        camera = new OrthographicCamera(10, 10 / ratio);
        shapeRenderer = new ShapeRenderer();
        tweenManager = new TweenManager();

        // ---------------------------------------------------------------------
        // 1. Preloading of the animations. You should always do that in your
        //    initialization code and not while your game is running, for
        //    performance reason.
        Import anImport = TweenStudio.preloadAnimation(Gdx.files.internal("data/shapes.timeline").file(), ANIMATION_1);
        shapes = anImport.getConfs()
                .stream()
                .map(Shape.Companion::fromConf)
                .collect(Collectors.toList());

        // (optional) The provided LibGdxTweenStudioEditorX editor needs to be
        //            configurated. Be careful that when edition is disabled,
        //            all editor instances are null.
        LibGdxTweenStudioEditorX editor = TweenStudio.getEditor(LibGdxTweenStudioEditorX.class);
        if (editor != null) editor.setup(camera);

        // 2. Registration of the editor we want to use for the next animations.
        TweenStudio.registerEditor(LibGdxTweenStudioEditorX.class);

        // 3. Registration of the objects that are part of the next animation.
        //    Their names are required to create timelines from serialized
        //    content.

        shapes.forEach(s -> TweenStudio.registerTarget(s, s.getName()));

//        TweenStudio.registerTarget(sprites[0], "Logo LibGDX");
        //      TweenStudio.registerTarget(sprites[1], "Logo Tween Engine");
        //    TweenStudio.registerTarget(sprites[2], "Logo Tween");
        //  TweenStudio.registerTarget(sprites[3], "Logo Studio");

        // 4. Registration of the callback that will be used for the next
        //    animation. Callbacks are the only way to retrieve the created
        //    animations (in order to cover all use-cases), so they are
        //    mandatory.
        TweenStudio.registerCallback(new TweenStudio.Callback() {
            @Override
            public void animationReady(String animationName, Timeline animation) {
                animation.start(tweenManager);
            }
        });

        // 5. Creation of the first animation timeline.
        TweenStudio.createAnimation(ANIMATION_1);
        // ---------------------------------------------------------------------

    }


    @Override
    public void render() {
        int delta = (int) (Gdx.graphics.getDeltaTime() * 1000);
        tweenManager.update(delta);

        // ---------------------------------------------------------------------
        // 5. You need to update the studio periodically (only required with
        //    some editors, like LibGdxTweenStudioEditorX). This won't do
        //    anything in play mode.
        TweenStudio.update(delta);
        // ---------------------------------------------------------------------

        GL20 gl = Gdx.gl20;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapes.forEach(s -> {
            shapeRenderer.setColor(s.getColor());
            shapeRenderer.rect(
                    s.getPosition().x, s.getPosition().y,
                    0, 0,
                    s.getSize().x, s.getSize().y,
                    s.getScale().x, s.getScale().y,
                    s.getRotation()
            );
        });

        shapeRenderer.end();

        // ---------------------------------------------------------------------
        // 6. Some editors (like LibGdxTweenStudioEditorX) render something in
        //    overlay of your game, so you need to add this line too. Again,
        //    this call doesn't cost anything in play mode.
        TweenStudio.render();
        // ---------------------------------------------------------------------
    }

    @Override
    public void resize(int w, int h) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
