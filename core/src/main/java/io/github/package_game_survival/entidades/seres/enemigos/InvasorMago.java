package io.github.package_game_survival.entidades.seres.enemigos;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.package_game_survival.entidades.objetos.Carne;
import io.github.package_game_survival.entidades.objetos.CarnePodrida;
import io.github.package_game_survival.entidades.objetos.PocionDeAmatista;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.habilidades.AtaqueAranazo;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class InvasorMago extends Enemigo {

    public InvasorMago(float x, float y) {
        super("Invasor Mago", x, y, 30, 40, 80, // 80 de vida (menos vida)
            100, 20, 20, Assets.get(PathManager.ENEMIGO_ATLAS, TextureAtlas.class));

        // AJUSTE DE IA: Ataca desde lejos
        this.rangoAtaque = 180f;
        this.habilidadPrincipal = new AtaqueAranazo(2.0f, 0.2f, 6, 180f, 30f, Jugador.class);

        this.agregarDrop(PocionDeAmatista.class, 1f);
    }
}
