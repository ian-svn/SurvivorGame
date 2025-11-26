package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorArquero extends Enemigo {

    public InvasorArquero(float x, float y) {
        // Velocidad reducida a 15 (Lento)
        super("Invasor Arquero", x, y, 30, 50, 100,
            100, 15, 20, Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class));

        // AJUSTE DE IA: Rango medio-largo
        this.rangoAtaque = 150f;

        // AJUSTE DE ARMA:
        // Cooldown: 3.5s
        // Casteo: 1.2s (Lento, tarda en tensar el arco)
        // Daño: 18 (Pega duro)
        // Rango: 150f
        this.habilidadPrincipal = new AtaqueAranazo(3.5f, 1.2f, 18, 150f, 20f, Jugador.class);
    }
}
