package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class CarnePodrida extends ObjetoConsumible {
    public CarnePodrida(float x, float y) {
        super("Carne Podrida", x, y, Assets.get(PathManager.CARNE_PODRIDA_TEXTURE, Texture.class),
            -5,
            0, 3, 0,
            0f, 0f);
    }
}
