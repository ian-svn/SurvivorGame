package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class CarnePodrida extends ObjetoConsumible {

    public CarnePodrida(float x, float y) {
        // Vida: -5, Hambre: 15, Sed: 0
        // Usamos la textura real CARNE_PODRIDA_TEXTURE
        super("Carne Podrida", x, y, Assets.get(PathManager.CARNE_PODRIDA_TEXTURE, Texture.class),
            -5, 15, 0);
    }

    // Eliminamos el método draw() porque ya tenemos la textura correcta
}
