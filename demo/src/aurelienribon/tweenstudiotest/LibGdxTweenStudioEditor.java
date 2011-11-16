package aurelienribon.tweenstudiotest;

import aurelienribon.tweenstudio.Editor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class LibGdxTweenStudioEditor extends Editor {
	@Override
	public void initialize() {
		registerProperty(SpriteTweenable.class, SpriteTweenable.POSITION_XY, "position");
		registerProperty(SpriteTweenable.class, SpriteTweenable.ROTATION, "rotation");
		registerProperty(SpriteTweenable.class, SpriteTweenable.OPACITY, "opacity");
		registerProperty(SpriteTweenable.class, SpriteTweenable.SCALE_XY, "scale");
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
}
