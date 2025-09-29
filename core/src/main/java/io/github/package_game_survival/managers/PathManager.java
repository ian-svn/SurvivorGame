package io.github.package_game_survival.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public final class PathManager {
    private PathManager(){}

    private static final FileHandle s = Gdx.files.internal("");

    public static final String PLACE_BLOCK = "sounds/place_block.mp3";
    public static final String GAME_MUSIC = "sounds/MyCastleTown.mp3";
    public static final String MENU_MUSIC = "sounds/MenuTheme.mp3";
    public static final String GRAB_OBJECT = "sounds/grab_object.mp3";

    public static final String PLAYER_TEXTURE = "sprites/jugador.png";
    public static final String BLOCK_TEXTURE = "sprites/block.png";
    public static final String GAME_BACKGROUND_TEXTURE = "sprites/fondoJuego.png";

    public static final String BACKGROUND = "skins/background.json";
    public static final String LABEL = "skins/label.json";
    public static final String TEXT_BUTTON = "skins/TextButton.json";
    public static final String TOOLTIP = "skins/tooltip.json";
    public static final String CHECK_BOX = "skins/checkBox.json";
    public static final String PROGRESS_BAR_SKIN = "skins/progressBar.json";

    public static final String PROGRESS_BAR_ATLAS = "skins/progressBar.atlas";
    public static final String PLAYER_ATLAS = "sprites/jugador.atlas";



}
