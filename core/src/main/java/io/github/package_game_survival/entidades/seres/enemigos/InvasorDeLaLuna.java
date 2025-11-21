package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorDeLaLuna extends Enemigo {

    public InvasorDeLaLuna(float x, float y) {
        super("Invasor De La Luna", x, y, 30, 40, 100,
            100, 50, 20,
            Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class)
        );
    }
}
