package io.github.package_game_survival.entidades.objetos;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class Palo extends ObjetoColeccionable{
    public Palo(float x, float y) {
        super("Palo", x, y,Assets.get(PathManager.PALO_TEXTURE,Texture.class));
    }
}
