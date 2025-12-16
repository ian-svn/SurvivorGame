package io.github.package_game_survival.interfaces;

import com.badlogic.gdx.math.Vector2;
import io.github.package_game_survival.entidades.seres.SerVivo;

public interface IAtaque {
    void intentarAtacar(SerVivo atacante, Vector2 destino, IMundoJuego mundo);
    void update(float delta);
    boolean estaCasteando();
    boolean isEnCooldown();
}
