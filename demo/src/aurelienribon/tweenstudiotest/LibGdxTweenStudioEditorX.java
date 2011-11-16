package aurelienribon.tweenstudiotest;

import aurelienribon.tweenengine.Tweenable;
import aurelienribon.tweenstudio.Editor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class LibGdxTweenStudioEditorX extends Editor {
	private final Map<Object, Tweenable> tweenablesMap = new HashMap<Object, Tweenable>();
	private final List<Sprite> sprites = new ArrayList<Sprite>();
	private InputProcessor oldInputProcessor;
	private final Vector2 screen2world = new Vector2(1, 1);

	public void setScreenToWorld(float x, float y) {
		screen2world.set(x, y);
	}

	@Override
	public void initialize() {
		registerProperty(SpriteTweenable.class, SpriteTweenable.POSITION_XY, "position");
		registerProperty(SpriteTweenable.class, SpriteTweenable.ROTATION, "rotation");
		registerProperty(SpriteTweenable.class, SpriteTweenable.OPACITY, "opacity");
		registerProperty(SpriteTweenable.class, SpriteTweenable.SCALE_XY, "scale");

		oldInputProcessor = Gdx.input.getInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);

		tweenablesMap.clear();
		sprites.clear();

		for (Tweenable tweenable : getRegisteredTweenables()) {
			if (tweenable instanceof SpriteTweenable) {
				Sprite sp = ((SpriteTweenable)tweenable).getSprite();
				tweenablesMap.put(sp, tweenable);
				sprites.add(sp);
			}
		}
	}

	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(oldInputProcessor);
	}

	@Override
	public void setFileContent(String filepath, String content) {
		try {
			FileHandle file = Gdx.files.absolute(filepath);
			OutputStream os = file.write(false);
			Writer writer = new BufferedWriter(new OutputStreamWriter(os));
			writer.write(content);
			writer.flush();
			os.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String getFileContent(String filepath) {
		FileHandle file = Gdx.files.internal(filepath);
		if (file.exists()) {
			return file.readString();
		} else {
			return "";
		}
	}

	// -------------------------------------------------------------------------
	// InputProcessor
	// -------------------------------------------------------------------------

	private final InputProcessor inputProcessor = new InputAdapter() {
		private final Vector2 lastPoint = new Vector2();
		private Sprite draggedSprite = null;

		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			Vector2 p = new Vector2(x * screen2world.x, y * screen2world.y);

			draggedSprite = null;
			for (Sprite sp : sprites)
				if (isOver(p, sp))
					draggedSprite = sp;

			lastPoint.set(p);
			return true;
		}

		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			draggedSprite = null;
			return true;
		}

		@Override
		public boolean touchDragged(int x, int y, int pointer) {
			Vector2 p = new Vector2(x * screen2world.x, y * screen2world.y);

			if (draggedSprite != null) {
				Vector2 delta = new Vector2(p).sub(lastPoint);
				fireTargetsChanged(
					tweenablesMap.get(draggedSprite),
					SpriteTweenable.POSITION_XY,
					draggedSprite.getX() + delta.x, 
					draggedSprite.getY() + delta.y);
			}

			lastPoint.set(p);
			return true;
		}
	};

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private boolean isOver(Vector2 p, Sprite sp) {
		return sp.getX() <= p.x && p.x <= sp.getX() + sp.getWidth()
			&& sp.getY() <= p.y && p.y <= sp.getY() + sp.getHeight();
	}
}
