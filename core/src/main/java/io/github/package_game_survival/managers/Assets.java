package io.github.package_game_survival.managers;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets {
    private static final AssetManager manager = new AssetManager();

    public static void load() {
        manager.load(PathManager.PLAYER, Texture.class);
        manager.load(PathManager.GRAB_OBJECT, Texture.class);
        manager.load(PathManager.MENU_MUSIC, Music.class);
        manager.load(PathManager.GAME_MUSIC, Music.class);
        manager.load(PathManager.PLACE_BLOCK, Sound.class);
    }

    public static boolean update() {
        return manager.update(); // true cuando terminó de cargar
    }

    public static float getProgress() {
        return manager.getProgress(); // para mostrar en pantalla de loading
    }

    public static <T> T get(String path, Class<T> type) {
        return manager.get(path, type);
    }

    public static void dispose() {
        manager.dispose();
    }
}

