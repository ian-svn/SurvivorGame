package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class CarnePodrida extends ObjetoConsumible {
    public CarnePodrida(float x, float y) {
        // Vida: -5, Hambre: 15
        // Bonos: Daño 0, Velocidad +20, MaxHP 0
        super("Carne Podrida", x, y, Assets.get(PathManager.CARNE_PODRIDA_TEXTURE, Texture.class),
            -5, 0, 0, 0, 5, 0);
    }
}
