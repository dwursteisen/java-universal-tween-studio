import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import aurelienribon.tweenstudiotest.App;

public class Main {
    public static void main(String[] args) {
        new LwjglApplication(new App(), "My Super Awesome Game !", 320, 480);
    }
}
