package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class PocionDeAmatista extends ObjetoConsumible {
    public PocionDeAmatista(float x, float y) {
        // Vida: 25
        // Bonos: MaxHP +20
        super("Pocion de amatista", x, y, Assets.get(PathManager.POCION_TEXTURE, Texture.class),
            25,
            0, 0, 5,
            0f, 0f);
    }
}
