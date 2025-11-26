package io.github.package_game_survival.interfaces;

import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.entidades.seres.SerVivo;

public interface IAtaque {
    void update(float delta);

    /**
     * Intenta iniciar el ataque.
     * @return true si comenzó el casteo, false si estaba en enfriamiento.
     */
    boolean intentarAtacar(SerVivo atacante, Vector2 destino, IMundoJuego mundo);

    boolean estaCasteando();
}
