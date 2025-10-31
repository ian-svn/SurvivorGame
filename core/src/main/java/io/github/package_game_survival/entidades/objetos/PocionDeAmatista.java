package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class PocionDeAmatista extends ObjetoConsumible {

    public PocionDeAmatista(float x, float y) {
        super("Pocion de amatista", x, y, 40, 50,
            Assets.get(PathManager.POCION_TEXTURE,Texture.class), 25);
    }
}

