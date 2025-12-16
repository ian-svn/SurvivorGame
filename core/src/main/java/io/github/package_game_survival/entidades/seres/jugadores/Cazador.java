package io.github.package_game_survival.entidades.seres.jugadores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.entidades.seres.SerVivo;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Cazador extends Jugador {

    public Cazador(float x, float y) {
        super("Cazador", x, y, 100, 100, 15,
            Assets.get(PathManager.CAZADOR_ATLAS, TextureAtlas.class));

        // BALANCE: Límite de daño 60
        this.limiteDanio = 65;

        this.habilidadPrincipal = new AtaqueAranazo(0.6f, 0.1f, getDanio(), 100f,
            10f, SerVivo.class, Color.GREEN);
    }
}
