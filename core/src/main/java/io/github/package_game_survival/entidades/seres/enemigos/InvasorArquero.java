package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.entidades.objetos.Carne;
import io.github.package_game_survival.entidades.objetos.CarnePodrida;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorArquero extends Enemigo {

    public InvasorArquero(float x, float y) {
        // BALANCE:
        // Vida: 180 (Antes menos)
        // Velocidad: 135
        // Daño: 20
        super("Invasor Arquero", x, y, 30, 50, 180,
            70, 35, 15, Assets.get(PathManager.ENEMIGO_ARQUERO_ATLAS, TextureAtlas.class));

        // Rango de visión/ataque aumentado a 150
        this.rangoAtaque = 150f;

        // Habilidad mejorada
        this.habilidadPrincipal = new AtaqueAranazo(2f, 1f, 20, 180f,
            15f, Jugador.class, Color.GREEN);

        this.agregarDrop(CarnePodrida.class, 0.75f);
        this.agregarDrop(Carne.class, 0.25f);
    }
}
