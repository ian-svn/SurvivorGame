package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Carne extends ObjetoConsumible {
    public Carne(float x, float y) {
        super("Carne", x, y, Assets.get(PathManager.CARNE_TEXTURE, Texture.class),
            10, 30, 5);
    }
}
