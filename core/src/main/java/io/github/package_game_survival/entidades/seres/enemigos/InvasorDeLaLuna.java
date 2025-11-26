package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.entidades.objetos.Carne;
import io.github.package_game_survival.entidades.objetos.CarnePodrida;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorDeLaLuna extends Enemigo {

    public InvasorDeLaLuna(float x, float y) {
        super("Invasor De La Luna", x, y, 30, 40, 120,
            100, 45, 20,
            Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class)
        );

        this.rangoAtaque = 45f;
        this.habilidadPrincipal = new AtaqueAranazo(1.5f, 0.5f, 12, 45f, 40f, Jugador.class);

        this.agregarDrop(CarnePodrida.class, 0.2f);
        this.agregarDrop(Carne.class, 1f);
    }
}
