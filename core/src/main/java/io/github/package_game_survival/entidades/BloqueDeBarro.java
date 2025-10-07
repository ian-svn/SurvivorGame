package io.github.package_game_survival.entidades;

import com.badlogic.gdx.graphics.Texture;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class BloqueDeBarro extends Bloque{

    public BloqueDeBarro(int x, int y) {
        super(x, y);
        this.texture = Assets.get(PathManager.BLOCK_TEXTURE, Texture.class);
    }


}
