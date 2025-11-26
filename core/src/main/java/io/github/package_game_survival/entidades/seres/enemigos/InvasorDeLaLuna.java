package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorDeLaLuna extends Enemigo {

    public InvasorDeLaLuna(float x, float y) {
        // Velocidad aumentada a 45 (Ágil pero no exagerado)
        super("Invasor De La Luna", x, y, 30, 40, 120,
            100, 45, 20,
            Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class)
        );

        // AJUSTE DE IA: Cuerpo a cuerpo (Melee)
        this.rangoAtaque = 45f;

        // AJUSTE DE ARMA:
        // Cooldown: 1.5s (Ataca seguido)
        // Casteo: 0.5s (Normal)
        // Daño: 12
        // Rango: 45f
        this.habilidadPrincipal = new AtaqueAranazo(1.5f, 0.5f, 12, 45f, 40f, Jugador.class);
    }
}
