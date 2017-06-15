import aurelienribon.tweenstudiotest.App;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Main {
    public static void main(String[] args) {

        ShaderProgram.pedantic = false;

        new LwjglApplication(new App(), "My Super Awesome Game !", 320, 480);
    }
}
