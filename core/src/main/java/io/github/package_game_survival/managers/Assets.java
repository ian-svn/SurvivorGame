package io.github.package_game_survival.managers;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {
    private static final AssetManager manager = new AssetManager();

    public static void load() {
        manager.load(PathManager.PLAYER_TEXTURE, Texture.class);
        manager.load(PathManager.BLOCK_TEXTURE, Texture.class);
        manager.load(PathManager.GAME_BACKGROUND_TEXTURE, Texture.class);
        manager.load(PathManager.MENU_BACKGROUND_TEXTURE, Texture.class);
        manager.load(PathManager.POCION_TEXTURE, Texture.class);

        manager.load(PathManager.MENU_MUSIC, Music.class);
        manager.load(PathManager.GAME_MUSIC, Music.class);

        manager.load(PathManager.GRAB_OBJECT, Sound.class);
        manager.load(PathManager.PLACE_BLOCK, Sound.class);

        manager.load(PathManager.BACKGROUND, Skin.class);
        manager.load(PathManager.LABEL, Skin.class);
        manager.load(PathManager.TEXT_BUTTON, Skin.class);
        manager.load(PathManager.TOOLTIP, Skin.class);
        manager.load(PathManager.CHECK_BOX, Skin.class);

        manager.load(PathManager.PROGRESS_BAR_SKIN, Skin.class);
        manager.load(PathManager.PROGRESS_BAR_SKIN_VIDA, Skin.class);

        manager.load(PathManager.PROGRESS_BAR_ATLAS, TextureAtlas.class);
        manager.load(PathManager.PLAYER_ATLAS, TextureAtlas.class);

    }

    public static boolean update() {
        return manager.update(); // cuando termina de cargar
    }

    public static float getProgress() {
        return manager.getProgress(); // cuanto va
    }

    public static <T> T get(String path, Class<T> type) {
        return manager.get(path, type);
    }

    public static void dispose() {
        manager.dispose();
    }

    public static AssetManager getManager() {
        return manager;
    }
}

