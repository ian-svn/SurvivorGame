package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Lana extends ObjetoConsumible {
    public Lana(float x, float y) {
        super("Carne", x, y, Assets.get(PathManager.LANA_TEXTURE, Texture.class),
            0, 0, 5, 2, 0, 2);
    }
}
