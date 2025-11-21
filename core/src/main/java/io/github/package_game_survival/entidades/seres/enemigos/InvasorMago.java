package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorMago extends Enemigo {

    public InvasorMago(float x, float y) {
        super("Invasor Mago", x, y, 30, 40, 100,
            100, 20, 20, Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class));
    }
}
