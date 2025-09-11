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

    public static final String PLAYER = "sprites/jugador.png";

    public static final String sd = "skins/background.json";
    public static final String getSd = "skins/label.json";
    public static final String FD = "skins/OpcionesButton.json";
    public static final String CV = "skins/SalirButton.json";
    public static final String VB = "skins/SalirButton.json";



}
