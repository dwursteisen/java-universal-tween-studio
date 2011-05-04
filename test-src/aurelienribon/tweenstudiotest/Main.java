package aurelienribon.tweenstudiotest;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class Main {
    public static void main(String[] args) {
        new JoglApplication(new App(), "Tween Studio Test", 320, 480, false);
    }
}
