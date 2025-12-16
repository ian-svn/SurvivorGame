package io.github.package_game_survival.entidades.seres.jugadores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Guerrero extends Jugador {

    public Guerrero(float x, float y) {
        super("Guerrero", x, y, 120, 80, 105,
            Assets.get(PathManager.PLAYER_ATLAS, TextureAtlas.class));

        // BALANCE: Límite de daño 80
        this.limiteDanio = 100;

        this.habilidadPrincipal = new AtaqueAranazo(0.4f, 0.1f, getDanio(), 60f,
            40f, SerVivo.class, Color.BLUE);
    }
}
