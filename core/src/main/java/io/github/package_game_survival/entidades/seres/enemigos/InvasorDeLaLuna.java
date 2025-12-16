package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.entidades.objetos.Carne;
import io.github.package_game_survival.entidades.objetos.CarnePodrida;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorDeLaLuna extends Enemigo {

    public InvasorDeLaLuna(float x, float y) {
        super("Invasor De La Luna", x, y, 30, 40, 140,
            80, 45, 20,
            Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class)
        );

        this.rangoAtaque = 65f;
        this.habilidadPrincipal = new AtaqueAranazo(1f, 0.8f, 30, 65f,
            40f, Jugador.class, Color.RED);

        this.agregarDrop(CarnePodrida.class, 0.75f);
        this.agregarDrop(Carne.class, 0.25f);
    }
}
