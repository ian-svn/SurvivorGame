package io.github.package_game_survival.interfaces;

import io.github.package_game_survival.entidades.seres.SerVivo;

public interface IEstrategiaMovimiento {
    void actualizar(SerVivo serVivo, float delta);
    boolean haTerminado(SerVivo serVivo);
}
