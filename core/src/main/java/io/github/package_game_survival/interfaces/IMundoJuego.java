package io.github.package_game_survival.interfaces;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.managers.GestorTiempo;

public interface IMundoJuego {
    void agregarActor(Actor actor);

    Array<Enemigo> getEnemigos();
    Array<Objeto> getObjetos();
    Array<Rectangle> getRectangulosNoTransitables();
    Array<Bloque> getBloques();

    Jugador getJugador();
    GestorTiempo getGestorTiempo();
    Stage getStageMundo();

    float getAncho();
    float getAlto();
}
