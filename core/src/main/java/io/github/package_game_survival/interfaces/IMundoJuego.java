package io.github.package_game_survival.interfaces;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import io.github.package_game_survival.entidades.bloques.Bloque;
import io.github.package_game_survival.entidades.objetos.Objeto;
// IMPORTANTE: Importar Animal
import io.github.package_game_survival.entidades.seres.animales.Animal;
import io.github.package_game_survival.entidades.seres.enemigos.Enemigo;
import io.github.package_game_survival.entidades.seres.jugadores.Jugador;

public interface IMundoJuego {
    void agregarActor(Actor actor);
    void agregarActorUI(Actor actor);

    Jugador getJugador();
    Array<Enemigo> getEnemigos();
    Array<Bloque> getBloques();
    Array<Objeto> getObjetos();
    Array<Rectangle> getRectangulosNoTransitables();
    Array<Animal> getAnimales();
}
