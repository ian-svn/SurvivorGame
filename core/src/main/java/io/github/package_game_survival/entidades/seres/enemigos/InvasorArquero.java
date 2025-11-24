package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.algoritmos.EstrategiaMoverAPunto;
import io.github.package_game_survival.entidades.mapas.Escenario;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorArquero extends Enemigo {

    public InvasorArquero(float x, float y) {
        super("Invasor Arquero", x, y, 30, 50, 100,
            100, 30, 20, Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class),
            null, null);
    }
}

