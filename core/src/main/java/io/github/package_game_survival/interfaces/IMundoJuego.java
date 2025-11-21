package io.github.package_game_survival.interfaces;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;

public interface IMundoJuego {
    Array<Bloque> getBloques();
    Array<Rectangle> getRectangulosNoTransitables();
    Array<Enemigo> getEnemigos();
    Array<Objeto> getObjetos();
    Jugador getJugador();

    void agregarActor(Actor actor);
    void agregarActorUI(Actor actor);
}
