package io.github.package_game_survival.entidades.bloques;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import io.github.package_game_survival.managers.Assets;
import io.github.package_game_survival.managers.PathManager;

public class BloqueDeBarro extends Bloque {

    public BloqueDeBarro(int x, int y) {
        super(x, y, "Bloque de barro");
        this.texture = Assets.get(PathManager.BLOCK_TEXTURE, Texture.class);
    }

    @Override
    public Rectangle getRectColision() {
        return new Rectangle(getX(), getY(), getAncho(), getAlto());
    }
}
