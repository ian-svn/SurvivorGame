package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Carne extends ObjetoConsumible {
    public Carne(float x, float y) {
        // Vida: 10, Hambre: 30, Sed: 5
        // Bonos: Daño +5, Velocidad +10, MaxHP +10
        super("Carne", x, y, Assets.get(PathManager.CARNE_TEXTURE, Texture.class),
            10, 0, 0, 3, 0, 0);
    }
}
