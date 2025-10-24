package io.github.package_game_survival.interfaces;

import io.github.package_game_survival.entidades.Personaje;

public interface IEstrategiaMovimiento {
    void actualizar(Personaje personaje, float delta);
    boolean haTerminado(Personaje personaje);
}
