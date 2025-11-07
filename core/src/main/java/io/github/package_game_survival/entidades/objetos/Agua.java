package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Agua extends ObjetoConsumible{
    public Agua(float x, float y) {
        super("Agua", x, y, Assets.get(PathManager.AGUA_TEXTURE,Texture.class)
            , 0,0,30);
    }
}
