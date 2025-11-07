package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Piedra extends ObjetoColeccionable{
    public Piedra(float x, float y) {
        super("Piedra", x, y, Assets.get(PathManager.PIEDRA_TEXTURE,Texture.class));
    }
}
