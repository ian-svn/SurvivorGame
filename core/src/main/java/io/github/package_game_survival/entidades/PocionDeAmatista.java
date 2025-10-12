package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class PocionDeAmatista extends Pocion{

    public PocionDeAmatista(int x, int y) {
        super(Assets.get(PathManager.POCION_TEXTURE,Texture.class),"Pocion de amatista", 25);
        setX(x);
        setY(y);
    }
}

