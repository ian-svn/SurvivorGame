package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorArquero extends Enemigo {

    public InvasorArquero(float x, float y) {
        super("Invasor Arquero", x, y, 30, 50, 100,
            100, 30, 20, Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class));
    }
}
