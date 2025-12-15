package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import io.github.package_game_survival.interfaces.IMundoJuego;

public class BloqueAnimado extends Bloque {

    public BloqueAnimado(float x, float y, String tipo) {
        super(x, y, tipo);
    }
    public static BloqueAnimado verificarCreacion(TiledMapTile tile, float x, float y) {
        return null;
    }

    @Override
    public void agregarAlMundo(IMundoJuego mundo) {
        mundo.agregarActor(this);
    }
}
