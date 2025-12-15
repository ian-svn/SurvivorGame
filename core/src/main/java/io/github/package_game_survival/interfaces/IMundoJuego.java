package io.github.package_game_survival.interfaces;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.objetos.Objeto;
import io.github.package_game_survival.entidades.seres.animales.Animal;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;
import io.github.package_game_survival.managers.GestorTiempo;
import com.badlogic.gdx.scenes.scene2d.Actor;

public interface IMundoJuego {
    Array<Bloque> getBloques();
    Array<Rectangle> getRectangulosNoTransitables();
    Array<Enemigo> getEnemigos();
    Array<Animal> getAnimales();
    Array<Objeto> getObjetos();
    Jugador getJugador();

    void agregarActor(Actor actor);
    void agregarActorUI(Actor actor);
    GestorTiempo getGestorTiempo();

    float getAncho();
    float getAlto();
}
