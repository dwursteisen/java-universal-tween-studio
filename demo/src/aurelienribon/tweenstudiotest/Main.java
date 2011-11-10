package aurelienribon.tweenstudiotest;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Main {
    public static void main(String[] args) {
        new LwjglApplication(new App(), "Tween Studio Test", 320, 480, false);
    }
}
